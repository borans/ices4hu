package com.pointers.ices4hu.controllers;

import com.pointers.ices4hu.services.ReevaluationRequestService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reevaluation_request")
public class ReevaluationRequestController {

    private final ReevaluationRequestService reevaluationRequestService;

    public ReevaluationRequestController(ReevaluationRequestService reevaluationRequestService) {
        this.reevaluationRequestService = reevaluationRequestService;
    }

    @PostMapping("/instructor")
    @PreAuthorize("hasAuthority('instructor')")
    public ResponseEntity<Object> requestReevaluation(@RequestParam Long survey,
                                                      @RequestParam String user) {
        return reevaluationRequestService.requestReevaluation(survey, user);
    }

    @PostMapping("/department_manager/deny")
    @PreAuthorize("hasAuthority('department_manager')")
    public ResponseEntity<Object> denyReevaluationRequest(@RequestParam Long survey,
                                                          @RequestParam String user) {
        return reevaluationRequestService.denyReevaluationRequest(survey, user);
    }

    @PostMapping("/department_manager/approve")
    @PreAuthorize("hasAuthority('department_manager')")
    public ResponseEntity<Object> approveReevaluationRequest(@RequestParam Long survey,
                                                             @RequestParam String user) {
        return reevaluationRequestService.approveReevaluationRequest(survey, user);
    }

}
