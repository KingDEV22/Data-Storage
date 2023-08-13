package com.data.organization.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.data.organization.dto.QuestionResponse;
import com.data.organization.model.Question;
import com.data.organization.repository.QuestionRepo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class QuestionService {

    @Autowired
    private QuestionRepo qRepo;

    public void saveQuestion(List<QuestionResponse> questions, String fId) {
        List<Question> questionData = questions.stream()
                .map(question -> new Question(question.getName(), question.getLabel(), question.getType(), fId))
                .collect(Collectors.toList());
        log.info("Questions list created");
        qRepo.saveAll(questionData);
        log.info("questions saved");
    }


    @Cacheable(value = "questions" , key = "#fId")
    public List<QuestionResponse> getQuestionByForm(String fId) {
        List<Question> questions = qRepo.findAllByfId(fId);
        return questions.stream()
                .map(question -> new QuestionResponse(question.getQname(), question.getQtype(), question.getQlabel()))
                .collect(Collectors.toList());
    }

}
