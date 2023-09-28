package com.rkumar0206.mymexpensecategoryservice.service;

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

import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExpenseCategoryServiceImpl implements ExpenseCategoryService {

    private final ExpenseCategoryRepository expenseCategoryRepository;
    private final UserContextService userContextService;

    @Override
    public Page<ExpenseCategory> getExpenseCategoriesByUid(Pageable pageable) {

        return expenseCategoryRepository.findByUid(getUserInfo().getUid(), pageable);
    }

    @Override
    public ExpenseCategoryResponse getExpenseCategoryByKey(String key) {

        String uid = getUserInfo().getUid();

        ExpenseCategory expenseCategory = expenseCategoryRepository.findByKey(key)
                .orElseThrow(() -> new ExpenseCategoryException(ErrorMessageConstants.NO_CATEGORY_FOUND_ERROR));

        if (!expenseCategory.getUid().equals(uid)) {
            throw new ExpenseCategoryException(ErrorMessageConstants.PERMISSION_DENIED);
        }

        return ModelMapper.buildExpenseCategoryResponse(expenseCategory);
    }

    @Override
    public ExpenseCategoryResponse createExpenseCategory(ExpenseCategoryRequest expenseCategoryRequest) {

        String uid = getUserInfo().getUid();

        if (expenseCategoryRepository.findByCategoryNameAndUid(
                expenseCategoryRequest.getCategoryName(), uid).isPresent()
        ) {

            log.error(ErrorMessageConstants.CATEGORY_NAME_ALREADY_PRESENT_ERROR);
            throw new ExpenseCategoryException(ErrorMessageConstants.CATEGORY_NAME_ALREADY_PRESENT_ERROR);
        }

        ExpenseCategory expenseCategory = new ExpenseCategory(
                null,
                expenseCategoryRequest.getCategoryName(),
                expenseCategoryRequest.getCategoryDescription(),
                expenseCategoryRequest.getImageUrl(),
                new Date(System.currentTimeMillis()),
                new Date(System.currentTimeMillis()),
                uid,
                uid.substring(0, 8) + "_" + UUID.randomUUID()
        );

        return ModelMapper.buildExpenseCategoryResponse(expenseCategoryRepository.save(expenseCategory));
    }

    @Override
    public ExpenseCategoryResponse updateExpenseCategory(ExpenseCategoryRequest expenseCategoryRequest) {

        String uid = getUserInfo().getUid();

        ExpenseCategory expenseCategoryInDBByRequest = expenseCategoryRepository.findByKey(expenseCategoryRequest.getKey())
                .orElseThrow(() -> new ExpenseCategoryException(ErrorMessageConstants.NO_CATEGORY_FOUND_ERROR));

        if (!expenseCategoryInDBByRequest.getUid().equals(uid))
            throw new ExpenseCategoryException(ErrorMessageConstants.PERMISSION_DENIED);

        if (!expenseCategoryRequest.getCategoryName().equals(expenseCategoryInDBByRequest.getCategoryName())) {

            if (expenseCategoryRepository.findByCategoryNameAndUid(
                    expenseCategoryRequest.getCategoryName(), uid).isPresent()
            ) {

                log.error(ErrorMessageConstants.CATEGORY_NAME_ALREADY_PRESENT_ERROR);
                throw new ExpenseCategoryException(ErrorMessageConstants.CATEGORY_NAME_ALREADY_PRESENT_ERROR);
            }
        }

        expenseCategoryInDBByRequest.updateExpenseCategoryFields(expenseCategoryRequest);

        return ModelMapper.buildExpenseCategoryResponse(expenseCategoryRepository.save(expenseCategoryInDBByRequest));
    }

    @Override
    public void deleteExpenseCategoryByKey(String key) {

        String uid = getUserInfo().getUid();

        ExpenseCategory expenseCategoryInDBByRequest = expenseCategoryRepository.findByKey(key)
                .orElseThrow(() -> new ExpenseCategoryException(ErrorMessageConstants.NO_CATEGORY_FOUND_ERROR));

        if (!expenseCategoryInDBByRequest.getUid().equals(uid))
            throw new ExpenseCategoryException(ErrorMessageConstants.PERMISSION_DENIED);

        expenseCategoryRepository.delete(expenseCategoryInDBByRequest);
    }

    @Override
    public void deleteAllExpenseCategory(String uid) {

        // todo : delete all
    }

    private UserInfo getUserInfo() {

        return userContextService.getUserInfo();
    }
}
