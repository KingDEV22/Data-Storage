package com.data.organization.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.stereotype.Service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.data.organization.dto.FormRequest;
import com.data.organization.dto.Question;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FormHandling {

    private String generateHTMLFormTemplate(List<Question> questions, String formName, String country) {
        StringBuilder htmlBuilder = new StringBuilder();
        String state = "OK";
        htmlBuilder.append("<!DOCTYPE html>\n");
        htmlBuilder.append("<html>\n");
        htmlBuilder.append("<head>\n");
        htmlBuilder.append("<title>" + formName + "</title>\n");
        // Add JavaScript function to send form data to Lambda
        htmlBuilder.append("<script>\n");
        htmlBuilder.append("function submitFormToLambda() {\n");
        htmlBuilder.append("  const form = document.getElementById('myForm');\n");
        htmlBuilder.append("  const formData = new FormData(form);\n");
        htmlBuilder.append("  const data = {};\n");
        htmlBuilder.append("  data['name'] = " + formName + "\n");
        htmlBuilder.append("  data['country'] = " + country + "\n");
        htmlBuilder.append("  data['state'] = " + state + "\n");
        htmlBuilder.append("  formData.forEach((value, key) => data[key] = value);\n");
        htmlBuilder.append("  fetch('https://lambda-function-endpoint', {\n");
        htmlBuilder.append("    method: 'POST',\n");
        htmlBuilder.append("    headers: {\n");
        htmlBuilder.append("      'Content-Type': 'application/json'\n");
        htmlBuilder.append("    },\n");
        htmlBuilder.append("    body: JSON.stringify(data)\n");
        htmlBuilder.append("  }).then(response => {\n");
        htmlBuilder.append("    // Handle response as needed\n");
        htmlBuilder.append("  }).catch(error => {\n");
        htmlBuilder.append("    console.error('Error sending form data:', error);\n");
        htmlBuilder.append("  });\n");
        htmlBuilder.append("}\n");
        htmlBuilder.append("</script>\n");
        htmlBuilder.append("</head>\n");
        htmlBuilder.append("<body>\n");
        htmlBuilder.append("<h1>" + formName + "</h1>\n");
        // Add the form and questions dynamically
        htmlBuilder.append("<form id=\"myForm\">\n");
        for (Question question : questions) {
            htmlBuilder.append("  <label for=\"" + question.getName() + "\">" + question.getLabel() + "</label>\n");
            if ("textarea".equals(question.getType())) {
                htmlBuilder.append("  <textarea id=\"" + question.getName() + "\" name=\"" + question.getName()
                        + "\" rows=\"4\" cols=\"50\"></textarea><br><br>\n");
            } else {
                htmlBuilder.append("  <input type=\"" + question.getType() + "\" id=\"" + question.getName()
                        + "\" name=\"" + question.getName() + "\"><br><br>\n");
            }
        }
        htmlBuilder.append("   <button type=\"button\" onclick=\"submitFormToLambda()\">Submit</button>\n");
        htmlBuilder.append("</form>\n");
        htmlBuilder.append("</body>\n");
        htmlBuilder.append("</html>\n");

        return htmlBuilder.toString();
    }

    private String uploadToS3AndReturnUrl(String bucketName, String s3Key, String htmlContent) throws IOException {
        AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
                .build();

        byte[] contentAsBytes = htmlContent.getBytes(StandardCharsets.UTF_8);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(contentAsBytes);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(contentAsBytes.length);
        metadata.setContentType("text/html");

        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, s3Key, inputStream, metadata);
            s3Client.putObject(putObjectRequest);

            // Get the public URL of the uploaded object
            String s3FileUrl = s3Client.getUrl(bucketName, s3Key).toString();

            return s3FileUrl;
        } catch (AmazonServiceException e) {
            log.error(e.getMessage());
        } catch (AmazonClientException e) {
            log.error(e.getMessage());
        }

        return null;
    }

    public String createform(FormRequest formRequest) {
        String formTemplate = generateHTMLFormTemplate(formRequest.getQuestions(), formRequest.getFormName(),
                formRequest.getCountry());
        String bucketName = "your-s3-bucket-name";
        String s3Key = "form_template.html";
        String formurl = "";
        try {
            formurl = uploadToS3AndReturnUrl(bucketName, s3Key, formTemplate);
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        return formurl;

    }

}
