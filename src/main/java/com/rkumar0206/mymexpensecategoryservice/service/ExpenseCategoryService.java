package com.rkumar0206.mymexpensecategoryservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rkumar0206.mymexpensecategoryservice.domain.ExpenseCategory;
import com.rkumar0206.mymexpensecategoryservice.model.request.ExpenseCategoryRequest;
import com.rkumar0206.mymexpensecategoryservice.model.response.ExpenseCategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ExpenseCategoryService {

    Page<ExpenseCategory> getExpenseCategoriesByUid(Pageable pageable, String uid);

    ExpenseCategory getExpenseCategoryByKey(String key);

    ExpenseCategoryResponse createExpenseCategory(ExpenseCategoryRequest expenseCategoryRequest) throws JsonProcessingException;

    ExpenseCategoryResponse updateExpenseCategory(ExpenseCategoryRequest expenseCategoryRequest);

    void deleteExpenseCategoryByKey(String key);

    void deleteAllExpenseCategory(String uid);
}
