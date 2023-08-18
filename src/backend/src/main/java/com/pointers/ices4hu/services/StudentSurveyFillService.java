package com.pointers.ices4hu.services;

import com.pointers.ices4hu.models.StudentSurveyFill;
import com.pointers.ices4hu.repositories.StudentSurveyFillRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class StudentSurveyFillService {

    private final SurveyService surveyService;
    private final UserService userService;
    private final StudentSurveyFillRepository studentSurveyFillRepository;

    public StudentSurveyFillService(SurveyService surveyService,
                                    UserService userService,
                                    StudentSurveyFillRepository studentSurveyFillRepository) {
        this.surveyService = surveyService;
        this.userService = userService;
        this.studentSurveyFillRepository = studentSurveyFillRepository;
    }

    public StudentSurveyFill getStudentSurveyFillByStudentAndSurveyIds(String studentLoginId, Long surveyId) {
        return studentSurveyFillRepository.getStudentSurveyFillByStudentLoginIdAndSurveyId(studentLoginId, surveyId);
    }

    public StudentSurveyFill getStudentSurveyFillByStudentAndSurveyIdsNotNull(String studentLoginId, Long surveyId,
                                                                              int initialTrialCount) {
        StudentSurveyFill studentSurveyFill = getStudentSurveyFillByStudentAndSurveyIds(studentLoginId, surveyId);

        if (studentSurveyFill == null) {
            studentSurveyFill = new StudentSurveyFill();
            studentSurveyFill.setSurvey(surveyService.getSurvey(surveyId));
            studentSurveyFill.setUser(userService.getUserByLoginID(studentLoginId));
            studentSurveyFill.setCompletionDatetime(null);
            studentSurveyFill.setTrialCount(initialTrialCount);
        }

        return studentSurveyFill;
    }

    public void saveStudentSurveyFill(StudentSurveyFill studentSurveyFill) {
        studentSurveyFillRepository.save(studentSurveyFill);
    }
}
