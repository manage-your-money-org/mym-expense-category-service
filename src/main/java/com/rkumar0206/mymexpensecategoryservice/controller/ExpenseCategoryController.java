package com.rkumar0206.mymexpensecategoryservice.controller;

import com.rkumar0206.mymexpensecategoryservice.contanstsAndEnums.Constants;
import com.rkumar0206.mymexpensecategoryservice.contanstsAndEnums.ErrorMessageConstants;
import com.rkumar0206.mymexpensecategoryservice.exceptions.ExpenseCategoryException;
import com.rkumar0206.mymexpensecategoryservice.model.request.ExpenseCategoryRequest;
import com.rkumar0206.mymexpensecategoryservice.model.response.CustomResponse;
import com.rkumar0206.mymexpensecategoryservice.model.response.ExpenseCategoryResponse;
import com.rkumar0206.mymexpensecategoryservice.service.ExpenseCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mym/api/expensecategories")
public class ExpenseCategoryController {

    @Autowired
    private ExpenseCategoryService expenseCategoryService;

    @PostMapping("/new/create")
    public ResponseEntity<CustomResponse<ExpenseCategoryResponse>> createNewExpenseCategory(@RequestBody ExpenseCategoryRequest expenseCategoryRequest) {

        CustomResponse<ExpenseCategoryResponse> response = new CustomResponse<>();

        if (expenseCategoryRequest.isValid()) {

            try {

                ExpenseCategoryResponse expenseCategory = expenseCategoryService.createExpenseCategory(expenseCategoryRequest);

                response.setCode(HttpStatus.CREATED.value());
                response.setMessage("Success");
                response.setBody(expenseCategory);

            } catch (Exception e) {

                if (e instanceof ExpenseCategoryException) {

                    if (e.getMessage().contains(ErrorMessageConstants.CATEGORY_NAME_ALREADY_PRESENT_ERROR))
                        response.setCode(HttpStatus.CONFLICT.value());
                    else
                        response.setCode(HttpStatus.BAD_REQUEST.value());
                }

                response.setMessage(String.format(Constants.FAILED_, e.getMessage()));
            }
        } else {

            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(String.format(Constants.FAILED_, ErrorMessageConstants.INVALID_REQUEST_BODY));
        }

        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }
}
