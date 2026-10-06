package com.sliit.smartlibrary.controller;

import com.sliit.smartlibrary.dto.*;
import com.sliit.smartlibrary.entity.User;
import com.sliit.smartlibrary.service.UserService;
import com.sliit.smartlibrary.service.ActivityLogService;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.*;
import org.springframework.security.core.context.*;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/auth")
public class AuthController {
 private final AuthenticationManager manager; private final SecurityContextRepository contextRepo; private final UserService users; private final ActivityLogService logs;
 public AuthController(AuthenticationManager manager,SecurityContextRepository contextRepo,UserService users,ActivityLogService logs){this.manager=manager;this.contextRepo=contextRepo;this.users=users;this.logs=logs;}
 @PostMapping("/login") public User login(@RequestBody LoginRequest r,HttpServletRequest req,HttpServletResponse res){Authentication a=manager.authenticate(new UsernamePasswordAuthenticationToken(r.getEmail(),r.getPassword()));SecurityContext c=SecurityContextHolder.createEmptyContext();c.setAuthentication(a);SecurityContextHolder.setContext(c);contextRepo.saveContext(c,req,res);logs.log(a.getName(),"LOGIN","User logged in successfully.");return users.getByEmail(a.getName());}
 @PostMapping("/register") public User register(@RequestBody RegisterRequest r){User u=users.register(r);logs.log(u.getEmail(),"REGISTER","New library member account created.");return u;}
 @GetMapping("/me") public User me(Authentication a){return a==null?null:users.getByEmail(a.getName());}
 @PostMapping("/logout") public void logout(Authentication a,HttpServletRequest req){if(a!=null)logs.log(a.getName(),"LOGOUT","User logged out.");SecurityContextHolder.clearContext();HttpSession s=req.getSession(false);if(s!=null)s.invalidate();}
}
