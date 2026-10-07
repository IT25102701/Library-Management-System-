package com.sliit.smartlibrary.repository;
import com.sliit.smartlibrary.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AuthorRepository extends JpaRepository<Author,Long>{
    // validation support
    boolean existsByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);
}
