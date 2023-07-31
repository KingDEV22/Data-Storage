package com.data.organization.service;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.data.organization.dto.FormRequest;
import com.data.organization.model.Form;
import com.data.organization.model.OrgUser;
import com.data.organization.repository.FormRepo;
import com.data.organization.repository.OrgUserRepo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FormService {

    @Autowired
    private OrgUserRepo oRepository;
    @Autowired
    private FormUtilService fUtilService;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private QuestionService qService;
    @Autowired
    private FormRepo fRepo;

    private String getEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication.getName();
    }

    public String saveFormMetaData(FormRequest fRequest) throws IOException {
        Optional<OrgUser> user = oRepository.findByEmail(getEmail());
        String orgId = "";
        if (user.isPresent()) {
            orgId = user.get().getOrgId();
        } else {
            throw new UsernameNotFoundException("No user found!!");
        }
        log.info("Org User found");
        String formLink = fUtilService.createform(fRequest);
        String fId = orgId + ".f" + (mongoTemplate.count(new Query(), Form.class) + 1);
        Form forMetaData = new Form(fId,
                formLink,
                fRequest.getFormName(),
                orgId);
        fRepo.save(forMetaData);
        log.info("form data saved");
        qService.saveQuestion(fRequest.getQuestions(), fId);
        return formLink;

    }
}
