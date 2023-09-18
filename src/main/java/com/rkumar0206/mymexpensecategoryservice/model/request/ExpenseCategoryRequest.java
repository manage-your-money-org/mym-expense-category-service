package com.rkumar0206.mymexpensecategoryservice.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rkumar0206.mymexpensecategoryservice.contanstsAndEnums.RequestAction;
import com.rkumar0206.mymexpensecategoryservice.utility.MymStringUtil;
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

        boolean isValid = MymStringUtil.isValid(categoryName)
                && (action == RequestAction.ADD || action == RequestAction.UPDATE);

        if (isValid && action == RequestAction.UPDATE) {

            // key is mandatory for updating any category
            isValid = MymStringUtil.isValid(key);
        }

        return isValid;
    }
}
