package com.shoppy.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class APIResponseErrorMessage {

    private String error;

}
