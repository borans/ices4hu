package com.pointers.ices4hu.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;

@Entity
@Table(name="course_enrolment_request")
public class CourseEnrolmentRequest {

    @Id
    @SequenceGenerator(
            name = "course_enrolment_request_sequence",
            sequenceName = "course_enrolment_request_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "course_enrolment_request_sequence"
    )
    private Long requestId;

    private Byte status;

    private LocalDateTime requestDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="course_id")
    @OnDelete(action = OnDeleteAction.CASCADE) //user silinirse course request
    @JsonIgnore
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="student_id")
    @OnDelete(action = OnDeleteAction.CASCADE) //user silinirse course request
    @JsonIgnore
    private User user;

    public Long getRequestId() {
        return requestId;
    }

    public void setRequestId(Long requestId) {
        this.requestId = requestId;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public LocalDateTime getRequestDateTime() {
        return requestDateTime;
    }

    public void setRequestDateTime(LocalDateTime requestDateTime) {
        this.requestDateTime = requestDateTime;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
