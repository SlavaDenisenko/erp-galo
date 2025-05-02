package com.denisenko.orderservice.mapper.config;

import java.util.List;

public interface EntityMapper<D, E> {
    E toEntity(D d);

    List<E> toEntity(List<D> d);
}
