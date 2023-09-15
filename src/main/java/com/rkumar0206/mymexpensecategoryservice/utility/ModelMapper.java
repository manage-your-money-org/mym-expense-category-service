package com.rkumar0206.mymexpensecategoryservice.utility;

import com.rkumar0206.mymexpensecategoryservice.domain.ExpenseCategory;
import com.rkumar0206.mymexpensecategoryservice.model.response.ExpenseCategoryResponse;

public class ModelMapper {

    public static ExpenseCategoryResponse buildExpenseCategoryResponse(ExpenseCategory expenseCategory) {

        return ExpenseCategoryResponse.builder()
                .categoryName(expenseCategory.getCategoryName())
                .categoryDescription(expenseCategory.getCategoryDescription())
                .imageUrl(expenseCategory.getImageUrl())
                .uid(expenseCategory.getUid())
                .key(expenseCategory.getKey())
                .created(expenseCategory.getCreated())
                .modified(expenseCategory.getModified())
                .build();
    }
}
