package com.data.organization.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.data.organization.dto.FormRequest;
import com.data.organization.dto.QuestionResponse;
import com.data.organization.model.Form;
import com.data.organization.model.OrgUser;
import com.data.organization.repository.FormRepo;
import com.data.organization.repository.OrgUserRepo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FormUtilService {

    @Autowired
    private OrgUserRepo oRepository;

    @Autowired
    private FormRepo fRepo;

    private final String inputFileName = "html" + File.separator + "base" + File.separator + "index.txt";
    private final String formsaveName = "html" + File.separator + "forms";

    @Cacheable(cacheNames = "form", key = "#name")
    public Form getFormMetaData(String name) {
        return fRepo.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Form not found!!!"));
    }

    @Cacheable(value = "users", key = "#email")
    public OrgUser getOrgUser(String email) {
        return oRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("No user found!!"));
    }

    private String generateHTMLFormTemplate(List<QuestionResponse> questions, String formName, String country)
            throws IOException {
        try (BufferedReader br = new BufferedReader(new FileReader(inputFileName))) {
            StringBuilder htmlBuilder = new StringBuilder(br.lines().collect(Collectors.joining("\n")));

            String formHeader = String.format("<title>%s</title>\n", formName);
            StringBuilder formFields = new StringBuilder(String.format("<form id=\"%s\">\n", formName));

            for (QuestionResponse question : questions) {
                String inputTag;
                if ("textarea".equals(question.getType())) {
                    inputTag = String.format(
                            "  <textarea id=\"%s\" name=\"%s\" rows=\"4\" cols=\"50\"></textarea><br><br>\n",
                            question.getName(), question.getName());
                } else {
                    inputTag = String.format("  <input type=\"%s\" id=\"%s\" name=\"%s\"><br><br>\n",
                            question.getType(), question.getName(), question.getName());
                }
                formFields.append(
                        String.format("  <label for=\"%s\">%s</label>\n", question.getName(), question.getLabel()));
                formFields.append(inputTag);
            }

            String scriptTag = String.format(
                    "document.getElementById(\"%s\").addEventListener(\"submit\", function (event) \n", formName);

            htmlBuilder = htmlBuilder.replace(htmlBuilder.indexOf("<!-- Add title -->"),
                    htmlBuilder.indexOf("<!-- Add title -->") + "<!-- Add title -->".length(), formHeader);
            htmlBuilder = htmlBuilder.replace(htmlBuilder.indexOf("<!-- Add form body -->"),
                    htmlBuilder.indexOf("<!-- Add form body -->") + "<!-- Add form body -->".length(),
                    formFields.toString());
            htmlBuilder = htmlBuilder.replace(htmlBuilder.indexOf("// Add script id"),
                    htmlBuilder.indexOf("// Add script id") + "// Add script id".length(), scriptTag);

            log.info("HTML template created");

            return htmlBuilder.toString();
        }
    }

    private String saveHTMLToFile(String htmlContent, String fileName) throws IOException {
        File file = new File(formsaveName, fileName);
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
        String formLink = saveHTMLToFile(formTemplate, (formRequest.getFormName() + ".html"));
        return formLink;

    }

}
