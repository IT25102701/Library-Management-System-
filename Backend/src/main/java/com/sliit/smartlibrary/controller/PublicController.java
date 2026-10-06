package com.sliit.smartlibrary.controller;
import com.sliit.smartlibrary.entity.*;
import com.sliit.smartlibrary.repository.*;
import com.sliit.smartlibrary.service.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/public")
public class PublicController {
 private final BookService books;private final AssetService assets;private final CategoryRepository categories;private final ReviewService reviews;private final AcademicResourceService academicResources;
 public PublicController(BookService books,AssetService assets,CategoryRepository categories,ReviewService reviews,AcademicResourceService academicResources){this.books=books;this.assets=assets;this.categories=categories;this.reviews=reviews;this.academicResources=academicResources;}
 @GetMapping("/categories") public Object categories(){return categories.findAll();}
 @GetMapping("/books") public List<Map<String,Object>> list(@RequestParam(required=false)String q,@RequestParam(required=false)Long categoryId){return books.search(q,categoryId).stream().map(this::view).toList();}
 @GetMapping("/books/{id}") public Map<String,Object> one(@PathVariable Long id){return view(books.get(id));}
 @GetMapping("/books/{id}/reviews") public Object reviews(@PathVariable Long id){return reviews.forBook(id);}
 @GetMapping("/academic-resources") public Object academicResources(@RequestParam(required=false) String q){return academicResources.search(q);}
 @GetMapping("/academic-resources/{id}") public Object academicResource(@PathVariable Long id){return academicResources.recordView(id);}
 private Map<String,Object> view(Book b){Map<String,Object>m=new LinkedHashMap<>();m.put("id",b.getId());m.put("title",b.getTitle());m.put("isbn",b.getIsbn());m.put("category",b.getCategory());m.put("author",b.getAuthor());m.put("publisher",b.getPublisher());m.put("description",b.getDescription());m.put("publishedYear",b.getPublishedYear());m.put("coverUrl",b.getCoverUrl());m.put("availableCopies",assets.availableCount(b.getId()));m.put("totalCopies",assets.byBook(b.getId()).size());return m;}
}
