package com.pointers.ices4hu.types;

public enum RequestStatusType {

    PENDING((byte)0, "Pending"),
    APPROVED((byte)1, "Approved"),
    DENIED((byte)2, "Denied");

    private Byte value;
    private String name;

    private RequestStatusType(Byte value, String name) {
        this.value = value;
        this.name = name;
    }

    public Byte getValue() {
        return value;
    }

    public void setValue(Byte value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
