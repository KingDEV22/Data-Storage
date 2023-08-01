package com.data.organization.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import com.amazonaws.services.directory.model.EntityAlreadyExistsException;
import com.data.organization.dto.FormMetaDataResponse;
import com.data.organization.dto.FormRequest;
import com.data.organization.model.Form;
import com.data.organization.model.OrgUser;
import com.data.organization.repository.FormRepo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FormService {

    @Autowired
    private FormUtilService fUtilService;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private QuestionService qService;
    @Autowired
    private FormRepo fRepo;

    public String saveFormMetaData(FormRequest fRequest) throws IOException {
        OrgUser user = fUtilService.getOrgUser();

        if (user.getName().equals(fRequest.getFormName())) {
            throw new EntityAlreadyExistsException("Form Name already used!!");
        }
        String orgId = user.getOrgId();
        log.info("Org User found");
        String formLink = fUtilService.createform(fRequest);
        String fId = orgId + ".f" + (mongoTemplate.count(new Query(), Form.class) + 1);
        Form forMetaData = new Form(fId,
                formLink,
                fRequest.getFormName(),
                orgId);
        fRepo.save(forMetaData);
        log.info("form data saved");
        qService.saveQuestion(fRequest.getQuestions(), fId);
        return formLink;

    }

    public List<FormMetaDataResponse> getAllFormByOrg() {
        OrgUser user = fUtilService.getOrgUser();
        List<Form> formByOrg = fRepo.findAllByorgId(user.getOrgId());
        return formByOrg.stream()
                .map(form -> new FormMetaDataResponse(form.getLink(), form.getName(), form.getCreateDate()))
                .collect(Collectors.toList());

    }

    public FormMetaDataResponse getFormByName(String name) {
        Form formByName = fUtilService.getFormMetaData(name);
        FormMetaDataResponse formData = new FormMetaDataResponse();
        BeanUtils.copyProperties(formByName,formData);
        return formData;
    }

}
