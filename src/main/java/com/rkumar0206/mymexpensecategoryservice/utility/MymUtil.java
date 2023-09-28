package com.rkumar0206.mymexpensecategoryservice.utility;

import com.rkumar0206.mymexpensecategoryservice.contanstsAndEnums.Constants;
import com.rkumar0206.mymexpensecategoryservice.contanstsAndEnums.ErrorMessageConstants;
import com.rkumar0206.mymexpensecategoryservice.model.response.CustomResponse;
import org.springframework.http.HttpStatus;

public class MymUtil {

    public static boolean isValid(String str) {

        return str != null && !str.trim().isEmpty();
    }

    public static boolean isNotValid(String str) {

        return !isValid(str);
    }

    public static void setAppropriateResponseStatus(CustomResponse response, Exception ex) {

        if (ex.getMessage().startsWith("Max page size should be less than or equal to")) {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        } else {

            switch (ex.getMessage()) {

                case ErrorMessageConstants.PERMISSION_DENIED, ErrorMessageConstants.USER_NOT_AUTHORIZED_ERROR, ErrorMessageConstants.USER_INFO_NOT_PROVIDED_ERROR, ErrorMessageConstants.USER_INFO_NOT_PROPER ->
                        response.setStatus(HttpStatus.FORBIDDEN.value());

                case ErrorMessageConstants.INVALID_EXPENSE_CATEGORY_KEY, ErrorMessageConstants.INVALID_REQUEST_BODY_CREATE, ErrorMessageConstants.INVALID_REQUEST_BODY_UPDATE ->
                        response.setStatus(HttpStatus.BAD_REQUEST.value());

                case ErrorMessageConstants.CATEGORY_NAME_ALREADY_PRESENT_ERROR ->
                        response.setStatus(HttpStatus.CONFLICT.value());

                case ErrorMessageConstants.NO_CATEGORY_FOUND_ERROR -> response.setStatus(HttpStatus.NO_CONTENT.value());

                default -> response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }
        }

        response.setMessage(String.format(Constants.FAILED_, ex.getMessage()));
    }

}
