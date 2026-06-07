package finance_dashboard.domain.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
public class ErrorResponse {
    private final int statusCode;
    private final String error;
    private final String message;
    private final Instant timestamp;
    private final String path;
    private final List<FieldError> fieldErrors; // cho validation

    @Getter
    @AllArgsConstructor
    public static class FieldError {
        private final String field;
        private final String message;
    }
}
