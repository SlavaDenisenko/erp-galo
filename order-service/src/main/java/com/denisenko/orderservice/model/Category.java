package com.denisenko.orderservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("Category")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Category {

    @Id
    private Integer id;
    private String name;
    private Integer parentCategoryId;
}
