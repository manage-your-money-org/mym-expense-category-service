package com.rkumar0206.mymexpensecategoryservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rkumar0206.mymexpensecategoryservice.contanstsAndEnums.Constants;
import com.rkumar0206.mymexpensecategoryservice.contanstsAndEnums.ErrorMessageConstants;
import com.rkumar0206.mymexpensecategoryservice.model.UserInfo;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserContextService {

    @Autowired
    private final HttpServletRequest request;

    public UserInfo getUserInfo() {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(Constants.BEARER)) {
            throw new RuntimeException(ErrorMessageConstants.USER_NOT_AUTHORIZED_ERROR);
        }

        String tempUserInfo = request.getHeader(Constants.USER_INFO_HEADER_NAME);

        if (tempUserInfo == null || tempUserInfo.trim().isEmpty()) {
            throw new RuntimeException(ErrorMessageConstants.USER_INFO_NOT_PROVIDED_ERROR);
        }

        try {
            return new ObjectMapper().readValue(tempUserInfo, UserInfo.class);
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}
