package com.data.organization.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.data.organization.dto.FormDataUpdateDTO;
import com.data.organization.dto.FormDeleteDTO;
import com.data.organization.dto.FormRequest;
import com.data.organization.dto.ResponseMessage;
import com.data.organization.service.FormService;
import com.data.organization.service.RecordService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/org", produces = "application/json")
@CrossOrigin(origins = "*")
@Slf4j
public class DataController {

    @Autowired
    private FormService formService;

    @Autowired
    private RecordService rService;
    private final String FILE_FORMAT = "text/csv";

    @PostMapping("/form")
    public ResponseEntity<ResponseMessage> getFormLink(@RequestBody FormRequest fRequest) {
        try {
            return ResponseEntity.ok().body(
                    new ResponseMessage(formService.saveFormMetaData(fRequest.getFormName(), fRequest.getQuestions())));
        } catch (Exception e) {
            log.error("error", e);
            return ResponseEntity.badRequest().body(new ResponseMessage(e.getMessage()));
        }
    }

    @PostMapping("/data")
    public ResponseEntity<?> storeFileData(@RequestParam("file") MultipartFile file, @RequestParam("name") String fileName) {
        try {
            if (!file.getContentType().equals(FILE_FORMAT)) {
                return ResponseEntity.badRequest().body("File format not supported...");
            }
            rService.storeFileData(file,fileName);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/data")
    public ResponseEntity<?> getFileData(@RequestHeader("name") String fileName) {
        try {
            
            return ResponseEntity.ok().body(rService.getFileData(fileName));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/form/all")
    public ResponseEntity<?> getFormByOrg() {
        try {
            return ResponseEntity.ok().body(formService.getAllFormByOrg());
        } catch (Exception e) {
            log.error("error", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/form")
    public ResponseEntity<?> getFormByName(@RequestHeader("name") String name) {
        try {
            return ResponseEntity.ok().body(formService.getFormByName(name));
        } catch (Exception e) {
            log.error("error", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/form/name")
    public ResponseEntity<?> updateFormName(@RequestBody FormDataUpdateDTO formRequest) {
        try {
            formService.updateFormName(formRequest.getNewFormName(), formRequest.getFormName());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("error", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/form/name")
    public ResponseEntity<?> deleteForm(@RequestBody FormDeleteDTO formRequest) {
        try {
            formService.deleteForm(formRequest.getFormName());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("error", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/form/question")
    public ResponseEntity<?> updateFormQuestions(@RequestBody FormDataUpdateDTO formRequest) {
        try {
            formService.updateFormQuestions(formRequest);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("error", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/form/question")
    public ResponseEntity<?> deteleFormQuestions(@RequestBody FormDeleteDTO formRequest) {
        try {
            formService.deleteFormQuestions(formRequest.getQuestionIds());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("error", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/form/question")
    public ResponseEntity<?> getQuestionByForm(@RequestHeader("name") String name) {
        try {
            return ResponseEntity.ok().body(formService.getFormQuestions(name));
        } catch (Exception e) {
            log.error("error", e);
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
