package com.sliit.smartlibrary.controller;
import com.sliit.smartlibrary.entity.User;
import com.sliit.smartlibrary.dto.AdminUserRequest;
import com.sliit.smartlibrary.repository.*;
import com.sliit.smartlibrary.service.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/admin")
public class AdminController {
 private final UserService users;private final ActivityLogService logs;private final BookRepository books;private final BookCopyRepository copies;private final ReservationRepository reservations;private final BorrowRecordRepository borrows;private final FineRepository fines;private final FeedbackRepository feedback;private final AcademicResourceRepository academicResources;
 public AdminController(UserService users,ActivityLogService logs,BookRepository books,BookCopyRepository copies,ReservationRepository reservations,BorrowRecordRepository borrows,FineRepository fines,FeedbackRepository feedback,AcademicResourceRepository academicResources){this.users=users;this.logs=logs;this.books=books;this.copies=copies;this.reservations=reservations;this.borrows=borrows;this.fines=fines;this.feedback=feedback;this.academicResources=academicResources;}
 @GetMapping("/users") public Object users(){return users.getAll();}
 @PostMapping("/users") public Object addUser(@RequestBody AdminUserRequest r){User created=users.createByAdmin(r);logs.log("ADMIN","USER_CREATE","Created "+created.getRole()+" user "+created.getEmail());return created;}
 @PutMapping("/users/{id}") public Object update(@PathVariable Long id,@RequestBody User u){User updated=users.update(id,u);logs.log("ADMIN","USER_UPDATE","Updated user #"+id);return updated;}
 @DeleteMapping("/users/{id}") public void delete(@PathVariable Long id){users.delete(id);logs.log("ADMIN","USER_DELETE","Deleted user #"+id);}
 @GetMapping("/activity-logs") public Object activityLogs(){return logs.recent();}
 @GetMapping("/summary") public Map<String,Long> summary(){Map<String,Long>m=new LinkedHashMap<>();m.put("users",(long)users.getAll().size());m.put("books",books.count());m.put("copies",copies.count());m.put("reservations",reservations.count());m.put("borrowings",borrows.count());m.put("fines",fines.count());m.put("feedback",feedback.count());m.put("academicResources",academicResources.count());return m;}
}
