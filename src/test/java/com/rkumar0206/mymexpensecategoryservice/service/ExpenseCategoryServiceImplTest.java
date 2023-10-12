package com.rkumar0206.mymexpensecategoryservice.service;

import com.rkumar0206.mymexpensecategoryservice.contanstsAndEnums.ErrorMessageConstants;
import com.rkumar0206.mymexpensecategoryservice.domain.ExpenseCategory;
import com.rkumar0206.mymexpensecategoryservice.exceptions.ExpenseCategoryException;
import com.rkumar0206.mymexpensecategoryservice.feignClient.ExpenseServiceAPI;
import com.rkumar0206.mymexpensecategoryservice.model.UserInfo;
import com.rkumar0206.mymexpensecategoryservice.model.request.ExpenseCategoryRequest;
import com.rkumar0206.mymexpensecategoryservice.model.response.ExpenseCategoryResponse;
import com.rkumar0206.mymexpensecategoryservice.repositories.ExpenseCategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class ExpenseCategoryServiceImplTest {

    @Mock
    private ExpenseCategoryRepository expenseCategoryRepository;
    @Mock
    private UserContextService userContextService;
    @Mock
    private ExpenseServiceAPI expenseServiceAPI;

    private ExpenseCategoryServiceImpl expenseCategoryService;

    private UserInfo userInfo;
    private ExpenseCategory tempExpenseCategory;
    private Pageable pageable;

    @BeforeEach
    void setUp() {

        String uid = "rrrrr_" + UUID.randomUUID();

        tempExpenseCategory = new ExpenseCategory(
                "sjnbkjskbsk",
                "Temp Expense category",
                "temp expense category description",
                "",
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis()),
                uid,
                "rrrrr_jkfdbvjhsbjhsbjhsbjhb"

        );

        userInfo = new UserInfo(
                "Temp",
                "test@gmail.com",
                uid,
                true
        );

        pageable = PageRequest.of(0, 50);

        when(userContextService.getUserInfo()).thenReturn(userInfo);

        expenseCategoryService = new ExpenseCategoryServiceImpl(expenseCategoryRepository, userContextService, expenseServiceAPI);
    }

    @Test
    void getExpenseCategoriesByUid_Success() {

        Page<ExpenseCategory> expenseCategoriesPage = new PageImpl<>(
                Collections.singletonList(tempExpenseCategory)
        );

        when(expenseCategoryRepository.findByUid(userInfo.getUid(), pageable)).thenReturn(
                expenseCategoriesPage
        );

        Page<ExpenseCategory> actual = expenseCategoryService.getExpenseCategoriesByUid(pageable);

        assertEquals(expenseCategoriesPage.getTotalElements(), actual.getTotalElements());
        assertEquals(expenseCategoriesPage.getContent().get(0).getCategoryName(), actual.getContent().get(0).getCategoryName());
        assertEquals(expenseCategoriesPage.getContent().get(0).getKey(), actual.getContent().get(0).getKey());

    }

    @Test
    void getExpenseCategoryByKey_Success() {

        tempExpenseCategory.setUid(userInfo.getUid());

        when(expenseCategoryRepository.findByKey(anyString())).thenReturn(
                Optional.of(tempExpenseCategory)
        );

        ExpenseCategoryResponse actual = expenseCategoryService.getExpenseCategoryByKey(tempExpenseCategory.getKey());


        assertEquals(tempExpenseCategory.getKey(), actual.getKey());
        assertEquals(tempExpenseCategory.getCategoryName(), actual.getCategoryName());
    }

    @Test
    void getExpenseCategoryByKey_NotFound_ExceptionThrown() {

        when(expenseCategoryRepository.findByKey(anyString())).thenReturn(
                Optional.empty()
        );

        assertThatThrownBy(() -> expenseCategoryService.getExpenseCategoryByKey(tempExpenseCategory.getKey()))
                .hasMessage(ErrorMessageConstants.NO_CATEGORY_FOUND_ERROR)
                .isInstanceOf(ExpenseCategoryException.class);

    }

    @Test
    void getExpenseCategoryByKey_UID_Mismatch_ExceptionThrown() {

        tempExpenseCategory.setUid("sdjbjhsbshb");

        when(expenseCategoryRepository.findByKey(anyString())).thenReturn(
                Optional.of(tempExpenseCategory)
        );

        assertThatThrownBy(() -> expenseCategoryService.getExpenseCategoryByKey(tempExpenseCategory.getKey()))
                .hasMessage(ErrorMessageConstants.PERMISSION_DENIED)
                .isInstanceOf(ExpenseCategoryException.class);
    }


    @Test
    void createExpenseCategory_Success() {

        when(expenseCategoryRepository.findByCategoryNameAndUid(anyString(), anyString())).thenReturn(Optional.empty());

        ExpenseCategoryRequest expenseCategoryRequest = new ExpenseCategoryRequest(
                "My category",
                "my category description",
                "",
                null
        );

        //----------------
        // This is only for ModelMapper method does not fail
        when(expenseCategoryRepository.save(any())).thenReturn(tempExpenseCategory);
        //------------------

        expenseCategoryService.createExpenseCategory(expenseCategoryRequest);

        ArgumentCaptor<ExpenseCategory> argumentCaptor = ArgumentCaptor.forClass(ExpenseCategory.class);
        verify(expenseCategoryRepository).save(argumentCaptor.capture());
        ExpenseCategory actual = argumentCaptor.getValue();

        assertEquals(expenseCategoryRequest.getCategoryName(), actual.getCategoryName());
        assertEquals(expenseCategoryRequest.getCategoryDescription(), actual.getCategoryDescription());
        assertNotNull(actual.getKey());
        assertNotNull(actual.getCreated());
        assertNotNull(actual.getModified());
        assertEquals(userInfo.getUid(), actual.getUid());
    }

    @Test
    void createExpenseCategory_CategoryNameAlreadyPresent_ExceptionThrown() {

        when(expenseCategoryRepository.findByCategoryNameAndUid(anyString(), anyString()))
                .thenReturn(Optional.of(tempExpenseCategory));

        ExpenseCategoryRequest expenseCategoryRequest = new ExpenseCategoryRequest(
                tempExpenseCategory.getCategoryName(),
                "my category description",
                "",
                null
        );


        assertThatThrownBy(() -> expenseCategoryService.createExpenseCategory(expenseCategoryRequest))
                .hasMessage(ErrorMessageConstants.CATEGORY_NAME_ALREADY_PRESENT_ERROR)
                .isInstanceOf(ExpenseCategoryException.class);
    }


    @Test
    void updateExpenseCategory_Success() {

        when(expenseCategoryRepository
                .findByKey(tempExpenseCategory.getKey())).thenReturn(Optional.of(tempExpenseCategory));

        ExpenseCategoryRequest expenseCategoryRequest = new ExpenseCategoryRequest(
                "MyCategory",
                "my category description",
                "",
                tempExpenseCategory.getKey()
        );

        //----------------
        // This is only for ModelMapper method does not fail
        when(expenseCategoryRepository.save(any())).thenReturn(tempExpenseCategory);
        //------------------

        Date oldCreatedValue = tempExpenseCategory.getCreated();
        Date oldModifiedValue = tempExpenseCategory.getModified();
        String oldKeyValue = tempExpenseCategory.getKey();

        expenseCategoryService.updateExpenseCategory(expenseCategoryRequest);

        ArgumentCaptor<ExpenseCategory> argumentCaptor = ArgumentCaptor.forClass(ExpenseCategory.class);
        verify(expenseCategoryRepository).save(argumentCaptor.capture());
        ExpenseCategory actual = argumentCaptor.getValue();

        assertEquals(expenseCategoryRequest.getCategoryName(), actual.getCategoryName());
        assertEquals(expenseCategoryRequest.getCategoryDescription(), actual.getCategoryDescription());
        assertNotNull(actual.getKey());
        assertEquals(oldKeyValue, actual.getKey());
        assertEquals(oldCreatedValue, actual.getCreated());  // created value should not be changed
        assertNotEquals(oldModifiedValue, actual.getModified()); // modified value should be changed
        assertEquals(userInfo.getUid(), actual.getUid());
    }

    @Test
    void updateExpenseCategory_CategoryNotFound_ExceptionThrown() {

        when(expenseCategoryRepository
                .findByKey(tempExpenseCategory.getKey())).thenReturn(Optional.empty());

        ExpenseCategoryRequest expenseCategoryRequest = new ExpenseCategoryRequest(
                "MyCategory",
                "my category description",
                "",
                tempExpenseCategory.getKey()
        );

        assertThatThrownBy(() -> expenseCategoryService.updateExpenseCategory(expenseCategoryRequest))
                .isInstanceOf(ExpenseCategoryException.class)
                .hasMessage(ErrorMessageConstants.NO_CATEGORY_FOUND_ERROR);
    }

    @Test
    void updateExpenseCategory_UidNotMatched_ExceptionThrown() {

        tempExpenseCategory.setUid(UUID.randomUUID().toString());

        when(expenseCategoryRepository.findByKey(anyString()))
                .thenReturn(Optional.of(tempExpenseCategory));

        ExpenseCategoryRequest expenseCategoryRequest = new ExpenseCategoryRequest(
                "MyCategory",
                "my category description",
                "",
                tempExpenseCategory.getKey()
        );

        assertThatThrownBy(() -> expenseCategoryService.updateExpenseCategory(expenseCategoryRequest))
                .isInstanceOf(ExpenseCategoryException.class)
                .hasMessage(ErrorMessageConstants.PERMISSION_DENIED);
    }

    @Test
    void updateExpenseCategory_CategoryAlreadyPresent_ExceptionThrown() {

        when(expenseCategoryRepository.findByKey(anyString()))
                .thenReturn(Optional.of(tempExpenseCategory));


        ExpenseCategoryRequest expenseCategoryRequest = new ExpenseCategoryRequest(
                "MyCategory",
                "my category description",
                "",
                tempExpenseCategory.getKey()
        );

        ExpenseCategory expenseCategoryAlreadyPresent = new ExpenseCategory();
        expenseCategoryAlreadyPresent.setCategoryName(expenseCategoryRequest.getCategoryName());

        when(expenseCategoryRepository.findByCategoryNameAndUid(anyString(), anyString()))
                .thenReturn(Optional.of(expenseCategoryAlreadyPresent));


        assertThatThrownBy(() -> expenseCategoryService.updateExpenseCategory(expenseCategoryRequest))
                .isInstanceOf(ExpenseCategoryException.class)
                .hasMessage(ErrorMessageConstants.CATEGORY_NAME_ALREADY_PRESENT_ERROR);
    }


    @Test
    void deleteExpenseCategoryByKey_Success() {

        when(expenseCategoryRepository.findByKey(anyString())).thenReturn(Optional.of(tempExpenseCategory));

        expenseCategoryService.deleteExpenseCategoryByKey(tempExpenseCategory.getKey());

        verify(expenseCategoryRepository, times(1)).delete(tempExpenseCategory);
    }

    @Test
    void deleteExpenseCategoryByKey_CategoryNotFound_ExceptionThrown() {

        when(expenseCategoryRepository.findByKey(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> expenseCategoryService.deleteExpenseCategoryByKey(tempExpenseCategory.getKey()))
                .isInstanceOf(ExpenseCategoryException.class)
                .hasMessage(ErrorMessageConstants.NO_CATEGORY_FOUND_ERROR);

        verify(expenseCategoryRepository, times(0)).delete(tempExpenseCategory);
    }

    @Test
    void deleteExpenseCategoryByKey_UidNotMatched_ExceptionThrown() {

        tempExpenseCategory.setUid(UUID.randomUUID().toString());

        when(expenseCategoryRepository.findByKey(anyString())).thenReturn(Optional.of(tempExpenseCategory));

        assertThatThrownBy(() -> expenseCategoryService.deleteExpenseCategoryByKey(tempExpenseCategory.getKey()))
                .isInstanceOf(ExpenseCategoryException.class)
                .hasMessage(ErrorMessageConstants.PERMISSION_DENIED);

        verify(expenseCategoryRepository, times(0)).delete(tempExpenseCategory);
    }


//    @Test
//    void deleteAllExpenseCategory() {
//    }
}