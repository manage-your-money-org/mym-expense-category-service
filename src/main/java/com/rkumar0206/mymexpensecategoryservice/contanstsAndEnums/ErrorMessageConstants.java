package com.rkumar0206.mymexpensecategoryservice.contanstsAndEnums;

public class ErrorMessageConstants {

    public static final String CATEGORY_NAME_ALREADY_PRESENT_ERROR = "A expense category with this name is already present";
    public static final String NO_CATEGORY_FOUND_ERROR = "Category not found";
    public static final String USER_NOT_AUTHORIZED_ERROR = "User not authorized";
    public static final String USER_INFO_NOT_PROVIDED_ERROR = "User info not provided or error in parsing user info value found in header";
    public static final String PERMISSION_DENIED = "Permission denied";
    public static final String INVALID_REQUEST_BODY_CREATE = "Please send all the mandatory values for creating expense category";
    public static final String INVALID_REQUEST_BODY_UPDATE = "Please send all the mandatory values for updating expense category";
    public static final String INVALID_EXPENSE_CATEGORY_KEY = "Expense category key is not valid";
    public static final String MAX_PAGE_SIZE_ERROR = "Max page size should be less than or equal to %s";

    public static final String USER_INFO_NOT_PROPER = "User information provided is not proper";
}
