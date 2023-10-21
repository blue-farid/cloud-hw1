package com.bluefarid.cloud.bank.bankoperator.exception;

import java.util.function.Supplier;

public class CustomerNotFoundException extends RuntimeException{
    public CustomerNotFoundException(String message) {
        super(message);
    }
}
