package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// import com.upgrad.quora.service.dao.UserDao;
//import com.upgrad.quora.service.entity.UserAuthTokenEntity;
//import com.upgrad.quora.service.entity.UserEntity;
//import com.upgrad.quora.service.exception.UploadFailedException;


@Service
public class QuestionService {
    @Autowired
    private QuestionDao questionDao;
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity) {
        questionDao.createQuestion(questionEntity);
        return questionEntity;
    }

    @Transactional
    public List<QuestionEntity> getAllQuestions() {
        return questionDao.getAllQuestions();
    }

    @Transactional
    public QuestionEntity getQuestionById(String question_id) throws InvalidQuestionException {

        QuestionEntity questionEntity = questionDao.getQuestionById(question_id);
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        return questionEntity;
    }


    @Transactional
    public void editQuestionContent(UserEntity signedinUser, QuestionEntity questionEntity) throws AuthorizationFailedException, InvalidQuestionException {

        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        //UserEntity questionOwner = questionEntity.getUser();
        UserEntity questionOwner = questionEntity.getUserEntity();
        if (!questionOwner.getUuid().equals(signedinUser.getUuid())) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
        }
        questionDao.editQuestionContent(questionEntity);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteQuestion(UserEntity signedinUser, QuestionEntity questionEntity) throws InvalidQuestionException, AuthorizationFailedException {
        if (questionEntity == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        String role = signedinUser.getRole();
        UserEntity questionOwner = questionEntity.getUserEntity();
        if (!(questionOwner.getUuid().equals(signedinUser.getUuid()) || role.equals("admin"))) {
            throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
        }
        questionDao.deleteQuestion(questionEntity);
    }

    @Transactional
    public List<QuestionEntity> getQuestionsByUser(UserEntity userEntity) {
        return questionDao.getQuestionsByUser(userEntity);
    }
}



