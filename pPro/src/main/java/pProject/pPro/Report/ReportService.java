package pProject.pPro.Report;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pProject.pPro.ServiceUtils;
import pProject.pPro.Report.DTO.CreateReportDTO;
import pProject.pPro.Report.DTO.ReportPageDTO;
import pProject.pPro.Report.DTO.ReportResponseDTO;
import pProject.pPro.Report.DTO.ReportSearchDTO;
import pProject.pPro.Report.exception.ReportErrorCode;
import pProject.pPro.Report.exception.ReportException;
import pProject.pPro.entity.ChatEntity;
import pProject.pPro.entity.Grade;
import pProject.pPro.entity.PostEntity;
import pProject.pPro.entity.ReplyEntity;
import pProject.pPro.entity.ReportEntity;
import pProject.pPro.entity.RoomEntity;
import pProject.pPro.entity.UserEntity;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReportService {
	private final ReportRepository reportRepository;
	private final ServiceUtils utils;

	// 신고 생성
	public void createReport(CreateReportDTO dto, String email) {
		UserEntity reporter = utils.findUser(email);
		UserEntity reportedUser = null;
		String parentId = null;
		String chatText = null;
		boolean exists = reportRepository.isAlreadyReported(reporter, dto.getTargetId(), dto.getTargetType());
		if (exists) {
			throw new ReportException(ReportErrorCode.DUPLICATE_REPORT);
		}
		// reportedUserId가 없으면 대상에서 user 찾아서 넣어주기
		if (dto.getReportedUserId() != null) {
			reportedUser = utils.findUserById(dto.getReportedUserId());
		} else {
			switch (dto.getTargetType()) {
			case ROOM -> {
				RoomEntity room = utils.findRoom(dto.getTargetId(), "신고 대상자가 존재하지 않습니다.");
				reportedUser = room.getCreateUser();
			}
			case POST -> {
				PostEntity post = utils.findPost(parseToLong(dto.getTargetId()), "신고 대상자가 존재하지 않습니다.");
				reportedUser = post.getUser();
			}
			case CHAT -> {
				ChatEntity chat = utils.findChat(parseToLong(dto.getTargetId()), "신고 대상자가 존재하지 않습니다.");
				reportedUser = chat.getUser();
				chatText = chat.getMessage();				parentId = chat.getRoom().getRoomId();
			}
			case REPLY -> {
				ReplyEntity reply = utils.findReply(parseToLong(dto.getTargetId()), "신고 대상자가 존재하지 않습니다.");
				reportedUser = reply.getUser();
				chatText = reply.getContent();
				parentId = reply.getPost().getPostId().toString();
			}
			}
		}

		ReportEntity report = new ReportEntity(dto, reporter, reportedUser);
		report.setParentId(parentId);
		report.setChatText(chatText);
		reportRepository.save(report);
	}

	// 신고 검색
	public ReportResponseDTO findReport(Long id) {
		return new ReportResponseDTO(utils.findReportWithUser(id));
	}

	// 신고 상태 변경
	public ReportStatus updateStatus(ReportStatus status, long reportId) {
		ReportEntity report = utils.findReportWithUser(reportId);
		if (status == ReportStatus.ACCEPT) {
			UserEntity user = report.getReportedUser();
			if (user.getReportedCount() > 2) {
				user.setReportedDate(LocalDateTime.now());
				user.setUserGrade(Grade.BANNED);
			} // 3번부터 계속 한달씩 BANNED상태
			user.setReportedCount(user.getReportedCount() + 1);
		}
		report.setStatus(status);
		return report.getStatus();
	}

	// 신고 리스트
	public ReportPageDTO getReportList(ReportSearchDTO dto) {
		Pageable pageable = PageRequest.of(dto.getPage(), 20, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<ReportResponseDTO> list = reportRepository.findAllReports(dto.getKeyword(), dto.getStatus(), pageable)
				.map(ReportResponseDTO::new);
		return new ReportPageDTO(list.getContent(), list.getTotalPages());
	}

	// 나의 신고 리스트
	public List<ReportResponseDTO> getMyReports(String email) {
		UserEntity user = utils.findUser(email);
		List<ReportEntity> reports = reportRepository.findByReporter(user);
		return reports.stream().map(ReportResponseDTO::new).collect(Collectors.toList());
	}

	private Long parseToLong(String id) {
		try {
			return Long.parseLong(id);
		} catch (NumberFormatException e) {
			log.error("잘못된 ID입력 id: " + id);
			throw new IllegalArgumentException("다시 실행해주십시오");
		}
	}
}
