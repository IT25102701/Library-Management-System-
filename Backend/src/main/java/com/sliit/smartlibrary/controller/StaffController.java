package com.sliit.smartlibrary.controller;
import com.sliit.smartlibrary.dto.*;
import com.sliit.smartlibrary.entity.*;
import com.sliit.smartlibrary.repository.*;
import com.sliit.smartlibrary.service.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/staff")
public class StaffController {
 private final CategoryRepository categories;private final AuthorRepository authors;private final PublisherRepository publishers;private final BookService books;private final AssetService assets;private final ReservationService reservations;private final BorrowService borrows;private final FineService fines;private final FeedbackService feedback;private final UserService users;private final AcademicResourceService academicResources;private final InventoryService inventory;
 public StaffController(CategoryRepository categories,AuthorRepository authors,PublisherRepository publishers,BookService books,AssetService assets,ReservationService reservations,BorrowService borrows,FineService fines,FeedbackService feedback,UserService users,AcademicResourceService academicResources,InventoryService inventory){this.categories=categories;this.authors=authors;this.publishers=publishers;this.books=books;this.assets=assets;this.reservations=reservations;this.borrows=borrows;this.fines=fines;this.feedback=feedback;this.users=users;this.academicResources=academicResources;this.inventory=inventory;}
 private User me(Authentication a){return users.getByEmail(a.getName());}
 @GetMapping("/categories") public Object categories(){return categories.findAll();}
 @PostMapping("/categories") public Category addCategory(@RequestBody Category c){c.setId(null);return categories.save(c);}
 @PutMapping("/categories/{id}") public Category updateCategory(@PathVariable Long id,@RequestBody Category c){Category x=categories.findById(id).orElseThrow();x.setName(c.getName());x.setDescription(c.getDescription());return categories.save(x);}
 @DeleteMapping("/categories/{id}") public void deleteCategory(@PathVariable Long id){categories.deleteById(id);}
 @GetMapping("/authors") public Object authors(){return authors.findAll();}
 @PostMapping("/authors") public Author addAuthor(@RequestBody Author a){a.setId(null);return authors.save(a);}
 @PutMapping("/authors/{id}") public Author updateAuthor(@PathVariable Long id,@RequestBody Author a){Author x=authors.findById(id).orElseThrow();x.setName(a.getName());x.setBiography(a.getBiography());return authors.save(x);}
 @DeleteMapping("/authors/{id}") public void deleteAuthor(@PathVariable Long id){authors.deleteById(id);}
 @GetMapping("/publishers") public Object publishers(){return publishers.findAll();}
 @PostMapping("/publishers") public Publisher addPublisher(@RequestBody Publisher p){p.setId(null);return publishers.save(p);}
 @PutMapping("/publishers/{id}") public Publisher updatePublisher(@PathVariable Long id,@RequestBody Publisher p){Publisher x=publishers.findById(id).orElseThrow();x.setName(p.getName());x.setEmail(p.getEmail());return publishers.save(x);}
 @DeleteMapping("/publishers/{id}") public void deletePublisher(@PathVariable Long id){publishers.deleteById(id);}
 @GetMapping("/books") public Object books(){return books.search(null,null);}
 @PostMapping("/books") public Object addBook(@RequestBody BookRequest r){return books.create(r);}
 @PutMapping("/books/{id}") public Object updateBook(@PathVariable Long id,@RequestBody BookRequest r){return books.update(id,r);}
 @DeleteMapping("/books/{id}") public void deleteBook(@PathVariable Long id){books.delete(id);}
 @GetMapping("/copies") public Object copies(){return assets.getAll();}
 @PostMapping("/copies") public Object addCopy(@RequestBody BookCopyRequest r){return assets.create(r);}
 @PutMapping("/copies/{id}") public Object updateCopy(@PathVariable Long id,@RequestBody BookCopyRequest r){return assets.update(id,r);}
 @DeleteMapping("/copies/{id}") public void deleteCopy(@PathVariable Long id){assets.delete(id);}
 @GetMapping("/inventory/summary") public Object inventorySummary(){return inventory.summary();}
 @GetMapping("/inventory/audits") public Object inventoryAudits(){return inventory.getAudits();}
 @PostMapping("/inventory/audits") public Object recordInventoryAudit(Authentication a,@RequestBody InventoryAuditRequest r){return inventory.recordAudit(r,a.getName());}
 @DeleteMapping("/inventory/audits/{id}") public void deleteInventoryAudit(@PathVariable Long id){inventory.deleteAudit(id);}
 @GetMapping("/reservations") public Object reservations(){return reservations.getAll();}
 @PutMapping("/reservations/{id}/status") public Object reservationStatus(@PathVariable Long id,@RequestBody Map<String,String> b){return reservations.updateStatus(id,b.get("status"));}
 @GetMapping("/borrowings") public Object borrowings(){return borrows.getAll();}
 @PostMapping("/borrowings") public Object issue(Authentication a,@RequestBody BorrowRequest r){return borrows.issue(r.getMemberId(),r.getBookCopyId(),me(a).getId());}
 @PutMapping("/borrowings/{id}/renew") public Object renew(@PathVariable Long id){return borrows.renew(id);}
 @PutMapping("/borrowings/{id}/return") public Object returnBook(@PathVariable Long id){return borrows.returnBook(id);}
 @PutMapping("/borrowings/{id}/cancel") public Object cancelBorrow(@PathVariable Long id){return borrows.cancel(id);}
 @GetMapping("/fines") public Object fines(){return fines.getAll();}
 @PutMapping("/fines/{id}/status") public Object fineStatus(@PathVariable Long id,@RequestBody Map<String,String> b){return fines.updateStatus(id,b.get("status"));}
 @DeleteMapping("/fines/{id}") public void deleteFine(@PathVariable Long id){fines.delete(id);}
 @GetMapping("/feedback") public Object feedback(){return feedback.getAll();}
 @PutMapping("/feedback/{id}/status") public Object feedbackStatus(@PathVariable Long id,@RequestBody Map<String,String> b){return feedback.updateStatus(id,b.get("status"));}
 @DeleteMapping("/feedback/{id}") public void deleteFeedback(@PathVariable Long id){feedback.delete(id);}
 @GetMapping("/academic-resources") public Object academicResources(@RequestParam(required=false) String q){return academicResources.search(q);}
 @PostMapping("/academic-resources") public Object addAcademicResource(@RequestBody AcademicResourceRequest r){return academicResources.create(r);}
 @PutMapping("/academic-resources/{id}") public Object updateAcademicResource(@PathVariable Long id,@RequestBody AcademicResourceRequest r){return academicResources.update(id,r);}
 @DeleteMapping("/academic-resources/{id}") public void deleteAcademicResource(@PathVariable Long id){academicResources.delete(id);}

 @GetMapping("/members") public Object members(){return users.getAll().stream().filter(u->u.getRole().name().equals("MEMBER")).toList();}
}
