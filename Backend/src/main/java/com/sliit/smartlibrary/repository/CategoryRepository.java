package com.sliit.smartlibrary.repository;
import com.sliit.smartlibrary.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CategoryRepository extends JpaRepository<Category,Long>{boolean existsByNameIgnoreCase(String name);}
