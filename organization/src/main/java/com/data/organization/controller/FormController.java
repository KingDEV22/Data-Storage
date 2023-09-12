package com.data.organization.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.data.organization.dto.FormDataUpdateDTO;
import com.data.organization.dto.FormDeleteDTO;
import com.data.organization.dto.FormRequest;
import com.data.organization.dto.ResponseMessage;
import com.data.organization.exception.MetaDataException;
import com.data.organization.service.FormService;

@RestController
@RequestMapping(value = "/org", produces = "application/json")
@CrossOrigin(origins = "*")
public class FormController {

    @Autowired
    private FormService formService;

    @PostMapping("/form")
    public ResponseEntity<?> getFormLink(@RequestBody FormRequest fRequest) {
        try {
            return ResponseEntity.ok().body(
                    new ResponseMessage(formService.saveFormMetaData(fRequest.getFormName(), fRequest.getQuestions())));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        } catch (MetaDataException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ResponseMessage(e.getMessage()));
        }
    }

    @GetMapping("/form/all")
    public ResponseEntity<?> getFormByOrg() {
        try {
            return ResponseEntity.ok().body(formService.getAllFormByOrg());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        } catch (MetaDataException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage(e.getMessage()));
        }
    }

    @GetMapping("/form")
    public ResponseEntity<?> getFormByName(@RequestHeader("name") String name) {
        try {
            return ResponseEntity.ok().body(formService.getFormByName(name));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        } catch (MetaDataException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage(e.getMessage()));
        }
    }

    @PutMapping("/form/name")
    public ResponseEntity<?> updateFormName(@RequestBody FormDataUpdateDTO formRequest) {
        try {
            formService.updateFormName(formRequest.getNewFormName(), formRequest.getFormName());
            return ResponseEntity.ok().build();
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        } catch (MetaDataException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage(e.getMessage()));
        }
    }

    @DeleteMapping("/form/name")
    public ResponseEntity<?> deleteForm(@RequestBody FormDeleteDTO formRequest) {
        try {
            formService.deleteForm(formRequest.getFormName());
            return ResponseEntity.ok().build();
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        } catch (MetaDataException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage(e.getMessage()));
        }
    }

    @PutMapping("/form/question")
    public ResponseEntity<?> updateFormQuestions(@RequestBody FormDataUpdateDTO formRequest) {
        try {
            formService.updateFormQuestions(formRequest);
            return ResponseEntity.ok().build();
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        } catch (MetaDataException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage(e.getMessage()));
        }
    }

    @DeleteMapping("/form/question")
    public ResponseEntity<?> deteleFormQuestions(@RequestBody FormDeleteDTO formRequest) {
        try {
            formService.deleteFormQuestions(formRequest.getQuestionIds());
            return ResponseEntity.ok().build();
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        } catch (MetaDataException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage(e.getMessage()));
        }
    }

    @GetMapping("/form/question")
    public ResponseEntity<?> getQuestionByForm(@RequestHeader("name") String name) {
        try {
            return ResponseEntity.ok().body(formService.getFormQuestions(name));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        } catch (MetaDataException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseMessage(e.getMessage()));
        }
    }

}
