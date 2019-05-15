package com.seasun.management.dto;

public class IdValueDto {

    private Long id;
    private Float value;

    public IdValueDto() {
    }

    public IdValueDto(Long id, Float value) {
        this.id = id;
        this.value = value;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }
}
