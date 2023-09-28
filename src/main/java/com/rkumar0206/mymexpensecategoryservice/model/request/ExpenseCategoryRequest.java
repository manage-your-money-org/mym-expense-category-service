package com.rkumar0206.mymexpensecategoryservice.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rkumar0206.mymexpensecategoryservice.contanstsAndEnums.RequestAction;
import com.rkumar0206.mymexpensecategoryservice.utility.MymUtil;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ExpenseCategoryRequest {

    private String categoryName;
    private String categoryDescription;
    private String imageUrl;
    private String key;

    @JsonIgnore
    public boolean isValid(RequestAction action) {

        boolean isValid = MymUtil.isValid(categoryName)
                && (action == RequestAction.ADD || action == RequestAction.UPDATE);

        if (isValid && action == RequestAction.UPDATE) {

            // key is mandatory for updating any category
            isValid = MymUtil.isValid(key);
        }

        return isValid;
    }
}
