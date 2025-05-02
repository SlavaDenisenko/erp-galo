package com.denisenko.orderservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.math.BigDecimal;

@RedisHash("Recipe")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Recipe {

    @Id
    private Integer id;
    private String name;
    private String description;
    @Indexed
    private Integer categoryId;
    private Integer preparationTime;
    private BigDecimal price;
    private String instructions;
    private String location;
}
