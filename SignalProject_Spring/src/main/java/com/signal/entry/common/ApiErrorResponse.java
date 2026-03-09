package com.signal.entry.common;

import java.util.List;

public record ApiErrorResponse(String code, String message, List<ErrorDetail> details) {
}
