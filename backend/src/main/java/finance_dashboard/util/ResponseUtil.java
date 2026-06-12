package finance_dashboard.util;

import finance_dashboard.domain.response.ApiResponse;

public class ResponseUtil {
    public static <T> ApiResponse<T> success(
            int statusCode,
            String message,
            T data
    ) {
        return ApiResponse.<T>builder()
                .status(statusCode)
                .message(message)
                .data(data)
                .build();
    }
}
