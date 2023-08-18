package com.pointers.ices4hu.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Date;
@Entity
@Table(name="schedule")
public class Schedule {

    @Id
    private Long id;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private LocalDateTime gradeFinalizationDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getGradeFinalizationDate() {
        return gradeFinalizationDate;
    }

    public void setGradeFinalizationDate(LocalDateTime gradeFinalizationDate) {
        this.gradeFinalizationDate = gradeFinalizationDate;
    }
}
