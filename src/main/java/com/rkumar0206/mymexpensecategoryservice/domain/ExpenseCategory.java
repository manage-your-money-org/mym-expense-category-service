package com.rkumar0206.mymexpensecategoryservice.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ExpenseCategory")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ExpenseCategory {

    @Id
    private String id;

    private String categoryName;
    private String categoryDescription;
    private String imageUrl;
    private Long created;
    private Long modified;
    private String uid;
    private String key;
}
