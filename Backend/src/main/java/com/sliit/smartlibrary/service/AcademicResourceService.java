package com.sliit.smartlibrary.service;
import com.sliit.smartlibrary.dto.AcademicResourceRequest;
import com.sliit.smartlibrary.entity.AcademicResource;
import java.util.List;
public interface AcademicResourceService {
    List<AcademicResource> search(String q);
    AcademicResource get(Long id);
    AcademicResource create(AcademicResourceRequest request);
    AcademicResource update(Long id, AcademicResourceRequest request);
    AcademicResource recordView(Long id);
    void delete(Long id);
}
