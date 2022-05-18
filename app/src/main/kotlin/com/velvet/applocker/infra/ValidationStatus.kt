package com.velvet.applocker.infra

enum class ValidationStatus {
    SUCCESS_REAL_PASSWORD,
    SUCCESS_FAKE_PASSWORD,
    FAILURE_WRONG_PASSWORD,
    FAILURE_NO_PASSWORD_SET
}