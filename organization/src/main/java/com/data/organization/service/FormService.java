package com.data.organization.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityExistsException;
import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.data.organization.dto.FormMetaDataResponse;
import com.data.organization.dto.FormRequest;
import com.data.organization.model.Form;
import com.data.organization.repository.FormRepo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class FormService {

    private FormUtilService fUtilService;
    private QuestionService qService;
    private FormRepo fRepo;

    @Transactional
    public String saveFormMetaData(FormRequest fRequest, String orgId) throws IOException {
        if (fRepo.existsByName(fRequest.getFormName())) {
            throw new EntityExistsException("Form Name already used!!");
        }
        log.info("Org User found");
        String formLink = fUtilService.createform(fRequest);
        String fId = storeFormData(formLink, fRequest.getFormName(), orgId);
        log.info("form data saved");
        qService.saveQuestion(fRequest.getQuestions(), fId);
        return formLink;

    }

    @CachePut(cacheNames = "forms",key = "#orgId")
    private String storeFormData(String formLink, String name, String orgId) {
        Form formMetaData = new Form(
                formLink,
                name,
                orgId);
        System.out.println();
        fRepo.save(formMetaData);
        return formMetaData.getFId();
    }

    @Cacheable(cacheNames = "forms",key = "#orgId")
    public List<FormMetaDataResponse> getAllFormByOrg(String orgId) {
        List<Form> formByOrg = fRepo.findAllByorgId(orgId);
        System.out.println(formByOrg);
        return formByOrg.stream()
                .map(form -> new FormMetaDataResponse(form.getLink(), form.getName(), form.getCreateDate()))
                .collect(Collectors.toList());

    }

    public FormMetaDataResponse getFormByName(String name) {
        Form formByName = fUtilService.getFormMetaData(name);
        FormMetaDataResponse formData = new FormMetaDataResponse();
        BeanUtils.copyProperties(formByName, formData);
        return formData;
    }

}
