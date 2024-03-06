package msa.userserver.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiResponse<T>{
    private Boolean isSuccess;
    private T response;
    private ErrorResponse errorResponse;

    public ApiResponse(Boolean isSuccess, T response, ErrorResponse errorResponse) {
        this.isSuccess = isSuccess;
        this.response = response;
        this.errorResponse = errorResponse;
    }
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }
}
