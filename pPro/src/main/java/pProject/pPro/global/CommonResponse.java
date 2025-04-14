package pProject.pPro.global;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CommonResponse<T> {
    private String message;
    private T data;

    public static <T> CommonResponse<T> success(String message, T data) {
        return new CommonResponse<>(message, data);
    }
    public static <T> CommonResponse<T> success(String message) {
        return new CommonResponse<>(message, null);
    }

    public static <T> CommonResponse<T> fail(String message) {
        return new CommonResponse<>(message, null);
    }
}

