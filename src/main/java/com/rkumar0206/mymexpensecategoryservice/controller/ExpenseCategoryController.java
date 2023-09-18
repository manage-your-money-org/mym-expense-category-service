package com.rkumar0206.mymexpensecategoryservice.controller;

import com.rkumar0206.mymexpensecategoryservice.contanstsAndEnums.Constants;
import com.rkumar0206.mymexpensecategoryservice.contanstsAndEnums.ErrorMessageConstants;
import com.rkumar0206.mymexpensecategoryservice.contanstsAndEnums.Headers;
import com.rkumar0206.mymexpensecategoryservice.contanstsAndEnums.RequestAction;
import com.rkumar0206.mymexpensecategoryservice.domain.ExpenseCategory;
import com.rkumar0206.mymexpensecategoryservice.exceptions.ExpenseCategoryException;
import com.rkumar0206.mymexpensecategoryservice.model.request.ExpenseCategoryRequest;
import com.rkumar0206.mymexpensecategoryservice.model.response.CustomResponse;
import com.rkumar0206.mymexpensecategoryservice.model.response.ExpenseCategoryResponse;
import com.rkumar0206.mymexpensecategoryservice.service.ExpenseCategoryService;
import com.rkumar0206.mymexpensecategoryservice.utility.MymStringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/mym/api/expensecategories")
@Slf4j
public class ExpenseCategoryController {

    @Value("${pagination.maxPageSizeAllowed}")
    private int maxPageSizeAllowed;

    @Value("${pagination.defaultPageSize}")
    private int defaultPageSize;

    @Autowired
    private ExpenseCategoryService expenseCategoryService;

    @GetMapping("/key")
    public ResponseEntity<CustomResponse<ExpenseCategoryResponse>> getExpenseCategoryByKey(
            @RequestHeader(Headers.CORRELATION_ID) String correlationId,
            @RequestParam("key") String key
    ) {

        CustomResponse<ExpenseCategoryResponse> response = new CustomResponse<>();

        try {
            if (null == key || !StringUtils.hasLength(key.trim())) {
                throw new ExpenseCategoryException(ErrorMessageConstants.INVALID_EXPENSE_CATEGORY_KEY);
            }

            ExpenseCategoryResponse expenseCategory = expenseCategoryService.getExpenseCategoryByKey(key);

            response.setCode(HttpStatus.OK.value());
            response.setMessage(Constants.SUCCESS);
            response.setBody(expenseCategory);

        } catch (Exception e) {

            if (e instanceof ExpenseCategoryException) {

                switch (e.getMessage()) {

                    case ErrorMessageConstants.PERMISSION_DENIED -> response.setCode(HttpStatus.FORBIDDEN.value());
                    case ErrorMessageConstants.INVALID_EXPENSE_CATEGORY_KEY ->
                            response.setCode(HttpStatus.BAD_REQUEST.value());
                    case ErrorMessageConstants.NO_CATEGORY_FOUND_ERROR ->
                            response.setCode(HttpStatus.NO_CONTENT.value());
                }
            } else {
                response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }

            response.setMessage(e.getMessage());

            log.info(String.format(Constants.LOG_MESSAGE_STRUCTURE, correlationId, e.getMessage()));
        }

        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @GetMapping
    public ResponseEntity<CustomResponse<Page<ExpenseCategory>>> getAllExpenseCategory(
            @RequestHeader(Headers.CORRELATION_ID) String correlationId,
            Pageable pageable
    ) {

        if (defaultPageSize == 0) defaultPageSize = 50;
        if (maxPageSizeAllowed == 0) maxPageSizeAllowed = 200;

        Pageable thePageable = pageable;

        if (pageable == null) {
            thePageable = Pageable.ofSize(defaultPageSize);
        }

        CustomResponse<Page<ExpenseCategory>> response = new CustomResponse<>();

        try {
            if (thePageable.getPageSize() > maxPageSizeAllowed)
                throw new ExpenseCategoryException(String.format(ErrorMessageConstants.MAX_PAGE_SIZE_ERROR, maxPageSizeAllowed));


            Page<ExpenseCategory> expenseCategories = expenseCategoryService
                    .getExpenseCategoriesByUid(thePageable);

            response.setCode(HttpStatus.OK.value());
            response.setMessage("Success");
            response.setBody(expenseCategories);

        } catch (Exception e) {

            if (e instanceof ExpenseCategoryException)
                response.setCode(HttpStatus.BAD_REQUEST.value());
            else
                response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());

            response.setMessage(String.format(Constants.FAILED_, e.getMessage()));
            log.info(String.format(Constants.LOG_MESSAGE_STRUCTURE, correlationId, e.getMessage()));
        }

        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @PostMapping("/new/create")
    public ResponseEntity<CustomResponse<ExpenseCategoryResponse>> createNewExpenseCategory(
            @RequestHeader(Headers.CORRELATION_ID) String correlationId,
            @RequestBody ExpenseCategoryRequest expenseCategoryRequest
    ) {

        CustomResponse<ExpenseCategoryResponse> response = new CustomResponse<>();

        if (expenseCategoryRequest.isValid(RequestAction.ADD)) {

            try {

                ExpenseCategoryResponse expenseCategory = expenseCategoryService.createExpenseCategory(expenseCategoryRequest);

                response.setCode(HttpStatus.CREATED.value());
                response.setMessage(Constants.SUCCESS);
                response.setBody(expenseCategory);

                log.info(String.format(Constants.LOG_MESSAGE_STRUCTURE, correlationId, "Category created successfully with key : " + expenseCategory.getKey()));

            } catch (Exception e) {

                if (e instanceof ExpenseCategoryException) {

                    if (e.getMessage().contains(ErrorMessageConstants.CATEGORY_NAME_ALREADY_PRESENT_ERROR))
                        response.setCode(HttpStatus.CONFLICT.value());
                    else
                        response.setCode(HttpStatus.BAD_REQUEST.value());
                } else {

                    response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                }

                response.setMessage(String.format(Constants.FAILED_, e.getMessage()));
                log.info(String.format(Constants.LOG_MESSAGE_STRUCTURE, correlationId, e.getMessage()));
            }
        } else {

            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(String.format(Constants.FAILED_, String.format(ErrorMessageConstants.INVALID_REQUEST_BODY, "creating")));
        }

        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @PutMapping("/update")
    public ResponseEntity<CustomResponse<ExpenseCategoryResponse>> updateExpenseCategory(
            @RequestHeader(Headers.CORRELATION_ID) String correlationId,
            @RequestBody ExpenseCategoryRequest expenseCategoryRequest
    ) {

        CustomResponse<ExpenseCategoryResponse> response = new CustomResponse<>();

        if (expenseCategoryRequest.isValid(RequestAction.UPDATE)) {

            try {

                ExpenseCategoryResponse expenseCategory = expenseCategoryService.updateExpenseCategory(expenseCategoryRequest);

                response.setCode(HttpStatus.OK.value());
                response.setMessage(Constants.SUCCESS);
                response.setBody(expenseCategory);

                log.info(String.format(Constants.LOG_MESSAGE_STRUCTURE, correlationId, "Category with key : " + expenseCategory.getKey() + " updated successfully"));

            } catch (Exception e) {

                if (e instanceof ExpenseCategoryException) {

                    if (e.getMessage().contains(ErrorMessageConstants.CATEGORY_NAME_ALREADY_PRESENT_ERROR))
                        response.setCode(HttpStatus.CONFLICT.value());
                    else
                        response.setCode(HttpStatus.BAD_REQUEST.value());

                    log.info(String.format(Constants.LOG_MESSAGE_STRUCTURE, correlationId, e.getMessage()));
                } else {

                    response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
                }

                response.setMessage(String.format(Constants.FAILED_, e.getMessage()));
            }
        } else {

            response.setCode(HttpStatus.BAD_REQUEST.value());
            response.setMessage(String.format(Constants.FAILED_, String.format(ErrorMessageConstants.INVALID_REQUEST_BODY, "updating")));
        }

        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

    @DeleteMapping("/key")
    public ResponseEntity<CustomResponse<String>> deleteExpenseCategory(
            @RequestHeader(Headers.CORRELATION_ID) String correlationId,
            @RequestParam("key") String key
    ) {

        CustomResponse<String> response = new CustomResponse<>();

        try {
            if (!MymStringUtil.isValid(key)) {
                throw new ExpenseCategoryException(ErrorMessageConstants.INVALID_EXPENSE_CATEGORY_KEY);
            }

            expenseCategoryService.deleteExpenseCategoryByKey(key);

            response.setCode(HttpStatus.NO_CONTENT.value());
            log.info(String.format(Constants.LOG_MESSAGE_STRUCTURE, correlationId, "Category with key " + key + " deleted successfully"));
        } catch (Exception e) {

            if (e instanceof ExpenseCategoryException) {

                switch (e.getMessage()) {

                    case ErrorMessageConstants.PERMISSION_DENIED -> response.setCode(HttpStatus.FORBIDDEN.value());
                    case ErrorMessageConstants.INVALID_EXPENSE_CATEGORY_KEY ->
                            response.setCode(HttpStatus.BAD_REQUEST.value());
                    case ErrorMessageConstants.NO_CATEGORY_FOUND_ERROR ->
                            response.setCode(HttpStatus.NO_CONTENT.value());
                }
            } else {
                response.setCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            }

            response.setMessage(e.getMessage());
            log.info(String.format(Constants.LOG_MESSAGE_STRUCTURE, correlationId, e.getMessage()));
        }

        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getCode()));
    }

}
