package com.denisenko.reportservice.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
@Getter
@Setter
public class FilterCriteria {
    private String field;
    @Enumerated(EnumType.STRING)
    private Operator operator;
    private String value;
}
