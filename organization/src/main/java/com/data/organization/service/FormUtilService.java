package com.data.organization.service;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.data.organization.model.Form;
import com.data.organization.model.OrgUser;
import com.data.organization.repository.FormRepo;
import com.data.organization.repository.OrgUserRepo;

@Service
public class FormUtilService {

    @Autowired
    private OrgUserRepo oRepository;

    @Autowired
    private FormRepo fRepo;

    @Cacheable(cacheNames = "form", key = "#name")
    public Form getFormMetaData(String name) {
        return fRepo.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Form not found!!!"));
    }

    @Cacheable(value = "users", key = "#email")
    public OrgUser getOrgUser(String email) {
        return oRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user found!!"));
    }

    @Cacheable(cacheNames = "form", key="#link")
    public Form getFormMetaDataByLink(String link){
        return fRepo.findByLink(link).orElseThrow(() -> new EntityNotFoundException("Form not found!!!"));
    }

}