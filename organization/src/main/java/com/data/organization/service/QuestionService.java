package com.data.organization.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.data.organization.dto.QuestionDTO;
import com.data.organization.exception.FormDataException;
import com.data.organization.model.Question;
import com.data.organization.repository.QuestionRepo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class QuestionService {

    @Autowired
    private QuestionRepo qRepo;

    @Transactional
    public void saveQuestion(List<QuestionDTO> questions, String fid) {
        List<Question> questionData = questions.parallelStream()
                .map(question -> new Question(question.getQname(), question.getQlabel(), question.getQtype(), fid))
                .collect(Collectors.toList());
        log.info("Questions list created");
        qRepo.saveAll(questionData);
        log.info("questions saved");
    }

    @Transactional
    public void updateQuestions(List<Question> questionData, String fid) throws Exception {
        List<Question> formQuestions = getAllQuestions(fid);
        for (Question newData : questionData) {
            formQuestions.parallelStream()
                    .filter(existingQuestion -> existingQuestion.getQid().equals(newData.getQid()))
                    .findFirst()
                    .ifPresent(existingQuestion -> {
                        existingQuestion.setQname(newData.getQname());
                        existingQuestion.setQlabel(newData.getQlabel());
                        existingQuestion.setQtype(newData.getQtype());
                    });
        }
        qRepo.saveAll(formQuestions);
    }

    @Transactional
    public void deleteQuestionsByIds(List<String> qids){
        qRepo.deleteAllById(qids);
    }

    public void deleteByFormId(String fid)throws Exception {
        if(fid == null) throw new FormDataException("Form not found!!!");
        qRepo.deleteAllByFid(fid);
    }

    public List<QuestionDTO> getQuestionByForm(String fid) throws Exception {
        return getAllQuestions(fid).parallelStream()
                .map(question -> new QuestionDTO(question.getQid(), question.getQname(), question.getQtype(),
                        question.getQlabel()))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "questions", key = "#fid")
    private List<Question> getAllQuestions(String fid) throws Exception {
        List<Question> questions = qRepo.findAllByFid(fid);
        if (questions.isEmpty())
            throw new FormDataException("No Questions found!!!. Seems an alteration in Form Data");
        return questions;

    }

}
