package com.sliit.smartlibrary.repository;

import com.sliit.smartlibrary.entity.AcademicResource;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AcademicResourceRepository extends JpaRepository<AcademicResource,Long> {
    List<AcademicResource> findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrCategoryContainingIgnoreCase(String title,String author,String category);
}
