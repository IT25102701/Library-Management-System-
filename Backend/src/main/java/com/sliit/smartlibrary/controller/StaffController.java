// This file belongs to the package com.sliit.smartlibrary.controller (its folder in the project).
package com.sliit.smartlibrary.controller;

// Imports every class in the project's dto package.
import com.sliit.smartlibrary.dto.*;
// Imports every class in the project's entity package.
import com.sliit.smartlibrary.entity.*;
// Imports the BadRequestException exception.
import com.sliit.smartlibrary.exception.BadRequestException;
// Imports the ResourceNotFoundException exception.
import com.sliit.smartlibrary.exception.ResourceNotFoundException;
// Imports every class in the project's repository package.
import com.sliit.smartlibrary.repository.*;
// Imports every class in the project's service package.
import com.sliit.smartlibrary.service.*;
// Imports the shared validation class ValidationRules.
import com.sliit.smartlibrary.validation.ValidationRules;
// Imports the @Valid annotation, which tells Spring to check a request body against its validation rules.
import jakarta.validation.Valid;
// Imports Java's collection classes (List, Map, Optional, ...).
import java.util.*;
// Imports Authentication, which describes the currently logged-in user.
import org.springframework.security.core.Authentication;
// Imports Spring web annotations (@RestController, @GetMapping, @RequestBody, ...).
import org.springframework.web.bind.annotation.*;

// Marks this class as a REST controller: its methods answer HTTP requests with JSON.
@RestController
// Every URL in this controller starts with "/api/staff".
@RequestMapping("/api/staff")
// Class StaffController.
public class StaffController {

    // Repository used to read and save category records (given to this class by Spring).
    private final CategoryRepository categories;
    // Repository used to read and save author records (given to this class by Spring).
    private final AuthorRepository authors;
    // Repository used to read and save publisher records (given to this class by Spring).
    private final PublisherRepository publishers;
    // The book service this class depends on (given to this class by Spring).
    private final BookService books;
    // The asset service this class depends on (given to this class by Spring).
    private final AssetService assets;
    // The reservation service this class depends on (given to this class by Spring).
    private final ReservationService reservations;
    // The borrow service this class depends on (given to this class by Spring).
    private final BorrowService borrows;
    // The fine service this class depends on (given to this class by Spring).
    private final FineService fines;
    // The feedback service this class depends on (given to this class by Spring).
    private final FeedbackService feedback;
    // The user service this class depends on (given to this class by Spring).
    private final UserService users;
    // The academic resource service this class depends on (given to this class by Spring).
    private final AcademicResourceService academicResources;
    // The inventory service this class depends on (given to this class by Spring).
    private final InventoryService inventory;
    // Repository used to read and save book records (given to this class by Spring).
    private final BookRepository bookRecords;

    // Constructor: Spring passes in the dependencies listed below (dependency injection).
    public StaffController(
        // the category repository,
        CategoryRepository categories,
        // the author repository,
        AuthorRepository authors,
        // the publisher repository,
        PublisherRepository publishers,
        // the book service,
        BookService books,
        // the asset (copy) service,
        AssetService assets,
        // the reservation service,
        ReservationService reservations,
        // the borrow service,
        BorrowService borrows,
        // the fine service,
        FineService fines,
        // the feedback service,
        FeedbackService feedback,
        // the user service,
        UserService users,
        // the academic-resource service,
        AcademicResourceService academicResources,
        // the inventory service,
        InventoryService inventory,
        // and the book repository (to block deleting categories/authors/publishers still used by books).
        BookRepository bookRecords
    // End of the parameter list; the constructor body starts here.
    ) {
        // Stores the given categories in this object's field.
        this.categories = categories;
        // Stores the given authors in this object's field.
        this.authors = authors;
        // Stores the given publishers in this object's field.
        this.publishers = publishers;
        // Stores the given books in this object's field.
        this.books = books;
        // Stores the given assets in this object's field.
        this.assets = assets;
        // Stores the given reservations in this object's field.
        this.reservations = reservations;
        // Stores the given borrows in this object's field.
        this.borrows = borrows;
        // Stores the given fines in this object's field.
        this.fines = fines;
        // Stores the given feedback in this object's field.
        this.feedback = feedback;
        // Stores the given users in this object's field.
        this.users = users;
        // Stores the given academic resources in this object's field.
        this.academicResources = academicResources;
        // Stores the given inventory in this object's field.
        this.inventory = inventory;
        // Stores the given book records in this object's field.
        this.bookRecords = bookRecords;
    } // end of block

    // Helper: loads the account of the staff member who is logged in.
    private User me(Authentication a) {
        // Look the user up by the email Spring Security stored at login.
        return users.getByEmail(a.getName());
    } // end of me()

    // Handles HTTP GET requests to "/categories" (reading data).
    @GetMapping("/categories")
    // GET /api/staff/categories: returns every category.
    public Object categories() {
        // Load all category records.
        return categories.findAll();
    } // end of categories()

    // Handles HTTP POST requests to "/categories" (creating something).
    @PostMapping("/categories")
    // POST /api/staff/categories: adds a new category; the body is checked first (@Valid).
    public Category addCategory(@Valid @RequestBody CategoryRequest r) {
        // Clean the name: remove spaces at both ends and squeeze repeated spaces into one.
        String name = r.getName().trim().replaceAll("\\s+", " ");
        // If a category with this name already exists (ignoring case), reject...
        if (categories.existsByNameIgnoreCase(name)) throw new BadRequestException(
            // ...with this message (HTTP 400).
            "A category with this name already exists."
        ); // end of the argument list
        // Create a new category object.
        Category x = new Category();
        // Set its name.
        x.setName(name);
        // Set its description (empty text becomes null).
        x.setDescription(ValidationRules.clean(r.getDescription()));
        // Save it and return it.
        return categories.save(x);
    } // end of addCategory()

    // Handles HTTP PUT requests to "/categories/{id}" (updating something).
    @PutMapping("/categories/{id}")
    // PUT /api/staff/categories/{id}: edits a category; the body is checked first (@Valid).
    public Category updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest r) {
        // Load the category...
        Category x = categories
            // ...by id...
            .findById(id)
            // ...or stop with "not found" (HTTP 404).
            .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
        // Clean the name: trim it and squeeze repeated spaces.
        String name = r.getName().trim().replaceAll("\\s+", " ");
        // If a different category already has this name (ignoring case), reject...
        if (categories.existsByNameIgnoreCaseAndIdNot(name, id)) throw new BadRequestException(
            // ...with this message (HTTP 400).
            "A category with this name already exists."
        ); // end of the argument list
        // Set the new name.
        x.setName(name);
        // Set the new description (empty text becomes null).
        x.setDescription(ValidationRules.clean(r.getDescription()));
        // Save the change and return it.
        return categories.save(x);
    } // end of updateCategory()

    // Handles HTTP DELETE requests to "/categories/{id}" (removing something).
    @DeleteMapping("/categories/{id}")
    // DELETE /api/staff/categories/{id}: deletes a category.
    public void deleteCategory(@PathVariable Long id) {
        // If there is no record with this id, stop with "not found" (HTTP 404).
        if (!categories.existsById(id)) throw new ResourceNotFoundException("Category not found: " + id);
        // If any book still uses this category, refuse...
        if (bookRecords.existsByCategoryId(id)) throw new BadRequestException(
            // ...with this message (HTTP 400).
            "This category is still used by books in the catalogue. Reassign or delete those books first."
        ); // end of the argument list
        // Safe to delete: remove it.
        categories.deleteById(id);
    } // end of deleteCategory()

    // Handles HTTP GET requests to "/authors" (reading data).
    @GetMapping("/authors")
    // GET /api/staff/authors: returns every author.
    public Object authors() {
        // Load all author records.
        return authors.findAll();
    } // end of authors()

    // Handles HTTP POST requests to "/authors" (creating something).
    @PostMapping("/authors")
    // POST /api/staff/authors: adds a new author; the body is checked first (@Valid).
    public Author addAuthor(@Valid @RequestBody AuthorRequest r) {
        // Clean the name: remove spaces at both ends and squeeze repeated spaces into one.
        String name = r.getName().trim().replaceAll("\\s+", " ");
        // If a author with this name already exists (ignoring case), reject...
        if (authors.existsByNameIgnoreCase(name)) throw new BadRequestException(
            // ...with this message (HTTP 400).
            "An author with this name already exists."
        ); // end of the argument list
        // Create a new author object.
        Author x = new Author();
        // Set its name.
        x.setName(name);
        // Set its biography (empty text becomes null).
        x.setBiography(ValidationRules.clean(r.getBiography()));
        // Save it and return it.
        return authors.save(x);
    } // end of addAuthor()

    // Handles HTTP PUT requests to "/authors/{id}" (updating something).
    @PutMapping("/authors/{id}")
    // PUT /api/staff/authors/{id}: edits a author; the body is checked first (@Valid).
    public Author updateAuthor(@PathVariable Long id, @Valid @RequestBody AuthorRequest r) {
        // Load the author...
        Author x = authors
            // ...by id...
            .findById(id)
            // ...or stop with "not found" (HTTP 404).
            .orElseThrow(() -> new ResourceNotFoundException("Author not found: " + id));
        // Clean the name: trim it and squeeze repeated spaces.
        String name = r.getName().trim().replaceAll("\\s+", " ");
        // If a different author already has this name (ignoring case), reject...
        if (authors.existsByNameIgnoreCaseAndIdNot(name, id)) throw new BadRequestException(
            // ...with this message (HTTP 400).
            "An author with this name already exists."
        ); // end of the argument list
        // Set the new name.
        x.setName(name);
        // Set the new biography (empty text becomes null).
        x.setBiography(ValidationRules.clean(r.getBiography()));
        // Save the change and return it.
        return authors.save(x);
    } // end of updateAuthor()

    // Handles HTTP DELETE requests to "/authors/{id}" (removing something).
    @DeleteMapping("/authors/{id}")
    // DELETE /api/staff/authors/{id}: deletes a author.
    public void deleteAuthor(@PathVariable Long id) {
        // If there is no record with this id, stop with "not found" (HTTP 404).
        if (!authors.existsById(id)) throw new ResourceNotFoundException("Author not found: " + id);
        // If any book still uses this author, refuse...
        if (bookRecords.existsByAuthorId(id)) throw new BadRequestException(
            // ...with this message (HTTP 400).
            "This author is still used by books in the catalogue. Reassign or delete those books first."
        ); // end of the argument list
        // Safe to delete: remove it.
        authors.deleteById(id);
    } // end of deleteAuthor()

    // Handles HTTP GET requests to "/publishers" (reading data).
    @GetMapping("/publishers")
    // GET /api/staff/publishers: returns every publisher.
    public Object publishers() {
        // Load all publisher records.
        return publishers.findAll();
    } // end of publishers()

    // Handles HTTP POST requests to "/publishers" (creating something).
    @PostMapping("/publishers")
    // POST /api/staff/publishers: adds a new publisher; the body is checked first (@Valid).
    public Publisher addPublisher(@Valid @RequestBody PublisherRequest r) {
        // Clean the name: remove spaces at both ends and squeeze repeated spaces into one.
        String name = r.getName().trim().replaceAll("\\s+", " ");
        // If a publisher with this name already exists (ignoring case), reject...
        if (publishers.existsByNameIgnoreCase(name)) throw new BadRequestException(
            // ...with this message (HTTP 400).
            "A publisher with this name already exists."
        ); // end of the argument list
        // Create a new publisher object.
        Publisher x = new Publisher();
        // Set its name.
        x.setName(name);
        // Set its email (empty text becomes null).
        x.setEmail(ValidationRules.clean(r.getEmail()));
        // Save it and return it.
        return publishers.save(x);
    } // end of addPublisher()

    // Handles HTTP PUT requests to "/publishers/{id}" (updating something).
    @PutMapping("/publishers/{id}")
    // PUT /api/staff/publishers/{id}: edits a publisher; the body is checked first (@Valid).
    public Publisher updatePublisher(@PathVariable Long id, @Valid @RequestBody PublisherRequest r) {
        // Load the publisher...
        Publisher x = publishers
            // ...by id...
            .findById(id)
            // ...or stop with "not found" (HTTP 404).
            .orElseThrow(() -> new ResourceNotFoundException("Publisher not found: " + id));
        // Clean the name: trim it and squeeze repeated spaces.
        String name = r.getName().trim().replaceAll("\\s+", " ");
        // If a different publisher already has this name (ignoring case), reject...
        if (publishers.existsByNameIgnoreCaseAndIdNot(name, id)) throw new BadRequestException(
            // ...with this message (HTTP 400).
            "A publisher with this name already exists."
        ); // end of the argument list
        // Set the new name.
        x.setName(name);
        // Set the new email (empty text becomes null).
        x.setEmail(ValidationRules.clean(r.getEmail()));
        // Save the change and return it.
        return publishers.save(x);
    } // end of updatePublisher()

    // Handles HTTP DELETE requests to "/publishers/{id}" (removing something).
    @DeleteMapping("/publishers/{id}")
    // DELETE /api/staff/publishers/{id}: deletes a publisher.
    public void deletePublisher(@PathVariable Long id) {
        // If there is no record with this id, stop with "not found" (HTTP 404).
        if (!publishers.existsById(id)) throw new ResourceNotFoundException("Publisher not found: " + id);
        // If any book still uses this publisher, refuse...
        if (bookRecords.existsByPublisherId(id)) throw new BadRequestException(
            // ...with this message (HTTP 400).
            "This publisher is still used by books in the catalogue. Reassign or delete those books first."
        ); // end of the argument list
        // Safe to delete: remove it.
        publishers.deleteById(id);
    } // end of deletePublisher()

    // Handles HTTP GET requests to "/books" (reading data).
    @GetMapping("/books")
    // GET /api/staff/books: returns every book.
    public Object books() {
        // Search with no text and no category, which returns all books.
        return books.search(null, null);
    } // end of books()

    // Handles HTTP POST requests to "/books" (creating something).
    @PostMapping("/books")
    // POST /api/staff/books: adds a book; the body is checked first (@Valid).
    public Object addBook(@Valid @RequestBody BookRequest r) {
        // Create it (the service checks ISBN, year, category and author).
        return books.create(r);
    } // end of addBook()

    // Handles HTTP PUT requests to "/books/{id}" (updating something).
    @PutMapping("/books/{id}")
    // PUT /api/staff/books/{id}: edits a book; the body is checked first (@Valid).
    public Object updateBook(@PathVariable Long id, @Valid @RequestBody BookRequest r) {
        // Apply the change (with the same checks).
        return books.update(id, r);
    } // end of updateBook()

    // Handles HTTP DELETE requests to "/books/{id}" (removing something).
    @DeleteMapping("/books/{id}")
    // DELETE /api/staff/books/{id}: deletes a book.
    public void deleteBook(@PathVariable Long id) {
        // Delete it (the service refuses if copies or active reservations exist).
        books.delete(id);
    } // end of deleteBook()

    // Handles HTTP GET requests to "/copies" (reading data).
    @GetMapping("/copies")
    // GET /api/staff/copies: returns every physical copy.
    public Object copies() {
        // Load all copies.
        return assets.getAll();
    } // end of copies()

    // Handles HTTP POST requests to "/copies" (creating something).
    @PostMapping("/copies")
    // POST /api/staff/copies: adds a physical copy; the body is checked first (@Valid).
    public Object addCopy(@Valid @RequestBody BookCopyRequest r) {
        // Create it (the service checks barcode/QR uniqueness and condition/status).
        return assets.create(r);
    } // end of addCopy()

    // Handles HTTP PUT requests to "/copies/{id}" (updating something).
    @PutMapping("/copies/{id}")
    // PUT /api/staff/copies/{id}: edits a copy; the body is checked first (@Valid).
    public Object updateCopy(@PathVariable Long id, @Valid @RequestBody BookCopyRequest r) {
        // Apply the change (with the same checks and the on-loan rules).
        return assets.update(id, r);
    } // end of updateCopy()

    // Handles HTTP DELETE requests to "/copies/{id}" (removing something).
    @DeleteMapping("/copies/{id}")
    // DELETE /api/staff/copies/{id}: deletes a copy.
    public void deleteCopy(@PathVariable Long id) {
        // Delete it (the service refuses if the copy is on loan).
        assets.delete(id);
    } // end of deleteCopy()

    // Handles HTTP GET requests to "/inventory/summary" (reading data).
    @GetMapping("/inventory/summary")
    // GET /api/staff/inventory/summary: counts copies by status.
    public Object inventorySummary() {
        // Return the inventory counts.
        return inventory.summary();
    } // end of inventorySummary()

    // Handles HTTP GET requests to "/inventory" (reading data).
    @GetMapping("/inventory")
    // GET /api/staff/inventory: returns the inventory table (one record per stock item, stored in inventory_audits).
    public Object inventoryList() {
        // Load all inventory records.
        return inventory.getInventory();
    } // end of inventoryList()

    // Handles HTTP POST requests to "/inventory" (creating something).
    @PostMapping("/inventory")
    // POST /api/staff/inventory: Add Inventory; the body is checked first (@Valid).
    public Object addInventory(Authentication a, @Valid @RequestBody BookCopyRequest r) {
        // Save the item, recording the logged-in staff email.
        return inventory.addInventory(r, a.getName());
    } // end of addInventory()

    // Handles HTTP PUT requests to "/inventory/{id}" (updating something).
    @PutMapping("/inventory/{id}")
    // PUT /api/staff/inventory/{id}: Edit Inventory; the body is checked first (@Valid).
    public Object updateInventory(Authentication a, @PathVariable Long id, @Valid @RequestBody BookCopyRequest r) {
        // Update the item, recording the logged-in staff email.
        return inventory.updateInventory(id, r, a.getName());
    } // end of updateInventory()

    // Handles HTTP DELETE requests to "/inventory/{id}" (removing something).
    @DeleteMapping("/inventory/{id}")
    // DELETE /api/staff/inventory/{id}: Delete Inventory.
    public void deleteInventory(@PathVariable Long id) {
        // Delete it (the service refuses if the copy is on loan).
        inventory.deleteInventory(id);
    } // end of deleteInventory()

    // Handles HTTP GET requests to "/reservations" (reading data).
    @GetMapping("/reservations")
    // GET /api/staff/reservations: returns every reservation.
    public Object reservations() {
        // Load all reservations (expired ones are marked EXPIRED first).
        return reservations.getAll();
    } // end of reservations()

    // Handles HTTP PUT requests to "/reservations/{id}/status" (updating something).
    @PutMapping("/reservations/{id}/status")
    // PUT /api/staff/reservations/{id}/status: changes a reservation status; the body is {"status": "..."}.
    public Object reservationStatus(@PathVariable Long id, @RequestBody Map<String, String> b) {
        // Read "status" from the body and apply it (the service validates it).
        return reservations.updateStatus(id, b.get("status"));
    } // end of reservationStatus()

    // Handles HTTP PUT requests to "/reservations/{id}" (updating something).
    @PutMapping("/reservations/{id}")
    // PUT /api/staff/reservations/{id}: edits a reservation (status and expiry); the body is checked first (@Valid).
    public Object updateReservation(@PathVariable Long id, @Valid @RequestBody ReservationUpdateRequest r) {
        // Apply the change (the service checks the dates, duplicates and copy availability).
        return reservations.update(id, r.getStatus(), r.getExpiresAt());
    } // end of updateReservation()

    // Handles HTTP DELETE requests to "/reservations/{id}" (removing something).
    @DeleteMapping("/reservations/{id}")
    // DELETE /api/staff/reservations/{id}: deletes a reservation.
    public void deleteReservation(@PathVariable Long id) {
        // Delete it (the member is notified if it was still active).
        reservations.delete(id);
    } // end of deleteReservation()

    // Handles HTTP GET requests to "/borrowings" (reading data).
    @GetMapping("/borrowings")
    // GET /api/staff/borrowings: returns every borrow record.
    public Object borrowings() {
        // Load all loans (late ones are marked OVERDUE first).
        return borrows.getAll();
    } // end of borrowings()

    // Handles HTTP POST requests to "/borrowings" (creating something).
    @PostMapping("/borrowings")
    // POST /api/staff/borrowings: issues a copy to a member; the body is checked first (@Valid).
    public Object issue(Authentication a, @Valid @RequestBody BorrowRequest r) {
        // Issue it, recording the logged-in staff member as the issuer.
        return borrows.issue(r.getMemberId(), r.getBookCopyId(), me(a).getId());
    } // end of issue()

    // Handles HTTP PUT requests to "/borrowings/{id}/renew" (updating something).
    @PutMapping("/borrowings/{id}/renew")
    // PUT /api/staff/borrowings/{id}/renew: renews a loan.
    public Object renew(@PathVariable Long id) {
        // Renew it (the service checks limits and due dates).
        return borrows.renew(id);
    } // end of renew()

    // Handles HTTP PUT requests to "/borrowings/{id}/return" (updating something).
    @PutMapping("/borrowings/{id}/return")
    // PUT /api/staff/borrowings/{id}/return: records a return.
    public Object returnBook(@PathVariable Long id) {
        // Record it (a fine is created automatically if late).
        return borrows.returnBook(id);
    } // end of returnBook()

    // Handles HTTP PUT requests to "/borrowings/{id}/cancel" (updating something).
    @PutMapping("/borrowings/{id}/cancel")
    // PUT /api/staff/borrowings/{id}/cancel: cancels a loan issued by mistake.
    public Object cancelBorrow(@PathVariable Long id) {
        // Cancel it and put the copy back on the shelf.
        return borrows.cancel(id);
    } // end of cancelBorrow()

    // Handles HTTP PUT requests to "/borrowings/{id}" (updating something).
    @PutMapping("/borrowings/{id}")
    // PUT /api/staff/borrowings/{id}: edits a loan's due date and renewal count; the body is checked first (@Valid).
    public Object updateBorrow(@PathVariable Long id, @Valid @RequestBody BorrowUpdateRequest r) {
        // Apply the change (the service checks the loan is still out, the date range and the renewal limit).
        return borrows.update(id, r.getDueDate(), r.getRenewalCount());
    } // end of updateBorrow()

    // Handles HTTP DELETE requests to "/borrowings/{id}" (removing something).
    @DeleteMapping("/borrowings/{id}")
    // DELETE /api/staff/borrowings/{id}: deletes a loan record.
    public void deleteBorrow(@PathVariable Long id) {
        // Delete it (refused if it has a fine; a copy still out goes back on the shelf).
        borrows.delete(id);
    } // end of deleteBorrow()

    // Handles HTTP GET requests to "/fines" (reading data).
    @GetMapping("/fines")
    // GET /api/staff/fines: returns every fine.
    public Object fines() {
        // Load all fines.
        return fines.getAll();
    } // end of fines()

    // Handles HTTP PUT requests to "/fines/{id}/status" (updating something).
    @PutMapping("/fines/{id}/status")
    // PUT /api/staff/fines/{id}/status: changes a fine status; the body is {"status": "..."}.
    public Object fineStatus(@PathVariable Long id, @RequestBody Map<String, String> b) {
        // Read "status" from the body and apply it (the service validates it).
        return fines.updateStatus(id, b.get("status"));
    } // end of fineStatus()

    // Handles HTTP DELETE requests to "/fines/{id}" (removing something).
    @DeleteMapping("/fines/{id}")
    // DELETE /api/staff/fines/{id}: deletes a fine.
    public void deleteFine(@PathVariable Long id) {
        // Delete it (the service refuses UNPAID fines).
        fines.delete(id);
    } // end of deleteFine()

    // Handles HTTP GET requests to "/feedback" (reading data).
    @GetMapping("/feedback")
    // GET /api/staff/feedback: returns every feedback/complaint.
    public Object feedback() {
        // Load all feedback records.
        return feedback.getAll();
    } // end of feedback()

    // Handles HTTP PUT requests to "/feedback/{id}/status" (updating something).
    @PutMapping("/feedback/{id}/status")
    // PUT /api/staff/feedback/{id}/status: changes a feedback status; the body is {"status": "..."}.
    public Object feedbackStatus(@PathVariable Long id, @RequestBody Map<String, String> b) {
        // Read "status" from the body and apply it (the service validates it).
        return feedback.updateStatus(id, b.get("status"));
    } // end of feedbackStatus()

    // Handles HTTP DELETE requests to "/feedback/{id}" (removing something).
    @DeleteMapping("/feedback/{id}")
    // DELETE /api/staff/feedback/{id}: deletes a feedback record.
    public void deleteFeedback(@PathVariable Long id) {
        // Delete it.
        feedback.delete(id);
    } // end of deleteFeedback()

    // Handles HTTP GET requests to "/academic-resources" (reading data).
    @GetMapping("/academic-resources")
    // GET /api/staff/academic-resources: searches academic resources (?q= optional).
    public Object academicResources(@RequestParam(required = false) String q) {
        // Search by text (all if empty).
        return academicResources.search(q);
    } // end of academicResources()

    // Handles HTTP POST requests to "/academic-resources" (creating something).
    @PostMapping("/academic-resources")
    // POST /api/staff/academic-resources: adds a resource; the body is checked first (@Valid).
    public Object addAcademicResource(@Valid @RequestBody AcademicResourceRequest r) {
        // Create it.
        return academicResources.create(r);
    } // end of addAcademicResource()

    // Handles HTTP PUT requests to "/academic-resources/{id}" (updating something).
    @PutMapping("/academic-resources/{id}")
    // PUT /api/staff/academic-resources/{id}: edits a resource.
    public Object updateAcademicResource(
        // the id from the URL,
        @PathVariable Long id,
        // and the validated form data.
        @Valid @RequestBody AcademicResourceRequest r
    // End of the parameter list; the method body starts here.
    ) {
        // Apply the change.
        return academicResources.update(id, r);
    } // end of block

    // Handles HTTP DELETE requests to "/academic-resources/{id}" (removing something).
    @DeleteMapping("/academic-resources/{id}")
    // DELETE /api/staff/academic-resources/{id}: deletes a resource.
    public void deleteAcademicResource(@PathVariable Long id) {
        // Delete it.
        academicResources.delete(id);
    } // end of deleteAcademicResource()

    // Handles HTTP GET requests to "/members" (reading data).
    @GetMapping("/members")
    // GET /api/staff/members: returns only the MEMBER accounts (for the Issue Book form).
    public Object members() {
        // Take the users...
        return users
            // ...all of them...
            .getAll()
            // ...one by one...
            .stream()
            // ...keep only those whose role is MEMBER...
            .filter(u -> u.getRole().name().equals("MEMBER"))
            // ...and collect them into a list.
            .toList();
    } // end of members()
} // end of class StaffController
