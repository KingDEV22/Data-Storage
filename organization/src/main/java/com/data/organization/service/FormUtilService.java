package com.data.organization.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.data.organization.dto.FormRequest;
import com.data.organization.dto.QuestionRequest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FormUtilService {

    private static final String inputFileName = "index.txt";

    private String generateHTMLFormTemplate(List<QuestionRequest> questions, String formName, String country)
            throws IOException {
        StringBuilder htmlBuilder = new StringBuilder();

        BufferedReader br = new BufferedReader(new FileReader(inputFileName));
        String line;
        while ((line = br.readLine()) != null) {
            switch (line) {
                case "<!-- Add Line 4 -->":
                    htmlBuilder.append("<title>").append(formName).append("</title>");
                    break;
                case "<!-- Add Line 21 -->":
                    htmlBuilder.append("<form id=\"").append(formName).append("\">\n");
                    for (QuestionRequest question : questions) {
                        htmlBuilder.append("  <label for=\"").append(question.getName()).append("\">")
                                .append(question.getLabel()).append("</label>\n");
                        if ("textarea".equals(question.getType())) {
                            htmlBuilder.append("  <textarea id=\"").append(question.getName())
                                    .append("\" name=\"").append(question.getName())
                                    .append("\" rows=\"4\" cols=\"50\"></textarea><br><br>\n");
                        } else {
                            htmlBuilder.append("  <input type=\"").append(question.getType()).append("\" id=\"")
                                    .append(question.getName()).append("\" name=\"").append(question.getName())
                                    .append("\"><br><br>\n");
                        }
                    }
                    break;
                case "// Add Line 23":
                    htmlBuilder.append("document.getElementById(\"").append(formName)
                            .append("\").addEventListener(\"submit\", function (event) \n");
                    break;
                default:
                    htmlBuilder.append(line + "\n");
            }
        }
        br.close();
        log.info("html templated created");

        return htmlBuilder.toString();
    }

    private String saveHTMLToFile(String htmlContent, String fileName) throws IOException {

        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
        }
        FileWriter fileWriter = new FileWriter(file);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(htmlContent);
        bufferedWriter.close();
        log.info("form created");
        return file.getAbsolutePath();
    }

    public String createform(FormRequest formRequest) throws IOException {
        String formTemplate = generateHTMLFormTemplate(formRequest.getQuestions(), formRequest.getFormName(),
                formRequest.getCountry());
        String formLink =  saveHTMLToFile(formTemplate, (formRequest.getFormName() + ".html"));
        return formLink;

    }

}