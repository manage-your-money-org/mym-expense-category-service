package com.rkumar0206.mymexpensecategoryservice.model.response;

import lombok.*;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ExpenseCategoryResponse {

    private String categoryName;
    private String categoryDescription;
    private String imageUrl;
    private Date created;
    private Date modified;
    private String uid;
    private String key;
}
