package com.data.organization.service;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.management.AttributeNotFoundException;
import javax.persistence.EntityExistsException;
import javax.transaction.Transactional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.data.organization.configration.rabbitmq.MessagingConfig;
import com.data.organization.dto.FormDataRequest;
import com.data.organization.dto.FormMetaDataResponse;
import com.data.organization.dto.FormRequest;
import com.data.organization.dto.QuestionAnswerRequest;
import com.data.organization.model.Answer;
import com.data.organization.model.Form;
import com.data.organization.model.OrgUser;
import com.data.organization.repository.AnswerRepo;
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
    private AnswerRepo answerRepo;

    @Transactional
    public String saveFormMetaData(FormRequest fRequest, OrgUser orgUser) throws IOException {
        if (fRepo.existsByName(fRequest.getFormName())) {
            throw new EntityExistsException("Form Name already used!!");
        }
        log.info("Org User found");
        // String formLink = fUtilService.createform(fRequest);
        String formLink = "http://localhost:4000/" + "form?name=" + fRequest.getFormName() + "-"
                + orgUser.getName().replaceAll("\\s+", "");

        log.info(formLink);

        try {
            String fId = storeFormData(formLink, fRequest.getFormName(), orgUser.getOrgId());
            log.info("form data saved");
            qService.saveQuestion(fRequest.getQuestions(), fId);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return formLink;

    }

    @CachePut(cacheNames = "forms", key = "#orgId")
    private String storeFormData(String formLink, String name, String orgId) {
        Form formMetaData = new Form(
                formLink,
                name,
                orgId);
        fRepo.save(formMetaData);
        return formMetaData.getFId();
    }

    @Cacheable(cacheNames = "forms", key = "#orgId")
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

    @RabbitListener(queues = MessagingConfig.QUEUE)
    public void consumeMessageFromQueue(FormDataRequest formDataRequest) {
        log.info(formDataRequest.toString());
        try {
            if (formDataRequest.getUrl().equals(null)) {
                throw new AttributeNotFoundException("Url is not found");
            }
            Form formByLink = fUtilService.getFormMetaDataByLink(formDataRequest.getUrl());
            log.info("form found!!");
            saveAnswer(formDataRequest.getQa(), formByLink.getFId());
        } catch (Exception e) {
            log.error(e.getMessage());
        }

    }

    private void saveAnswer(List<QuestionAnswerRequest> answersRequest, String fId) {
        List<Answer> answersData = answersRequest.stream()
                .map(answer -> new Answer(answer.getName(), answer.getValue(), fId))
                .collect(Collectors.toList());
        answerRepo.saveAll(answersData);
        log.info("data saved");
    }

}
