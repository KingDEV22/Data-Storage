package com.data.organization.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.data.organization.model.DataType;
import com.data.organization.model.FileType;
import com.data.organization.service.RecordService;


@Controller
@RequestMapping(value = "/record")
@CrossOrigin(origins = "*")
public class DataController {
    @Autowired
    private RecordService rService;

    @PostMapping("/data")
    public ResponseEntity<?> storeFileData(@RequestParam("file") MultipartFile file,
            @RequestParam("name") String fileName) {
                System.out.println(file.getContentType());
        try {
            if (!file.getContentType().equals(FileType.CSV.getType())) {
                return ResponseEntity.badRequest().body("File format not supported...");
            }
            rService.storeFileData(file, fileName);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/data")
    public ResponseEntity<?> getFileData(@RequestHeader("name") String fileName) {
        try {

            return ResponseEntity.ok().body(rService.getFileData(fileName,DataType.FILE.toString()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

     @GetMapping("/data/all")
    public ResponseEntity<?> getAllFileMetaData() {
        try {

            return ResponseEntity.ok().body(rService.getAllFileMetaDataByOrg());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/download")
    public ResponseEntity<?> getDataInCSV(@RequestHeader("name") String fileName, @RequestHeader("type") String type) {
        try {
            return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName.replaceAll(" ", "-") + ".csv")
            .contentType(MediaType.parseMediaType("application/csv"))
            .body(rService.getDataInCSVFormat(fileName, type));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
