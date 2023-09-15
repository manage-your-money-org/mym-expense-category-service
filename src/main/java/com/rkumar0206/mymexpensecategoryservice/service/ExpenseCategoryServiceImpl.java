package com.rkumar0206.mymexpensecategoryservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.rkumar0206.mymexpensecategoryservice.contanstsAndEnums.ErrorMessageConstants;
import com.rkumar0206.mymexpensecategoryservice.domain.ExpenseCategory;
import com.rkumar0206.mymexpensecategoryservice.exceptions.ExpenseCategoryException;
import com.rkumar0206.mymexpensecategoryservice.model.UserInfo;
import com.rkumar0206.mymexpensecategoryservice.model.request.ExpenseCategoryRequest;
import com.rkumar0206.mymexpensecategoryservice.model.response.ExpenseCategoryResponse;
import com.rkumar0206.mymexpensecategoryservice.repositories.ExpenseCategoryRepository;
import com.rkumar0206.mymexpensecategoryservice.utility.ModelMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseCategoryServiceImpl implements ExpenseCategoryService {

    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final UserContextService userContextService;

    @Override
    public Page<ExpenseCategory> getExpenseCategoriesByUid(Pageable pageable, String uid) {



        return null;
    }

    @Override
    public ExpenseCategory getExpenseCategoryByKey(String key) {
        return null;
    }

    @Override
    public ExpenseCategoryResponse createExpenseCategory(ExpenseCategoryRequest expenseCategoryRequest) throws JsonProcessingException {

        UserInfo userInfo = getUserInfo();

        if (expenseCategoryRepository.findByCategoryNameAndUid(
                expenseCategoryRequest.getCategoryName(),
                userInfo.getUid()).isPresent()
        ) {

            log.error(ErrorMessageConstants.CATEGORY_NAME_ALREADY_PRESENT_ERROR);
            throw new ExpenseCategoryException(ErrorMessageConstants.CATEGORY_NAME_ALREADY_PRESENT_ERROR);
        }

        ExpenseCategory expenseCategory = new ExpenseCategory(
                null,
                expenseCategoryRequest.getCategoryName(),
                expenseCategoryRequest.getCategoryDescription(),
                expenseCategoryRequest.getImageUrl(),
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                userInfo.getUid(),
                getUserInfo().getUid().substring(0, 8) + "_" + UUID.randomUUID()
        );

        return ModelMapper.buildExpenseCategoryResponse(expenseCategoryRepository.save(expenseCategory));
    }

    @Override
    public ExpenseCategoryResponse updateExpenseCategory(ExpenseCategoryRequest expenseCategoryRequest) {
        return null;
    }

    @Override
    public void deleteExpenseCategoryByKey(String key) {

    }

    @Override
    public void deleteAllExpenseCategory(String uid) {

    }

    private UserInfo getUserInfo() throws JsonProcessingException {

        return userContextService.getUserInfo();
    }
}
