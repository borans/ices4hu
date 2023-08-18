package com.pointers.ices4hu.controllers;

import com.pointers.ices4hu.models.Schedule;
import com.pointers.ices4hu.services.ScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('admin', 'department_manager', 'instructor', 'student')")
    public ResponseEntity<Object> getSchedule() {
        return scheduleService.getSchedule();
    }

    @PutMapping("/admin")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Object> updateSchedule(@RequestBody Schedule schedule) {
        return scheduleService.updateSchedule(schedule);
    }

}
