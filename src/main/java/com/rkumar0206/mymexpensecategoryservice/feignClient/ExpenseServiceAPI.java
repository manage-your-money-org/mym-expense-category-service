package com.rkumar0206.mymexpensecategoryservice.feignClient;

import com.rkumar0206.mymexpensecategoryservice.contanstsAndEnums.Headers;
import com.rkumar0206.mymexpensecategoryservice.model.response.CustomResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "mym-expense-service"
)
public interface ExpenseServiceAPI {

    @DeleteMapping("/mym/api/expenses/categoryKey")
    public ResponseEntity<CustomResponse<String>> deleteExpenseByCategoryKey(
            @RequestHeader(Headers.CORRELATION_ID) String correlationId,
            @RequestHeader(Headers.USER_INFO_HEADER_NAME) String userInfo,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorization,
            @RequestParam("categoryKey") String categoryKey
    );
}
