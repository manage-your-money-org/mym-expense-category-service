package com.rkumar0206.mymexpensecategoryservice.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rkumar0206.mymexpensecategoryservice.model.request.ExpenseCategoryRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "ExpenseCategory")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ExpenseCategory {

    @Id
    @JsonIgnore
    private String id;

    private String categoryName;
    private String categoryDescription;
    private String imageUrl;
    private Date created;
    private Date modified;
    private String uid;
    private String key;

    public void updateExpenseCategoryFields(ExpenseCategoryRequest expenseCategoryRequest) {

        this.setCategoryName(expenseCategoryRequest.getCategoryName());
        this.setCategoryDescription(expenseCategoryRequest.getCategoryDescription());
        this.setImageUrl(expenseCategoryRequest.getImageUrl());
        this.setModified(new Date(System.currentTimeMillis()));
    }
}
