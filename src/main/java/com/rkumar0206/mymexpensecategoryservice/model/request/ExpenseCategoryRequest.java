package com.rkumar0206.mymexpensecategoryservice.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rkumar0206.mymexpensecategoryservice.contanstsAndEnums.RequestAction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.util.StringUtils;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ExpenseCategoryRequest {

    private String categoryName;
    private String categoryDescription;
    private String imageUrl;
    private String key;

    @JsonIgnore
    public boolean isValid(RequestAction action) {

        boolean isValid = StringUtils.hasLength(categoryName.trim())
                && (action == RequestAction.ADD || action == RequestAction.UPDATE);

        if (isValid && action == RequestAction.UPDATE) {

            // key is mandatory for updating any category
            isValid = key != null && StringUtils.hasLength(key.trim());
        }

        return isValid;
    }
}
