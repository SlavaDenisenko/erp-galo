package com.denisenko.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Report {
    private String name;
    private String description;
    private Object value;

    public Report(String name, String description, Object value) {
        this.name = name;
        this.description = description;
        this.value = value;
    }
}
