package com.denisenko.inventoryservice.exception;

public class CategoryTypeModificationNotAllowedException extends RuntimeException {

    public CategoryTypeModificationNotAllowedException(String message) {
        super(message);
    }

    public static CategoryTypeModificationNotAllowedException forCategoryId(Integer id) {
        return new CategoryTypeModificationNotAllowedException(
                "Modification of category type is not allowed for category with ID = " + id + ". Please delete and create a new one with the desired type."
        );
    }
}
