package com.denisenko.utils;

import io.quarkus.qute.TemplateExtension;

import java.math.BigDecimal;

public class TemplateUtils {

    @TemplateExtension
    public static BigDecimal multiply(Double quantity, BigDecimal price) {
        return BigDecimal.valueOf(quantity).multiply(price);
    }
}
