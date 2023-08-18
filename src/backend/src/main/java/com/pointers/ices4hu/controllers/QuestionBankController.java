package com.pointers.ices4hu.controllers;

import com.pointers.ices4hu.requests.QuestionBankSaveRequest;
import com.pointers.ices4hu.responses.MessageResponse;
import com.pointers.ices4hu.services.QuestionBankService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/question_bank")
public class QuestionBankController {

    private final QuestionBankService questionBankService;

    public QuestionBankController(QuestionBankService questionBankService) {
        this.questionBankService = questionBankService;
    }

    @PostMapping("/instructor/save")
    @PreAuthorize("hasAuthority('instructor')")
    public ResponseEntity<MessageResponse> save(@RequestParam String user,
                                                @RequestBody QuestionBankSaveRequest questionBankSaveRequest) {
        return questionBankService.save(user, questionBankSaveRequest);
    }

    @GetMapping("/instructor")
    @PreAuthorize("hasAuthority('instructor')")
    public ResponseEntity<Object> getQuestionBank(@RequestParam String user) {
        return questionBankService.getQuestionBank(user);
    }

}
