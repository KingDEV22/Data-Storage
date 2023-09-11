package com.data.organization.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.BeanUtils;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import com.data.organization.dto.FormDataUpdateDTO;
import com.data.organization.dto.FormMetaDataResponse;
import com.data.organization.dto.QuestionDTO;
import com.data.organization.exception.FormDataException;
import com.data.organization.model.MetaData;
import com.data.organization.model.OrgUser;
import com.data.organization.repository.MetaDataRepo;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class FormService {

    private FormUtilService fUtilService;
    private QuestionService qService;
    private MetaDataRepo fRepo;

    

    private String createFormLink(String formName, String orgName) {
        String link = "http://localhost:4000/form?name=" + formName.replaceAll("\\s+", "") + "-"
                + orgName.replaceAll("\\s+", "");
        return link;
    }

    public String saveFormMetaData(String formName, List<QuestionDTO> questions) throws Exception {
        OrgUser orgUser = fUtilService.getOrgUser();
        log.info("org User found!!!");
        fUtilService.checkForm(formName, orgUser.getOrgId());
        log.info("New Form object initiated");
        String formLink = createFormLink(formName, orgUser.getName());
        log.info(formLink);
        try {
            String fId = storeFormData(formLink, formName, orgUser.getOrgId());
            log.info("form data saved");
            qService.saveQuestion(questions, fId);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new FormDataException("Form Data not saved!!!!");
        }
        return formLink;
    }

    @Transactional
    @CachePut(cacheNames = "formsOrFiles", key = "#orgId")
    private String storeFormData(String formLink, String name, String orgId) {
        MetaData formMetaData = new MetaData(
                formLink,
                name,
                "form",
                orgId);
        fRepo.save(formMetaData);
        return formMetaData.getMetaDataId();
    }

    public List<FormMetaDataResponse> getAllFormByOrg() throws Exception {
        OrgUser orgUser = fUtilService.getOrgUser();
        log.info("org User found!!!");
        List<MetaData> formByOrg = fUtilService.getFormsOrFilesByOrg(orgUser.getOrgId(), "form");
        return formByOrg.parallelStream()
                .map(form -> new FormMetaDataResponse(form.getLink(), form.getName(), form.getCreateDate()))
                .collect(Collectors.toList());

    }

    public FormMetaDataResponse getFormByName(String name) {
        OrgUser orgUser = fUtilService.getOrgUser();
        log.info("org User found!!!");
        try {
            MetaData formByName = fUtilService.getMetaData(name, orgUser.getOrgId());
            log.info("Form found!!!");
            FormMetaDataResponse formData = new FormMetaDataResponse();
            BeanUtils.copyProperties(formByName, formData);
            return formData;
        } catch (Exception e) {
            throw new FormDataException("Error while fetching data!!!!");
        }

    }

    public List<QuestionDTO> getFormQuestions(String name) throws Exception {
        OrgUser orgUser = fUtilService.getOrgUser();
        log.info("org User found!!!");
        MetaData formByName = fUtilService.getMetaData(name, orgUser.getOrgId());
        return qService.getQuestionByForm(formByName.getMetaDataId());
    }

    public void updateFormName(String newName, String oldName) {
        OrgUser orgUser = fUtilService.getOrgUser();
        log.info("org User found!!!");
        try {
            MetaData formByName = fUtilService.getMetaData(oldName, orgUser.getOrgId());
            String link = createFormLink(newName, orgUser.getName());
            formByName.setName(newName);
            formByName.setLink(link);
            fRepo.save(formByName);
        } catch (Exception e) {
            log.error("Error", e);
            throw new FormDataException("Error while updating formName!!!");
        }
    }

    public void updateFormQuestions(FormDataUpdateDTO fUpdateDTO) throws Exception {
        OrgUser orgUser = fUtilService.getOrgUser();
        log.info("org User found!!!");
        MetaData formByName = fUtilService.getMetaData(fUpdateDTO.getFormName(), orgUser.getOrgId());
        if (formByName == null) {
            throw new FormDataException("Form Name not found!!! Data Altered...");
        }
        try {

            String mId = formByName.getMetaDataId();
            qService.updateQuestions(fUpdateDTO.getQuestions(), mId);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new FormDataException("Error while updating form questions.....");
        }
    }

    public void deleteFormQuestions(List<String> questionIds) {
        qService.deleteQuestionsByIds(questionIds);
    }

    public void deleteForm(String formName) throws Exception {
        OrgUser orgUser = fUtilService.getOrgUser();
        log.info("org User found!!!");
        MetaData formByName = fUtilService.getMetaData(formName, orgUser.getOrgId());
        if (formByName.getMetaDataId() == null)
            throw new FormDataException("Form not found!!!");
        try {
            qService.deleteByFormId(formByName.getMetaDataId());
            fRepo.deleteByNameAndOrgId(formName, orgUser.getOrgId());

        } catch (Exception e) {
            log.error("Error", e);
            throw new FormDataException("Error while deleting form !!!");
        }

    }

}
