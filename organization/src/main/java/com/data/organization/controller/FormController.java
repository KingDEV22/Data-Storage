package com.data.organization.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.data.organization.dto.FormRequest;
import com.data.organization.service.FormService;
import com.data.organization.service.QuestionService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/org", produces = "application/json")
@CrossOrigin(origins = "*")
@Slf4j
@AllArgsConstructor
public class FormController {

    private FormService formService;
    private QuestionService qService;

    @PostMapping("/form")
    public ResponseEntity<?> getForm(@RequestBody FormRequest fRequest) {
        try {
            return ResponseEntity.ok().body(formService.saveFormMetaData(fRequest));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/form/all")
    public ResponseEntity<?> getFormByOrg() {
        try {
            return ResponseEntity.ok().body(formService.getAllFormByOrg());
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
            return ResponseEntity.ok().body(qService.getQuestionByForm(name));
        } catch (Exception e) {
            log.error(e.toString());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
