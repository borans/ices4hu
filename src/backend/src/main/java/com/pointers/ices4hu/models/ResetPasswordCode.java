package com.pointers.ices4hu.models;

import jakarta.persistence.*;

@Entity
@Table(name="reset_password_code")
public class ResetPasswordCode {

    @Id
    @SequenceGenerator(
            name = "reset_password_code_sequence",
            sequenceName = "reset_password_code_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "reset_password_code_sequence"
    )
    private Long id;

    private String loginId;
    private String code;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
