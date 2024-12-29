package com.avi.eCommerce.exceptions;

public class CategoryAlreadyException extends RuntimeException {
    public CategoryAlreadyException(String message) {
        super(message);
    }
}
