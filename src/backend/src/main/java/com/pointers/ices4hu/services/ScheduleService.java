package com.pointers.ices4hu.services;

import com.pointers.ices4hu.models.Schedule;
import com.pointers.ices4hu.repositories.ScheduleRepository;
import com.pointers.ices4hu.responses.MessageResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public ResponseEntity<Object> getSchedule() {
        Schedule schedule = scheduleRepository.findById(1L).orElse(null);
        if (schedule == null) {
            schedule = new Schedule();
            schedule.setId(1L);
            scheduleRepository.save(schedule);
        }

        return new ResponseEntity<>(schedule, HttpStatus.OK);
    }

    public ResponseEntity<Object> updateSchedule(Schedule schedule) {

        if (schedule.getEndDate() == null || schedule.getStartDate() == null
                || schedule.getGradeFinalizationDate() == null) {
            return new ResponseEntity<>(new MessageResponse("Not all dates are specified!"),
                    HttpStatus.BAD_REQUEST);
        }

        Schedule foundSchedule = scheduleRepository.findById(1L).orElse(null);
        if (foundSchedule != null && (foundSchedule.getGradeFinalizationDate() != null
            && foundSchedule.getEndDate() != null && foundSchedule.getStartDate() != null)) {
            return new ResponseEntity<>(new MessageResponse("The schedule has already been set!"),
                    HttpStatus.BAD_REQUEST);
        }

        schedule.setId(1L);
        scheduleRepository.save(schedule);

        return new ResponseEntity<>(new MessageResponse("Operation successful!"), HttpStatus.OK);
    }
}
