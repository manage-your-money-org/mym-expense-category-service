package com.rkumar0206.mymexpensecategoryservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rkumar0206.mymexpensecategoryservice.contanstsAndEnums.Constants;
import com.rkumar0206.mymexpensecategoryservice.contanstsAndEnums.ErrorMessageConstants;
import com.rkumar0206.mymexpensecategoryservice.contanstsAndEnums.Headers;
import com.rkumar0206.mymexpensecategoryservice.model.UserInfo;
import com.rkumar0206.mymexpensecategoryservice.utility.MymUtil;
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

        String tempUserInfo = request.getHeader(Headers.USER_INFO_HEADER_NAME);

        if (tempUserInfo == null || tempUserInfo.trim().isEmpty()) {
            throw new RuntimeException(ErrorMessageConstants.USER_INFO_NOT_PROVIDED_ERROR);
        }

        try {
            UserInfo userInfo = new ObjectMapper().readValue(tempUserInfo, UserInfo.class);

            if (MymUtil.isNotValid(userInfo.getUid())
                    || MymUtil.isNotValid(userInfo.getName())
                    || MymUtil.isNotValid(userInfo.getEmailId())
                    || !userInfo.isAccountVerified()) {

                throw new RuntimeException(ErrorMessageConstants.USER_INFO_NOT_PROPER);
            }

            return userInfo;

        } catch (JsonProcessingException e) {
            throw new RuntimeException(ErrorMessageConstants.USER_INFO_NOT_PROPER);
        }
    }

    public String getAuthorizationToken() {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(Constants.BEARER)) {
            throw new RuntimeException(ErrorMessageConstants.USER_NOT_AUTHORIZED_ERROR);
        }

        return authHeader;
    }

    public String getCorrelationId() {

        String correlationId = request.getHeader(Headers.CORRELATION_ID);

        if (correlationId == null) {
            throw new RuntimeException(ErrorMessageConstants.NO_CORRELATION_ID_PASSED);
        }

        return correlationId;
    }

    public String getUserInfoHeaderValue() {

        String userInfo = request.getHeader(Headers.USER_INFO_HEADER_NAME);

        if (userInfo == null) {
            throw new RuntimeException(ErrorMessageConstants.USER_INFO_NOT_PROVIDED_ERROR);
        }

        return userInfo;
    }
}
