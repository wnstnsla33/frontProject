package pProject.pPro.Report.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportControllerDTO<T> {
    private boolean success; // 성공 여부
    private String message;  // 메시지 (성공 or 오류 메시지)
    private T data;          // 성공 시 데이터, 실패 시 null

    // 성공용 static 생성자
    public static <T> ReportControllerDTO<T> success(String message, T data) {
        return new ReportControllerDTO<>(true, message, data);
    }

    // 실패용 static 생성자
    public static <T> ReportControllerDTO<T> fail(String message) {
        return new ReportControllerDTO<>(false, message, null);
    }
}
