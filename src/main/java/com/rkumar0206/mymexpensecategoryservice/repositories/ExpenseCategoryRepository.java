package com.rkumar0206.mymexpensecategoryservice.repositories;

import com.rkumar0206.mymexpensecategoryservice.domain.ExpenseCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpenseCategoryRepository extends MongoRepository<ExpenseCategory, String> {

    Page<ExpenseCategory> findByUid(String uid, Pageable pageable);

    Optional<ExpenseCategory> findByKey(String key);

    Optional<ExpenseCategory> findByCategoryNameAndUid(String categoryName, String uid);
}
