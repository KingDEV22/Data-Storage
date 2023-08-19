package com.data.organization.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.management.AttributeNotFoundException;
import javax.transaction.Transactional;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CachePut;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.data.organization.configration.rabbitmq.MessagingConfig;
import com.data.organization.dto.FormDataRequest;
import com.data.organization.dto.FormMetaDataResponse;
import com.data.organization.dto.FormRequest;
import com.data.organization.dto.QuestionAnswerRequest;
import com.data.organization.dto.QuestionDTO;
import com.data.organization.exception.FormDataException;
import com.data.organization.model.Answer;
import com.data.organization.model.Form;
import com.data.organization.model.OrgUser;
import com.data.organization.model.Question;
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

    private OrgUser getOrgUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return fUtilService.getOrgUser(email);
    }

    public String saveFormMetaData(FormRequest fRequest) throws Exception {
        OrgUser orgUser = getOrgUser();
        log.info("org User found!!!");
        fUtilService.checkForm(fRequest.getFormName(), orgUser.getOrgId());
        log.info("New Form storing initiated");
        String formLink = "http://localhost:4000/" + "form?name=" + fRequest.getFormName() + "-"
                + orgUser.getName().replaceAll("\\s+", "");
        log.info(formLink);
        try {
            String fId = storeFormData(formLink, fRequest.getFormName(), orgUser.getOrgId());
            log.info("form data saved");
            qService.saveQuestion(fRequest.getQuestions(), fId);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new FormDataException("Form Data not saved!!!!");
        }
        return formLink;
    }

    @Transactional
    @CachePut(cacheNames = "forms", key = "#orgId")
    private String storeFormData(String formLink, String name, String orgId) {
        Form formMetaData = new Form(
                formLink,
                name,
                orgId);
        fRepo.save(formMetaData);
        return formMetaData.getFId();
    }

    public List<FormMetaDataResponse> getAllFormByOrg() throws Exception {
        OrgUser orgUser = getOrgUser();
        log.info("org User found!!!");
        List<Form> formByOrg = fUtilService.getFormsByOrg(orgUser.getOrgId());
        return formByOrg.stream()
                .map(form -> new FormMetaDataResponse(form.getLink(), form.getName(), form.getCreateDate()))
                .collect(Collectors.toList());

    }

    public FormMetaDataResponse getFormByName(String name) {
        OrgUser orgUser = getOrgUser();
        log.info("org User found!!!");
        try {
            Form formByName = fUtilService.getFormMetaData(name, orgUser.getOrgId());
            log.info("Form found!!!");
            FormMetaDataResponse formData = new FormMetaDataResponse();
            BeanUtils.copyProperties(formByName, formData);
            return formData;
        } catch (Exception e) {
            throw new FormDataException("Error while fetching data!!!!");
        }

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

    private void saveAnswer(List<QuestionAnswerRequest> answersRequest, String fid) {
        List<Answer> answersData = answersRequest.stream()
                .map(answer -> new Answer(answer.getQname(), answer.getQlabel(), fid))
                .collect(Collectors.toList());
        answerRepo.saveAll(answersData);
        log.info("data saved");
    }

    public List<QuestionDTO> getFormQuestions(String name) throws Exception {
        OrgUser orgUser = getOrgUser();
        log.info("org User found!!!");
        Form formByName = fUtilService.getFormMetaData(name, orgUser.getOrgId());
        return qService.getQuestionByForm(formByName.getFId());
    }

    public void updateFormName(String newName, String oldName) {
        OrgUser orgUser = getOrgUser();
        log.info("org User found!!!");
        try {
            Form formByName = fUtilService.getFormMetaData(oldName, orgUser.getOrgId());
            formByName.setName(newName);
            fRepo.save(formByName);
        } catch (Exception e) {
            log.error("Error", e);
            throw new FormDataException("Error while updating formName!!!");
        }
    }

    public void updateFormQuestions(List<Question> questions, String name) throws Exception {
        OrgUser orgUser = getOrgUser();
        log.info("org User found!!!");
        Form formByName = fUtilService.getFormMetaData(name, orgUser.getOrgId());
        String fid = formByName.getFId();
        qService.updateQuestions(questions, fid);
    }

    public void deteleFormQuestions(List<String> questionIds) {
        OrgUser orgUser = getOrgUser();
        if (orgUser == null)
            throw new FormDataException("Invalid Request!!!!");
        qService.deleteQuestionsByIds(questionIds);
    }

    public void deleteForm(String formName) throws Exception {
        OrgUser orgUser = getOrgUser();
        log.info("org User found!!!");
        Form formByName = fUtilService.getFormMetaData(formName, orgUser.getOrgId());
        try {
            qService.deleteByFormId(formByName.getFId());
            fRepo.deleteByNameAndOrgId(formName, orgUser.getOrgId());

        } catch (Exception e) {
            log.error("Error", e);
            throw new FormDataException("Error while deleting form !!!");
        }

    }

}
