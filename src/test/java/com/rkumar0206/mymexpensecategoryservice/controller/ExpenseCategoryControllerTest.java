package com.rkumar0206.mymexpensecategoryservice.controller;

import com.rkumar0206.mymexpensecategoryservice.contanstsAndEnums.ErrorMessageConstants;
import com.rkumar0206.mymexpensecategoryservice.domain.ExpenseCategory;
import com.rkumar0206.mymexpensecategoryservice.exceptions.ExpenseCategoryException;
import com.rkumar0206.mymexpensecategoryservice.model.request.ExpenseCategoryRequest;
import com.rkumar0206.mymexpensecategoryservice.model.response.CustomResponse;
import com.rkumar0206.mymexpensecategoryservice.model.response.ExpenseCategoryResponse;
import com.rkumar0206.mymexpensecategoryservice.service.ExpenseCategoryService;
import com.rkumar0206.mymexpensecategoryservice.utility.ModelMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.Date;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseCategoryControllerTest {

    @Mock
    private ExpenseCategoryService expenseCategoryService;

    @InjectMocks
    private ExpenseCategoryController expenseCategoryController;

    private ExpenseCategory tempExpenseCategory;

    private Pageable pageable;

    @BeforeEach
    void setUp() {

        tempExpenseCategory = new ExpenseCategory(
                "sjnbkjskbsk",
                "Temp Expense category",
                "temp expense category description",
                "",
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis()),
                UUID.randomUUID().toString(),
                "rrrrr_jkfdbvjhsbjhsbjhsbjhb"

        );

        pageable = PageRequest.of(0, 50);
    }

    @Test
    void getExpenseCategoryByKey_Success() {

        when(expenseCategoryService.getExpenseCategoryByKey(anyString()))
                .thenReturn(ModelMapper.buildExpenseCategoryResponse(tempExpenseCategory));

        ResponseEntity<CustomResponse<ExpenseCategoryResponse>> actual = expenseCategoryController.getExpenseCategoryByKey(UUID.randomUUID().toString(), tempExpenseCategory.getKey());

        assertEquals(HttpStatusCode.valueOf(HttpStatus.OK.value()), actual.getStatusCode());
        assertEquals(tempExpenseCategory.getCategoryName(), actual.getBody().getBody().getCategoryName());
    }

    @Test
    void getExpenseCategoryByKey_KeyIsNull_BAD_REQUEST_RESPONSE() {


        ResponseEntity<CustomResponse<ExpenseCategoryResponse>> actual =
                expenseCategoryController.getExpenseCategoryByKey(UUID.randomUUID().toString(), null);

        assertEquals(HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), actual.getStatusCode());
        assertEquals(ErrorMessageConstants.INVALID_EXPENSE_CATEGORY_KEY, actual.getBody().getMessage());

    }

    @Test
    void getExpenseCategoryByKey_PermissionDenied_FORBIDEEN_RESPONSE() {

        when(expenseCategoryService.getExpenseCategoryByKey(anyString()))
                .thenThrow(new ExpenseCategoryException(ErrorMessageConstants.PERMISSION_DENIED));

        ResponseEntity<CustomResponse<ExpenseCategoryResponse>> actual =
                expenseCategoryController.getExpenseCategoryByKey(UUID.randomUUID().toString(), tempExpenseCategory.getKey());

        assertEquals(HttpStatusCode.valueOf(HttpStatus.FORBIDDEN.value()), actual.getStatusCode());
        assertEquals(ErrorMessageConstants.PERMISSION_DENIED, actual.getBody().getMessage());

    }

    @Test
    void getExpenseCategoryByKey_NoCategoryFound_NO_CONTENT_RESPONSE() {

        when(expenseCategoryService.getExpenseCategoryByKey(anyString()))
                .thenThrow(new ExpenseCategoryException(ErrorMessageConstants.NO_CATEGORY_FOUND_ERROR));

        ResponseEntity<CustomResponse<ExpenseCategoryResponse>> actual =
                expenseCategoryController.getExpenseCategoryByKey(UUID.randomUUID().toString(), tempExpenseCategory.getKey());

        assertEquals(HttpStatusCode.valueOf(HttpStatus.NO_CONTENT.value()), actual.getStatusCode());
        assertEquals(ErrorMessageConstants.NO_CATEGORY_FOUND_ERROR, actual.getBody().getMessage());

    }

    @Test
    void getExpenseCategoryByKey_AnyOtherException_INTERNAL_SERVER_ERROR_RESPONSE() {

        when(expenseCategoryService.getExpenseCategoryByKey(anyString()))
                .thenThrow(new RuntimeException("Some error"));

        ResponseEntity<CustomResponse<ExpenseCategoryResponse>> actual =
                expenseCategoryController.getExpenseCategoryByKey(UUID.randomUUID().toString(), tempExpenseCategory.getKey());

        assertEquals(HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), actual.getStatusCode());
        assertEquals("Some error", actual.getBody().getMessage());

    }


    @Test
    void getAllExpenseCategory_Success() {

        Page<ExpenseCategory> expenseCategoriesPage = new PageImpl<>(
                Collections.singletonList(tempExpenseCategory)
        );

        when(expenseCategoryService.getExpenseCategoriesByUid(pageable))
                .thenReturn(expenseCategoriesPage);

        ResponseEntity<CustomResponse<Page<ExpenseCategory>>> actual =
                expenseCategoryController.getAllExpenseCategory(UUID.randomUUID().toString(), pageable);

        assertEquals(HttpStatusCode.valueOf(HttpStatus.OK.value()), actual.getStatusCode());
        assertEquals(tempExpenseCategory.getCategoryName(), actual.getBody().getBody().getContent().get(0).getCategoryName());

    }

    @Test
    void getAllExpenseCategory_MAX_PAGE_SIZE_ERROR_BAD_REQUEST_RESPONSE() {

        pageable = PageRequest.of(0, 500);

        ResponseEntity<CustomResponse<Page<ExpenseCategory>>> actual =
                expenseCategoryController.getAllExpenseCategory(UUID.randomUUID().toString(), pageable);

        assertEquals(HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), actual.getStatusCode());
        assertThat(actual.getBody().getMessage(), containsString(ErrorMessageConstants.MAX_PAGE_SIZE_ERROR.substring(0, 8)));

    }

    @Test
    void getAllExpenseCategory_AnyOtherError_INTERNAL_SERVER_EERROR_RESPONSE() {

        when(expenseCategoryService.getExpenseCategoriesByUid(pageable))
                .thenThrow(new RuntimeException("Some Error"));

        ResponseEntity<CustomResponse<Page<ExpenseCategory>>> actual =
                expenseCategoryController.getAllExpenseCategory(UUID.randomUUID().toString(), pageable);

        assertEquals(HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), actual.getStatusCode());
        assertEquals("Failed: Some Error", actual.getBody().getMessage());

    }


    @Test
    void createNewExpenseCategory_Success() {

        ExpenseCategoryRequest request = new ExpenseCategoryRequest(
                "MyCategory",
                "CategoryDesc",
                "",
                null
        );

        ExpenseCategoryResponse expenseCategoryResponse = new ExpenseCategoryResponse(
                request.getCategoryName(),
                request.getCategoryDescription(),
                request.getImageUrl(),
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis()),
                "rrrrrr_askncbkjsbkabk",
                UUID.randomUUID().toString()
        );

        when(expenseCategoryService.createExpenseCategory(request))
                .thenReturn(expenseCategoryResponse);

        ResponseEntity<CustomResponse<ExpenseCategoryResponse>> actual =
                expenseCategoryController.createNewExpenseCategory(UUID.randomUUID().toString(), request);

        assertEquals(HttpStatusCode.valueOf(HttpStatus.CREATED.value()), actual.getStatusCode());
        assertNotNull(actual.getBody().getBody().getKey());
        assertEquals(expenseCategoryResponse.getKey(), actual.getBody().getBody().getKey());
        assertEquals(request.getCategoryName(), actual.getBody().getBody().getCategoryName());

    }

    @Test
    void createNewExpenseCategory_CategoryNameAlreadyPresent_CONFLICT_RESPONSE() {

        ExpenseCategoryRequest request = new ExpenseCategoryRequest(
                "MyCategory",
                "CategoryDesc",
                "",
                null
        );

        when(expenseCategoryService.createExpenseCategory(request))
                .thenThrow(new ExpenseCategoryException(ErrorMessageConstants.CATEGORY_NAME_ALREADY_PRESENT_ERROR));

        ResponseEntity<CustomResponse<ExpenseCategoryResponse>> actual =
                expenseCategoryController.createNewExpenseCategory(UUID.randomUUID().toString(), request);

        assertEquals(HttpStatusCode.valueOf(HttpStatus.CONFLICT.value()), actual.getStatusCode());
        assertThat(actual.getBody().getMessage(), containsString(ErrorMessageConstants.CATEGORY_NAME_ALREADY_PRESENT_ERROR));

    }

    @Test
    void createNewExpenseCategory_AnyOtherException_INTERNAL_SERVER_ERROR_RESPONSE() {

        ExpenseCategoryRequest request = new ExpenseCategoryRequest(
                "MyCategory",
                "CategoryDesc",
                "",
                null
        );

        when(expenseCategoryService.createExpenseCategory(request))
                .thenThrow(new RuntimeException(ErrorMessageConstants.USER_NOT_AUTHORIZED_ERROR));

        ResponseEntity<CustomResponse<ExpenseCategoryResponse>> actual =
                expenseCategoryController.createNewExpenseCategory(UUID.randomUUID().toString(), request);

        assertEquals(HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), actual.getStatusCode());
        assertThat(actual.getBody().getMessage(), containsString(ErrorMessageConstants.USER_NOT_AUTHORIZED_ERROR));

    }

    @Test
    void createNewExpenseCategory_AnyOtherCategoryException_BAD_REQUEST_RESPONSE() {

        ExpenseCategoryRequest request = new ExpenseCategoryRequest(
                "MyCategory",
                "CategoryDesc",
                "",
                null
        );

        when(expenseCategoryService.createExpenseCategory(request))
                .thenThrow(new ExpenseCategoryException(ErrorMessageConstants.INVALID_REQUEST_BODY));

        ResponseEntity<CustomResponse<ExpenseCategoryResponse>> actual =
                expenseCategoryController.createNewExpenseCategory(UUID.randomUUID().toString(), request);

        assertEquals(HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), actual.getStatusCode());
        assertThat(actual.getBody().getMessage(), containsString(ErrorMessageConstants.INVALID_REQUEST_BODY));
    }

    @Test
    void createNewExpenseCategory_RequestNotValid_BAD_REQUEST_RESPONSE() {

        ExpenseCategoryRequest request = new ExpenseCategoryRequest(
                null,
                "CategoryDesc",
                "",
                null
        );

        ResponseEntity<CustomResponse<ExpenseCategoryResponse>> actual =
                expenseCategoryController.createNewExpenseCategory(UUID.randomUUID().toString(), request);

        assertEquals(HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), actual.getStatusCode());
        assertThat(actual.getBody().getMessage(), containsString(String.format(ErrorMessageConstants.INVALID_REQUEST_BODY, "creating")));
    }


    @Test
    void updateExpenseCategory_Success() {

        String key = UUID.randomUUID().toString();

        ExpenseCategoryRequest request = new ExpenseCategoryRequest(
                "MyCategory",
                "CategoryDesc",
                "",
                key
        );

        ExpenseCategoryResponse expenseCategoryResponse = new ExpenseCategoryResponse(
                request.getCategoryName(),
                request.getCategoryDescription(),
                request.getImageUrl(),
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis()),
                "rrrrrr_askncbkjsbkabk",
                key
        );

        when(expenseCategoryService.updateExpenseCategory(request))
                .thenReturn(expenseCategoryResponse);

        ResponseEntity<CustomResponse<ExpenseCategoryResponse>> actual =
                expenseCategoryController.updateExpenseCategory(UUID.randomUUID().toString(), request);

        assertEquals(HttpStatusCode.valueOf(HttpStatus.OK.value()), actual.getStatusCode());
        assertNotNull(actual.getBody().getBody().getKey());
        assertEquals(expenseCategoryResponse.getKey(), actual.getBody().getBody().getKey());
        assertEquals(request.getCategoryName(), actual.getBody().getBody().getCategoryName());

    }

    @Test
    void updateExpenseCategory_RequestBodyNotValid_BAD_REQUEST_RESPONSE() {


        ExpenseCategoryRequest request = new ExpenseCategoryRequest(
                "MyCategory",
                "CategoryDesc",
                "",
                null
        );


        ResponseEntity<CustomResponse<ExpenseCategoryResponse>> actual =
                expenseCategoryController.updateExpenseCategory(UUID.randomUUID().toString(), request);

        assertEquals(HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), actual.getStatusCode());
        assertThat(actual.getBody().getMessage(), containsString(String.format(ErrorMessageConstants.INVALID_REQUEST_BODY, "updating")));
    }

    @Test
    void updateExpenseCategory_CategoryAlreadyPresent_CONFLICT_RESPONSE() {

        ExpenseCategoryRequest request = new ExpenseCategoryRequest(
                "MyCategory",
                "CategoryDesc",
                "",
                UUID.randomUUID().toString()
        );

        when(expenseCategoryService.updateExpenseCategory(request))
                .thenThrow(new ExpenseCategoryException(ErrorMessageConstants.CATEGORY_NAME_ALREADY_PRESENT_ERROR));

        ResponseEntity<CustomResponse<ExpenseCategoryResponse>> actual =
                expenseCategoryController.updateExpenseCategory(UUID.randomUUID().toString(), request);

        assertEquals(HttpStatusCode.valueOf(HttpStatus.CONFLICT.value()), actual.getStatusCode());
        assertThat(actual.getBody().getMessage(), containsString(ErrorMessageConstants.CATEGORY_NAME_ALREADY_PRESENT_ERROR));

    }

    @Test
    void updateExpenseCategory_AnyOtherException_INTERNAL_SERVER_ERROR_RESPONSE() {

        ExpenseCategoryRequest request = new ExpenseCategoryRequest(
                "MyCategory",
                "CategoryDesc",
                "",
                UUID.randomUUID().toString()
        );

        when(expenseCategoryService.updateExpenseCategory(request))
                .thenThrow(new RuntimeException(ErrorMessageConstants.USER_NOT_AUTHORIZED_ERROR));

        ResponseEntity<CustomResponse<ExpenseCategoryResponse>> actual =
                expenseCategoryController.updateExpenseCategory(UUID.randomUUID().toString(), request);

        assertEquals(HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), actual.getStatusCode());
        assertThat(actual.getBody().getMessage(), containsString(ErrorMessageConstants.USER_NOT_AUTHORIZED_ERROR));

    }

    @Test
    void updateExpenseCategory_AnyOtherCategoryException_BAD_REQUEST_RESPONSE() {

        ExpenseCategoryRequest request = new ExpenseCategoryRequest(
                "MyCategory",
                "CategoryDesc",
                "",
                UUID.randomUUID().toString()
        );

        when(expenseCategoryService.updateExpenseCategory(request))
                .thenThrow(new ExpenseCategoryException(ErrorMessageConstants.INVALID_REQUEST_BODY));

        ResponseEntity<CustomResponse<ExpenseCategoryResponse>> actual =
                expenseCategoryController.updateExpenseCategory(UUID.randomUUID().toString(), request);

        assertEquals(HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), actual.getStatusCode());
        assertThat(actual.getBody().getMessage(), containsString(ErrorMessageConstants.INVALID_REQUEST_BODY));
    }

    @Test
    void deleteExpenseCategory_Success() {

        doNothing().when(expenseCategoryService).deleteExpenseCategoryByKey(anyString());

        ResponseEntity<CustomResponse<String>> actual =
                expenseCategoryController.deleteExpenseCategory(UUID.randomUUID().toString(), tempExpenseCategory.getKey());

        assertEquals(HttpStatusCode.valueOf(HttpStatus.NO_CONTENT.value()), actual.getStatusCode());

    }

    @Test
    void deleteExpenseCategory_KeyNotValid_BAD_REQUEST_RESPONSE() {

        ResponseEntity<CustomResponse<String>> actual =
                expenseCategoryController.deleteExpenseCategory(UUID.randomUUID().toString(), null);

        assertEquals(HttpStatusCode.valueOf(HttpStatus.BAD_REQUEST.value()), actual.getStatusCode());
        assertThat(actual.getBody().getMessage(), containsString(ErrorMessageConstants.INVALID_EXPENSE_CATEGORY_KEY));
    }

    @Test
    void deleteExpenseCategory_PermissionDenied_FORBIDDEN_RESPONSE() {

        doThrow(new ExpenseCategoryException(ErrorMessageConstants.PERMISSION_DENIED))
                .when(expenseCategoryService).deleteExpenseCategoryByKey(anyString());

        ResponseEntity<CustomResponse<String>> actual =
                expenseCategoryController.deleteExpenseCategory(UUID.randomUUID().toString(), tempExpenseCategory.getKey());

        assertEquals(HttpStatusCode.valueOf(HttpStatus.FORBIDDEN.value()), actual.getStatusCode());
        assertThat(actual.getBody().getMessage(), containsString(ErrorMessageConstants.PERMISSION_DENIED));
    }

    @Test
    void deleteExpenseCategory_NoCategoryFound_NO_COTENT_RESPONSE() {

        doThrow(new ExpenseCategoryException(ErrorMessageConstants.NO_CATEGORY_FOUND_ERROR))
                .when(expenseCategoryService).deleteExpenseCategoryByKey(anyString());

        ResponseEntity<CustomResponse<String>> actual =
                expenseCategoryController.deleteExpenseCategory(UUID.randomUUID().toString(), tempExpenseCategory.getKey());

        assertEquals(HttpStatusCode.valueOf(HttpStatus.NO_CONTENT.value()), actual.getStatusCode());
    }

    @Test
    void deleteExpenseCategory_AnyOtherException_INTERNAL_SERVER_ERROR_RESPONSE() {

        doThrow(new RuntimeException(ErrorMessageConstants.USER_INFO_NOT_PROVIDED_ERROR))
                .when(expenseCategoryService).deleteExpenseCategoryByKey(anyString());

        ResponseEntity<CustomResponse<String>> actual =
                expenseCategoryController.deleteExpenseCategory(UUID.randomUUID().toString(), tempExpenseCategory.getKey());

        assertEquals(HttpStatusCode.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()), actual.getStatusCode());
        assertThat(actual.getBody().getMessage(), containsString(ErrorMessageConstants.USER_INFO_NOT_PROVIDED_ERROR));
    }


}