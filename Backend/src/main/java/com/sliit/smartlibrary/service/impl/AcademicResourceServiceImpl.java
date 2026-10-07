package com.sliit.smartlibrary.service.impl;

import com.sliit.smartlibrary.enums.AcademicResourceType;
import com.sliit.smartlibrary.validation.ValidationRules;
import com.sliit.smartlibrary.dto.AcademicResourceRequest;
import com.sliit.smartlibrary.entity.AcademicResource;
import com.sliit.smartlibrary.exception.BadRequestException;
import com.sliit.smartlibrary.exception.ResourceNotFoundException;
import com.sliit.smartlibrary.repository.AcademicResourceRepository;
import com.sliit.smartlibrary.service.AcademicResourceService;
import org.springframework.stereotype.Service;
import java.time.Year;
import java.util.List;

@Service
public class AcademicResourceServiceImpl implements AcademicResourceService {
    private final AcademicResourceRepository resources;
    public AcademicResourceServiceImpl(AcademicResourceRepository resources){this.resources=resources;}
    public List<AcademicResource> search(String q){
        if(q==null||q.isBlank())return resources.findAll();
        String s=q.trim();return resources.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrCategoryContainingIgnoreCase(s,s,s);
    }
    public AcademicResource get(Long id){return resources.findById(id).orElseThrow(()->new ResourceNotFoundException("Academic resource not found: "+id));}
    public AcademicResource create(AcademicResourceRequest r){return resources.save(apply(new AcademicResource(),r));}
    public AcademicResource update(Long id,AcademicResourceRequest r){return resources.save(apply(get(id),r));}
    public AcademicResource recordView(Long id){AcademicResource x=get(id);x.setUsageCount((x.getUsageCount()==null?0:x.getUsageCount())+1);return resources.save(x);}
    public void delete(Long id){resources.delete(get(id));}
    private AcademicResource apply(AcademicResource x,AcademicResourceRequest r){
        if(r.getTitle()==null||r.getTitle().isBlank())throw new BadRequestException("Resource title is required.");
        if(r.getResourceType()==null)throw new BadRequestException("Resource type is required.");
        ValidationRules.requireYearInRange(r.getPublicationYear(),1500,"Publication year");
        if(r.getResourceType()==AcademicResourceType.EBOOK&&(r.getResourceUrl()==null||r.getResourceUrl().isBlank()))throw new BadRequestException("An e-book needs a resource URL so members can open it.");
        x.setTitle(r.getTitle().trim());x.setResourceType(r.getResourceType());x.setAuthor(ValidationRules.clean(r.getAuthor()));x.setCategory(ValidationRules.clean(r.getCategory()));x.setPublicationYear(r.getPublicationYear());x.setResourceUrl(ValidationRules.clean(r.getResourceUrl()));x.setDescription(ValidationRules.clean(r.getDescription()));if(x.getUsageCount()==null)x.setUsageCount(0L);return x;
    }
}
