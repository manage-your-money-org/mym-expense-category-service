package com.rkumar0206.mymexpensecategoryservice.model.request;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    public boolean isValid() {

        return StringUtils.hasLength(categoryName.trim());
    }
}
