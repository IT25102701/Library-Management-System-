package com.sliit.smartlibrary.service.impl;
import com.sliit.smartlibrary.dto.RegisterRequest;
import com.sliit.smartlibrary.dto.AdminUserRequest;
import com.sliit.smartlibrary.entity.User;
import com.sliit.smartlibrary.enums.*;
import com.sliit.smartlibrary.exception.*;
import com.sliit.smartlibrary.repository.UserRepository;
import com.sliit.smartlibrary.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;
@Service
public class UserServiceImpl implements UserService {
 private final UserRepository users; private final PasswordEncoder encoder;
 public UserServiceImpl(UserRepository users,PasswordEncoder encoder){this.users=users;this.encoder=encoder;}
 public User register(RegisterRequest r){
  if(r.getName()==null||r.getName().isBlank())throw new BadRequestException("Name is required.");
  if(r.getEmail()==null||r.getEmail().isBlank()||!r.getEmail().contains("@"))throw new BadRequestException("Enter a valid email address.");
  if(users.existsByEmailIgnoreCase(r.getEmail()))throw new BadRequestException("An account with this email already exists.");
  if(r.getPassword()==null||r.getPassword().length()<6)throw new BadRequestException("Password must contain at least 6 characters.");
  User u=new User();u.setName(r.getName().trim());u.setEmail(r.getEmail().trim().toLowerCase());u.setPassword(encoder.encode(r.getPassword()));u.setPhone(r.getPhone());u.setRole(Role.MEMBER);u.setStatus(AccountStatus.ACTIVE);return users.save(u);
 }
 public User createByAdmin(AdminUserRequest r){
  if(r.getName()==null||r.getName().isBlank())throw new BadRequestException("Name is required.");
  if(r.getEmail()==null||r.getEmail().isBlank()||!r.getEmail().contains("@"))throw new BadRequestException("Enter a valid email address.");
  if(users.existsByEmailIgnoreCase(r.getEmail()))throw new BadRequestException("An account with this email already exists.");
  if(r.getPassword()==null||r.getPassword().length()<6)throw new BadRequestException("Password must contain at least 6 characters.");
  Role role=Role.MEMBER; AccountStatus status=AccountStatus.ACTIVE;
  try{if(r.getRole()!=null&&!r.getRole().isBlank())role=Role.valueOf(r.getRole().toUpperCase());if(r.getStatus()!=null&&!r.getStatus().isBlank())status=AccountStatus.valueOf(r.getStatus().toUpperCase());}
  catch(IllegalArgumentException e){throw new BadRequestException("Invalid role or account status.");}
  User u=new User();u.setName(r.getName().trim());u.setEmail(r.getEmail().trim().toLowerCase());u.setPassword(encoder.encode(r.getPassword()));u.setPhone(r.getPhone());u.setRole(role);u.setStatus(status);return users.save(u);
 }
 public User getById(Long id){return users.findById(id).orElseThrow(()->new ResourceNotFoundException("User not found: "+id));}
 public User getByEmail(String email){return users.findByEmailIgnoreCase(email).orElseThrow(()->new ResourceNotFoundException("User not found."));}
 public List<User> getAll(){return users.findAll();}
 public User update(Long id,User in){User u=getById(id);if(in.getName()!=null&&!in.getName().isBlank())u.setName(in.getName().trim());if(in.getPhone()!=null)u.setPhone(in.getPhone());if(in.getRole()!=null)u.setRole(in.getRole());if(in.getStatus()!=null)u.setStatus(in.getStatus());return users.save(u);}
 public User updateProfile(Long id,String name,String phone){User u=getById(id);if(name==null||name.isBlank())throw new BadRequestException("Name is required.");u.setName(name.trim());u.setPhone(phone);return users.save(u);}
 public void changePassword(Long id,String currentPassword,String newPassword){User u=getById(id);if(currentPassword==null||!encoder.matches(currentPassword,u.getPassword()))throw new BadRequestException("Current password is incorrect.");if(newPassword==null||newPassword.length()<6)throw new BadRequestException("New password must contain at least 6 characters.");u.setPassword(encoder.encode(newPassword));users.save(u);}
 public void delete(Long id){users.delete(getById(id));}
}
