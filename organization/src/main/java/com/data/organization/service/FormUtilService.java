package com.data.organization.service;

import java.util.List;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.data.organization.exception.MetaDataException;
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

    public String getContextEmail() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }

    @Cacheable(cacheNames = "metaData", key = "#name + '-' + #type")
    public MetaData getMetaData(String name, String type) {
        OrgUser user = getOrgUser(getContextEmail());
        return fRepo.findByNameAndOrgIdAndType(name, user.getOrgId(), type).orElse(null);
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

    public void checkForm(String name, String type) {
        MetaData form = getMetaData(name, type);
        log.info("Form Data Fetched...");
        if (form != null)
            throw new MetaDataException("Form Name already used!!");
    }

    @Cacheable(cacheNames = "formsOrFiles", key = "#type")
    public List<MetaData> getFormsOrFilesByOrg(String type) throws UsernameNotFoundException, MetaDataException {
        OrgUser user = getOrgUser(getContextEmail());
        List<MetaData> formOrFileByOrg = fRepo.findAllByorgIdAndType(user.getOrgId(), type);
        if (formOrFileByOrg.isEmpty())
            throw new MetaDataException("No Forms or file Exists!!");
        return formOrFileByOrg;
    }

}