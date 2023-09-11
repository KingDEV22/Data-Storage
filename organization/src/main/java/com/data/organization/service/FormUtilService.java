package com.data.organization.service;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.data.organization.exception.FormDataException;
import com.data.organization.model.MetaData;
import com.data.organization.model.OrgUser;
import com.data.organization.repository.MetaDataRepo;
import com.data.organization.repository.OrgUserRepo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FormUtilService {

    @Autowired
    private OrgUserRepo oRepository;

    @Autowired
    private MetaDataRepo fRepo;

    @Cacheable(cacheNames = "metaData", key = "#name + '-' + #orgId")
    public MetaData getMetaData(String name, String orgId) {    
        return fRepo.findByNameAndOrgId(name, orgId).orElse(null);
    }

    @Cacheable(value = "users", key = "#email")
    public OrgUser getOrgUser(String email) {
        return oRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user found!!"));
    }

    @Cacheable(cacheNames = "forms", key = "#link")
    public MetaData getFormMetaDataByLink(String link) {
        return fRepo.findByLink(link).orElseThrow(() -> new EntityNotFoundException("Form not found!!!"));
    }

    public void checkForm(String name, String org){
        MetaData form = getMetaData(name, org);
        log.info("Form Data Fetched...");
        if(form !=null) throw new FormDataException("Form Name already used!!");
    }

    @Cacheable(cacheNames = "formsOrFiles", key = "#orgId" + "-" + "#type")
    public List<MetaData> getFormsOrFilesByOrg(String orgId,String type){
        List<MetaData> formOrFileByOrg = fRepo.findAllByorgId(orgId,type);
        if(formOrFileByOrg.isEmpty()) throw new FormDataException("No Forms or file Exists!!");
        return formOrFileByOrg;
    }

}