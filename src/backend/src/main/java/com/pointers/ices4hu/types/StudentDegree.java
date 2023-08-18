package com.pointers.ices4hu.types;

public enum StudentDegree {

    UNDERGRADUATE((byte)0, "Undergraduate"),
    GRADUATE((byte)1, "Graduate");

    private Byte value;
    private String name;

    private StudentDegree(Byte value, String name) {
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
