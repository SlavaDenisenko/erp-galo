package com.denisenko.reportservice.mapper.config;

import java.util.List;

public interface DtoMapper<D, E> {
    D toDTO(E e);

    List<D> toDTO(List<? extends E> e);
}
