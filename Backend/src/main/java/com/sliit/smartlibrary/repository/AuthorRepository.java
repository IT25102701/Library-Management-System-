package com.sliit.smartlibrary.repository;
import com.sliit.smartlibrary.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AuthorRepository extends JpaRepository<Author,Long>{}
