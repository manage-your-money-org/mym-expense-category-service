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
import com.rkumar0206.mymexpensecategoryservice.utility.MymUtil;
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

            response.setStatus(HttpStatus.OK.value());
            response.setMessage(Constants.SUCCESS);
            response.setBody(expenseCategory);

        } catch (Exception ex) {

            MymUtil.setAppropriateResponseStatus(response, ex);
            log.info(String.format(Constants.LOG_MESSAGE_STRUCTURE, correlationId, ex.getMessage()));
        }

        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
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

            response.setStatus(HttpStatus.OK.value());
            response.setMessage(Constants.SUCCESS);
            response.setBody(expenseCategories);

        } catch (Exception e) {

            MymUtil.setAppropriateResponseStatus(response, e);
            log.info(String.format(Constants.LOG_MESSAGE_STRUCTURE, correlationId, e.getMessage()));
        }

        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }

    @PostMapping("/new/create")
    public ResponseEntity<CustomResponse<ExpenseCategoryResponse>> createNewExpenseCategory(
            @RequestHeader(Headers.CORRELATION_ID) String correlationId,
            @RequestBody ExpenseCategoryRequest expenseCategoryRequest
    ) {

        CustomResponse<ExpenseCategoryResponse> response = new CustomResponse<>();

        try {

            if (!expenseCategoryRequest.isValid(RequestAction.ADD)) {
                throw new ExpenseCategoryException(ErrorMessageConstants.INVALID_REQUEST_BODY_CREATE);
            }

            ExpenseCategoryResponse expenseCategory = expenseCategoryService.createExpenseCategory(expenseCategoryRequest);

            response.setStatus(HttpStatus.CREATED.value());
            response.setMessage(Constants.SUCCESS);
            response.setBody(expenseCategory);

            log.info(String.format(Constants.LOG_MESSAGE_STRUCTURE, correlationId, "Category created successfully with key : " + expenseCategory.getKey()));

        } catch (Exception e) {

            MymUtil.setAppropriateResponseStatus(response, e);
            log.info(String.format(Constants.LOG_MESSAGE_STRUCTURE, correlationId, e.getMessage()));
        }

        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }

    @PutMapping("/update")
    public ResponseEntity<CustomResponse<ExpenseCategoryResponse>> updateExpenseCategory(
            @RequestHeader(Headers.CORRELATION_ID) String correlationId,
            @RequestBody ExpenseCategoryRequest expenseCategoryRequest
    ) {

        CustomResponse<ExpenseCategoryResponse> response = new CustomResponse<>();

        try {

            if (!expenseCategoryRequest.isValid(RequestAction.UPDATE)) {
                throw new ExpenseCategoryException(ErrorMessageConstants.INVALID_REQUEST_BODY_UPDATE);
            }

            ExpenseCategoryResponse expenseCategory = expenseCategoryService.updateExpenseCategory(expenseCategoryRequest);

            response.setStatus(HttpStatus.OK.value());
            response.setMessage(Constants.SUCCESS);
            response.setBody(expenseCategory);

            log.info(String.format(Constants.LOG_MESSAGE_STRUCTURE, correlationId, "Category with key : " + expenseCategory.getKey() + " updated successfully"));

        } catch (Exception e) {

            MymUtil.setAppropriateResponseStatus(response, e);
            log.info(String.format(Constants.LOG_MESSAGE_STRUCTURE, correlationId, e.getMessage()));

        }

        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }

    @DeleteMapping("/key")
    public ResponseEntity<CustomResponse<String>> deleteExpenseCategory(
            @RequestHeader(Headers.CORRELATION_ID) String correlationId,
            @RequestParam("key") String key
    ) {

        CustomResponse<String> response = new CustomResponse<>();

        try {
            if (!MymUtil.isValid(key)) {
                throw new ExpenseCategoryException(ErrorMessageConstants.INVALID_EXPENSE_CATEGORY_KEY);
            }

            expenseCategoryService.deleteExpenseCategoryByKey(key);

            response.setStatus(HttpStatus.NO_CONTENT.value());
            log.info(String.format(Constants.LOG_MESSAGE_STRUCTURE, correlationId, "Category with key " + key + " deleted successfully"));
        } catch (Exception e) {

            MymUtil.setAppropriateResponseStatus(response, e);
            log.info(String.format(Constants.LOG_MESSAGE_STRUCTURE, correlationId, e.getMessage()));
        }

        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }

}
