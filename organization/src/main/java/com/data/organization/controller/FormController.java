package com.data.organization.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.data.organization.dto.FormRequest;
import com.data.organization.model.Form;
import com.data.organization.service.FormService;
import com.data.organization.service.FormUtilService;
import com.data.organization.service.QuestionService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/org", produces = "application/json")
@CrossOrigin(origins = "*")
@Slf4j
public class FormController {

    @Autowired
    private FormService formService;
    @Autowired
    private QuestionService qService;
    @Autowired
     private FormUtilService fUtilService;

    private String getOrgId() {
       String email = SecurityContextHolder.getContext().getAuthentication().getName();
       return fUtilService.getOrgUser(email).getOrgId();
    }

    @PostMapping("/form")
    public ResponseEntity<?> getForm(@RequestBody FormRequest fRequest) {
        try {
            return ResponseEntity.ok().body(formService.saveFormMetaData(fRequest,getOrgId()));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/form/all")
    public ResponseEntity<?> getFormByOrg() {
        try {
            return ResponseEntity.ok().body(formService.getAllFormByOrg(getOrgId()));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/form/name")
    public ResponseEntity<?> getFormByName(@RequestHeader("name") String name) {
        try {
            return ResponseEntity.ok().body(formService.getFormByName(name));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/form/question")
    public ResponseEntity<?> getQuestionByForm(@RequestHeader("name") String name) {
        try {
            Form fdata = fUtilService.getFormMetaData(name);
            return ResponseEntity.ok().body(qService.getQuestionByForm(fdata.getFId()));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
