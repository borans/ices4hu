package com.pointers.ices4hu.controllers;

import com.pointers.ices4hu.models.*;
import com.pointers.ices4hu.requests.AnswerSurveyRequest;
import com.pointers.ices4hu.requests.SurveyRequest;
import com.pointers.ices4hu.responses.MessageResponse;
import com.pointers.ices4hu.responses.SurveyResponse;
import com.pointers.ices4hu.responses.SurveyResponseRemainingDays;
import com.pointers.ices4hu.services.CourseService;
import com.pointers.ices4hu.services.StudentSurveyFillService;
import com.pointers.ices4hu.services.SurveyService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.annotation.RequestScope;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static java.time.temporal.ChronoUnit.DAYS;

@RestController
@RequestMapping("/api/survey")
public class SurveyController {

    private final SurveyService surveyService;
    private final StudentSurveyFillService studentSurveyFillService;
    private final CourseService courseService;

    public SurveyController(SurveyService surveyService, StudentSurveyFillService studentSurveyFillService,
                            CourseService courseService) {
        this.surveyService = surveyService;
        this.studentSurveyFillService = studentSurveyFillService;
        this.courseService = courseService;
    }

    @GetMapping("/student")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<Object> getSurveysOfStudent(@RequestParam String user) {
        surveyService.autoSubmitStudentAnswers();
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals(user))
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"), HttpStatus.UNAUTHORIZED);

        List<Survey> surveys = surveyService.getSurveysOfStudent(user);
        List<SurveyResponse> surveyResponses = new ArrayList<>();
        for (Survey survey: surveys) {
            StudentSurveyFill studentSurveyFill;
            studentSurveyFill = studentSurveyFillService.getStudentSurveyFillByStudentAndSurveyIds(
                    user, survey.getId());

            if (studentSurveyFill == null) {
                studentSurveyFill = new StudentSurveyFill();
                studentSurveyFill.setTrialCount(0);
                studentSurveyFill.setCompletionDatetime(null);
            }

            SurveyResponseRemainingDays surveyResponse = new SurveyResponseRemainingDays();
            surveyResponse.setId(survey.getId());
            surveyResponse.setCreationDatetime(survey.getCreationDatetime());
            surveyResponse.setStartingDatetime(survey.getStartingDatetime());
            surveyResponse.setDeadline(survey.getDeadline());

            if (survey.getDeadline() != null && survey.getDeadline().isAfter(LocalDateTime.now())) {
                surveyResponse.setDaysUntilDeadline((int)DAYS.between(LocalDateTime.now(), survey.getDeadline()));
            } else {
                surveyResponse.setDaysUntilDeadline(0);
            }

            // surveyResponse.setTrialCount(survey.getTrialCount());
            Course course = survey.getCourse();
            User instructor = survey.getUserInstructor();
            if (course != null) {
                instructor = course.getUser();
                surveyResponse.setCourseId(course.getId());
                surveyResponse.setCourseCode(course.getCode());
                surveyResponse.setCourseName(course.getName());
                surveyResponse.setSurveyType("Course");

            } else if (instructor != null) {
                surveyResponse.setCourseId(null);
                surveyResponse.setCourseCode(null);
                surveyResponse.setCourseName(null);
                surveyResponse.setSurveyType("Instructor");
            } else {
                // throw new RuntimeException("Both course and instructor cannot be null!");
                continue;
            }

            surveyResponse.setInstructorId(instructor.getId());
            surveyResponse.setInstructorName(instructor.getFullName());

            if (studentSurveyFill.getCompletionDatetime() != null) {
                surveyResponse.setStatus("DONE");
            } else {
                surveyResponse.setStatus("NOT DONE");
            }

            surveyResponse.setTrialCount(studentSurveyFill.getTrialCount());

            surveyResponses.add(surveyResponse);
        }

        return new ResponseEntity<>(surveyResponses, HttpStatus.OK);
    }

    @GetMapping("/student/view")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<Object> getQuestionsOfSurveyWithAnswers(@RequestParam Long survey,
                                                                   @RequestParam String student) {
        surveyService.autoSubmitStudentAnswers();
        return surveyService.getQuestionsOfSurveyWithAnswers(survey, student);
    }

    @GetMapping("/student/upcoming")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<Object> getUpcomingSurveysOfStudent(@RequestParam String user) {
        surveyService.autoSubmitStudentAnswers();
        return surveyService.getUpcomingSurveysOfStudent(user);
    }

    @GetMapping("/instructor/unsubmitted")
    @PreAuthorize("hasAuthority('instructor')")
    public ResponseEntity<Object> getUnsubmittedSurveysOfInstructor(@RequestParam String user) {
        surveyService.autoSubmitStudentAnswers();
        return surveyService.getUnsubmittedSurveysOfInstructor(user);
    }

    @PostMapping("/student/answer")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<Object> answerSurvey(@RequestParam String student,
                                       @RequestParam Long survey,
                                       @RequestBody AnswerSurveyRequest answerSurveyRequest) {
        surveyService.autoSubmitStudentAnswers();
        ResponseEntity<Object> returnValue = surveyService.checkIfStudentIsBanned(student);
        if (returnValue != null)
            return returnValue;

        returnValue = surveyService.checkIfEnoughStudentsExist(survey);
        if (returnValue != null)
            return returnValue;

        StudentSurveyFill studentSurveyFill = studentSurveyFillService.getStudentSurveyFillByStudentAndSurveyIdsNotNull(
                student, survey, 0);

        if (studentSurveyFill.getCompletionDatetime() != null) {
            return new ResponseEntity<>(new MessageResponse("The survey has already been completed by the student!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (studentSurveyFill.getTrialCount() >= 2) {
            return new ResponseEntity<>(new MessageResponse("Trial count was exceeded!"), HttpStatus.BAD_REQUEST);
        }

        surveyService.answerSurvey(student, answerSurveyRequest);
        studentSurveyFill.setTrialCount(studentSurveyFill.getTrialCount() + 1);
        studentSurveyFillService.saveStudentSurveyFill(studentSurveyFill);
        return new ResponseEntity<>(new MessageResponse("Answers were saved successfully."), HttpStatus.OK);
    }

    @PostMapping("/student/submit")
    @PreAuthorize("hasAuthority('student')")
    public ResponseEntity<Object> submitSurvey(@RequestParam String student,
                                               @RequestParam Long survey,
                                               @RequestBody AnswerSurveyRequest answerSurveyRequest) {
        surveyService.autoSubmitStudentAnswers();
        ResponseEntity<Object> returnValue = surveyService.checkIfStudentIsBanned(student);
        if (returnValue != null)
            return returnValue;

        returnValue = surveyService.checkIfEnoughStudentsExist(survey);
        if (returnValue != null)
            return returnValue;

        int minimumAnsweredQuestions = 8;
        boolean check = surveyService.checkAnswerSurveyRequestRequirements(answerSurveyRequest,
                minimumAnsweredQuestions);
        if (!check) {
            return new ResponseEntity<>(new MessageResponse(String.format("At least %d questions have to be answered!",
                    minimumAnsweredQuestions)), HttpStatus.BAD_REQUEST);
        }



        StudentSurveyFill studentSurveyFill = studentSurveyFillService.getStudentSurveyFillByStudentAndSurveyIdsNotNull(
                student, survey, 0);

        if (studentSurveyFill.getCompletionDatetime() != null) {
            return new ResponseEntity<>(new MessageResponse("The survey has already been completed by the student!"),
                    HttpStatus.BAD_REQUEST);
        }

        surveyService.answerSurvey(student, answerSurveyRequest);
        studentSurveyFill.setTrialCount(studentSurveyFill.getTrialCount() + 1);
        studentSurveyFill.setCompletionDatetime(LocalDateTime.now());
        studentSurveyFillService.saveStudentSurveyFill(studentSurveyFill);

        User user = studentSurveyFill.getUser();
        if (user != null) {
            SurveyResponse surveyResponse = new SurveyResponse();
            Survey surveyObj = studentSurveyFill.getSurvey();
            surveyResponse.buildFrom(surveyObj);
            surveyService.sendSubmissionMailToStudent(user, surveyObj, surveyResponse);
        }

        return new ResponseEntity<>(new MessageResponse("Survey has been submitted successfully!"), HttpStatus.OK);

    }

    @PostMapping("/instructor")
    @PreAuthorize("hasAuthority('instructor')")
    public ResponseEntity<MessageResponse> createSurvey(@RequestParam String user,
                                                        @RequestBody SurveyRequest surveyRequest) {
        surveyService.autoSubmitStudentAnswers();
        return surveyService.createSurvey(user, surveyRequest);
    }

    @GetMapping("/instructor")
    @PreAuthorize("hasAuthority('instructor')")
    public ResponseEntity<Object> getSurveysOfInstructor(@RequestParam String user) {
        surveyService.autoSubmitStudentAnswers();
        return surveyService.getSurveysOfInstructor(user);
    }

    @DeleteMapping("/instructor")
    @PreAuthorize("hasAuthority('instructor')")
    public ResponseEntity<Object> removeSurveyOfInstructor(@RequestParam Long survey,
                                                           @RequestParam String user) {
        surveyService.autoSubmitStudentAnswers();
        return surveyService.removeSurveyOfInstructor(survey, user);
    }

    @GetMapping("/instructor/view")
    @PreAuthorize("hasAuthority('instructor')")
    public ResponseEntity<Object> getQuestionsOfSurveyForInstructor(
            @RequestParam Long survey, @RequestParam String instructor) {
        surveyService.autoSubmitStudentAnswers();
        return surveyService.getQuestionsOfSurveyForInstructor(survey, instructor);
    }

    @PostMapping("/instructor/save")
    @PreAuthorize("hasAuthority('instructor')")
    public ResponseEntity<Object> saveSurvey(@RequestParam Long survey,
                                             @RequestParam String instructor,
                                             @RequestBody SurveyRequest surveyRequest) {
        surveyService.autoSubmitStudentAnswers();
        return surveyService.saveSurvey(survey, instructor, surveyRequest);
    }

    @PostMapping("/instructor/submit")
    @PreAuthorize("hasAuthority('instructor')")
    public ResponseEntity<Object> submitSurvey(@RequestParam Long survey,
                                               @RequestParam String instructor,
                                               @RequestBody SurveyRequest surveyRequest) {
        surveyService.autoSubmitStudentAnswers();
        return surveyService.submitSurvey(survey, instructor, surveyRequest);
    }

    @PutMapping("/instructor/extend")
    @PreAuthorize("hasAuthority('instructor')")
    public ResponseEntity<Object> extendSurvey(@RequestParam Long survey,
                                               @RequestParam String instructor,
                                               @RequestBody SurveyRequest surveyRequest) {
        surveyService.autoSubmitStudentAnswers();
        return surveyService.extendSurvey(survey, instructor, surveyRequest);
    }

    @GetMapping("/department_manager")
    @PreAuthorize("hasAuthority('department_manager')")
    public ResponseEntity<Object> getSurveysOfDepartmentManager(@RequestParam String user) {
        surveyService.autoSubmitStudentAnswers();
        return surveyService.getSurveysOfDepartmentManager(user);
    }

    @GetMapping("/department_manager/view")
    @PreAuthorize("hasAuthority('department_manager')")
    public ResponseEntity<Object> getQuestionsOfSurveyForDepartmentManager(@RequestParam Long survey,
                                                                           @RequestParam String departmentManager) {
        surveyService.autoSubmitStudentAnswers();
        return surveyService.getQuestionsOfSurveyForDepartmentManager(survey, departmentManager);
    }

    @GetMapping("/department_manager/submitter_students")
    @PreAuthorize("hasAuthority('department_manager')")
    public ResponseEntity<Object> getSubmitterStudents(@RequestParam Long survey,
                                                       @RequestParam String user) {
        surveyService.autoSubmitStudentAnswers();
        return surveyService.getSubmitterStudents(survey, user);
    }

    @GetMapping("/department_manager/view_student_answers")
    @PreAuthorize("hasAuthority('department_manager')")
    public ResponseEntity<Object> viewStudentAnswersForDepartmentManager(@RequestParam Long survey,
                                                     @RequestParam String student,
                                                     @RequestParam String departmentManager) {
        surveyService.autoSubmitStudentAnswers();
        return surveyService.viewStudentAnswersForDepartmentManager(survey, student, departmentManager);
    }

    @GetMapping("/admin/view")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Object> getQuestionsOfSurveyForAdmin(@RequestParam Long survey) {
        surveyService.autoSubmitStudentAnswers();
        return surveyService.getQuestionsOfSurveyForAdmin(survey);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Object> getSurveysOfAdmin() {
        surveyService.autoSubmitStudentAnswers();
        return surveyService.getSurveysOfAdmin();
    }


    @PostMapping("/admin/save")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Object> saveSurveyForAdmin(@RequestParam Long survey,
                                             @RequestBody SurveyRequest surveyRequest) {
        surveyService.autoSubmitStudentAnswers();
        return surveyService.saveSurveyForAdmin(survey, surveyRequest);
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasAnyAuthority('admin', 'department_manager', 'instructor')")
    public ResponseEntity<Object> getStatistics(@RequestParam Long survey) {
        surveyService.autoSubmitStudentAnswers();
        return surveyService.getStatistics(survey);
    }
}
