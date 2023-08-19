package com.data.organization.service;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.data.organization.exception.FormDataException;
import com.data.organization.model.Form;
import com.data.organization.model.OrgUser;
import com.data.organization.repository.FormRepo;
import com.data.organization.repository.OrgUserRepo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FormUtilService {

    @Autowired
    private OrgUserRepo oRepository;

    @Autowired
    private FormRepo fRepo;

    @Cacheable(cacheNames = "form", key = "#name + '-' + #orgId")
    public Form getFormMetaData(String name, String orgId) {    
        return fRepo.findByNameAndOrgId(name, orgId).orElse(null);
    }

    @Cacheable(value = "users", key = "#email")
    public OrgUser getOrgUser(String email) {
        return oRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user found!!"));
    }

    @Cacheable(cacheNames = "form", key = "#link")
    public Form getFormMetaDataByLink(String link) {
        return fRepo.findByLink(link).orElseThrow(() -> new EntityNotFoundException("Form not found!!!"));
    }

    public void checkForm(String name, String org){
        Form form = getFormMetaData(name, org);
        log.info("Form Data Fetched...");
        if(form !=null) throw new FormDataException("Form Name already used!!");
    }

    @Cacheable(cacheNames = "forms", key = "#orgId")
    public List<Form> getFormsByOrg(String orgId){
        List<Form> formByOrg = fRepo.findAllByorgId(orgId);
        if(formByOrg.isEmpty()) throw new FormDataException("No Forms Exists!!");
        return formByOrg;
    }

}