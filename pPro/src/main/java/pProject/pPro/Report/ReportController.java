package pProject.pPro.Report;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.jaxb.SpringDataJaxb.PageDto;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import pProject.pPro.Report.DTO.CreateReportDTO;
import pProject.pPro.Report.DTO.ReportControllerDTO;
import pProject.pPro.Report.DTO.ReportPageDTO;
import pProject.pPro.Report.DTO.ReportResponseDTO;
import pProject.pPro.Report.DTO.ReportStatusDTO;
import pProject.pPro.ControllerUtils;
import pProject.pPro.Report.ReportStatus;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final ControllerUtils utils;
    // ✅ 1. 신고 등록
    
    @PostMapping("/reports")
    public ResponseEntity<ReportControllerDTO<Void>> createReport(@Valid @RequestBody CreateReportDTO dto,
    		@AuthenticationPrincipal UserDetails user) {
        reportService.createReport(dto,utils.findEmail(user));
        return ResponseEntity.ok(ReportControllerDTO.success("신고가 접수되었습니다."));
    }

   

    // ✅ 5. 내가 한 신고 목록 (마이페이지)
    @GetMapping("/reports/me")
    public ResponseEntity<ReportControllerDTO<List<ReportResponseDTO>>> getMyReports(@AuthenticationPrincipal UserDetails user) {
        List<ReportResponseDTO> result = reportService.getMyReports(utils.findEmail(user));
        return ResponseEntity.ok(ReportControllerDTO.success("내 신고 목록 조회 성공", result));
    }
}
