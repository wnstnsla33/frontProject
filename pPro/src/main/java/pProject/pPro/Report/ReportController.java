package pProject.pPro.Report;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import pProject.pPro.Report.DTO.CreateReportDTO;
import pProject.pPro.Report.DTO.ReportControllerDTO;
import pProject.pPro.Report.DTO.ReportResponseDTO;
import pProject.pPro.Report.DTO.SearchDTO;
import pProject.pPro.Report.ReportStatus;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    // ✅ 1. 신고 등록
    
    @PostMapping("/reports")
    public ResponseEntity<ReportControllerDTO<Void>> createReport(@RequestBody CreateReportDTO dto,
    		@AuthenticationPrincipal UserDetails user) {
        reportService.createReport(dto, user.getUsername());
        return ResponseEntity.ok(ReportControllerDTO.success("신고가 접수되었습니다.", null));
    }

    // ✅ 2. 신고 단일 조회
    @GetMapping("/reports/{reportId}")
    public ResponseEntity<ReportControllerDTO<ReportResponseDTO>> findReport(@PathVariable("reportId") Long id) {
        ReportResponseDTO result = reportService.findReport(id);
        return ResponseEntity.ok(ReportControllerDTO.success("신고 조회 성공", result));
    }

    // ✅ 3. 신고 상태 변경 (관리자)
    @PatchMapping("/admin/reports/{reportId}")
    public ResponseEntity<ReportControllerDTO<Void>> updateStatus(@PathVariable("reportId") Long id,
                                                                   @RequestParam ReportStatus status) {
        reportService.updateStatus(status, id);
        return ResponseEntity.ok(ReportControllerDTO.success("신고 상태가 변경되었습니다.", null));
    }

    // ✅ 4. 관리자 신고 리스트 조회 (검색 + 페이징)
    @GetMapping("/admin/reports")
    public ResponseEntity<ReportControllerDTO<Page<ReportResponseDTO>>> getReportList(@ModelAttribute SearchDTO dto) {
        Page<ReportResponseDTO> result = reportService.getReportList(dto);
        return ResponseEntity.ok(ReportControllerDTO.success("신고 리스트 조회 성공", result));
    }

    // ✅ 5. 내가 한 신고 목록 (마이페이지)
    @GetMapping("/reports/me")
    public ResponseEntity<ReportControllerDTO<List<ReportResponseDTO>>> getMyReports(@AuthenticationPrincipal UserDetails user) {
        List<ReportResponseDTO> result = reportService.getMyReports(user.getUsername());
        return ResponseEntity.ok(ReportControllerDTO.success("내 신고 목록 조회 성공", result));
    }
}
