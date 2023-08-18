package com.pointers.ices4hu.types;

public enum UserType {
    ADMIN((byte)0, "Admin"),
    STUDENT((byte)1, "Student"),
    DEPARTMENT_MANAGER((byte)2, "Department Manager"),
    INSTRUCTOR((byte)3, "Instructor");

    private Byte value;
    private String name;

    private UserType(Byte value, String name) {
        this.value = value;
        this.name = name;
    }

    public Byte getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

}
