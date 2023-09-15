package com.rkumar0206.mymexpensecategoryservice.model.response;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ExpenseCategoryResponse {

    private String categoryName;
    private String categoryDescription;
    private String imageUrl;
    private Long created;
    private Long modified;
    private String uid;
    private String key;
}
