package com.nimbleways.springboilerplate.exceptions;

import com.nimbleways.springboilerplate.dto.ErrorCode;
import com.nimbleways.springboilerplate.dto.ErrorMessage;
import com.nimbleways.springboilerplate.dto.enums.ErrorCodesEnum;
import org.springframework.http.HttpStatus;

public class OrderException  extends BaseException{

    private static final ErrorCode ERROR_CODE = ErrorCode.from(ErrorCodesEnum.ORDER_NOT_FOUND.toString());
    private static final HttpStatus HTTP_STATUS = HttpStatus.NOT_FOUND;
    private static final ErrorMessage DEFAULT_MESSAGE = ErrorMessage.from("Category not found");

    public OrderException(){
        super(ERROR_CODE, HTTP_STATUS, DEFAULT_MESSAGE, null);
    }
    public OrderException(final String errorCode, final HttpStatus httpStatus, final String errorMessage) {
        super(ErrorCode.from(errorCode), httpStatus, ErrorMessage.from(errorMessage), null);
    }
}
