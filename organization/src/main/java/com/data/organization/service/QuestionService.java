package com.data.organization.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.data.organization.dto.QuestionResponse;
import com.data.organization.model.Form;
import com.data.organization.model.OrgUser;
import com.data.organization.model.Question;
import com.data.organization.repository.QuestionRepo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class QuestionService {

    @Autowired
    private QuestionRepo qRepo;

    @Autowired
    private FormUtilService fUtilService;

    public void saveQuestion(List<QuestionResponse> questions, String fId) {
        List<Question> questionData = questions.stream()
                .map(question -> new Question(question.getName(), question.getLabel(), question.getType(), fId))
                .collect(Collectors.toList());
        log.info("Questions list created");
        qRepo.saveAll(questionData);
        log.info("questions saved");
    }

    public List<QuestionResponse> getQuestionByForm(String name) {
        Form fdata = fUtilService.getFormMetaData(name);
        List<Question> questions = qRepo.findAllByfId(fdata.getFId());
        return questions.stream()
                .map(question -> new QuestionResponse(question.getQname(), question.getQtype(), question.getQlabel()))
                .collect(Collectors.toList());
    }

}
