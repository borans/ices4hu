package com.pointers.ices4hu.controllers;

import com.pointers.ices4hu.services.UserEnrolmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user_enrolment")
public class UserEnrolmentController {

    private final UserEnrolmentService userEnrolmentService;

    public UserEnrolmentController(UserEnrolmentService userEnrolmentService) {
        this.userEnrolmentService = userEnrolmentService;
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Object> getPendingUserEnrolmentRequests() {
        return userEnrolmentService.getPendingUserEnrolmentRequests();
    }

    @PostMapping("/admin/approve")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Object> approveUserEnrolmentRequest(@RequestParam Long request) {
        return userEnrolmentService.approveUserEnrolmentRequest(request);
    }

    @PostMapping("/admin/deny")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Object> denyUserEnrolmentRequest(@RequestParam Long request) {
        return userEnrolmentService.denyUserEnrolmentRequest(request);
    }

}
