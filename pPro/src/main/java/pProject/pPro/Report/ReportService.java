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
import pProject.pPro.Report.DTO.CreateReportDTO;
import pProject.pPro.Report.DTO.ReportPageDTO;
import pProject.pPro.Report.DTO.ReportResponseDTO;
import pProject.pPro.Report.DTO.ReportSearchDTO;
import pProject.pPro.Report.DTO.ReportStatusDTO;
import pProject.pPro.Report.exception.ReportErrorCode;
import pProject.pPro.Report.exception.ReportException;
import pProject.pPro.entity.ChatEntity;
import pProject.pPro.entity.Grade;
import pProject.pPro.entity.MessageEntity;
import pProject.pPro.entity.PostEntity;
import pProject.pPro.entity.ReplyEntity;
import pProject.pPro.entity.ReportEntity;
import pProject.pPro.entity.RoomEntity;
import pProject.pPro.entity.UserEntity;
import pProject.pPro.global.ServiceUtils;
import pProject.pPro.message.MessageRepository;
import pProject.pPro.message.DTO.MessageType;
@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReportService {
	private final ReportRepository reportRepository;
	private final ServiceUtils utils;
	private final MessageRepository messageRepository;
	// 신고 생성
	public void createReport(CreateReportDTO dto, String email) {
		log.info("********** createReport() 호출 - email: {}, targetId: {}, targetType: {} **********", email, dto.getTargetId(), dto.getTargetType());

		UserEntity reporter = utils.findUser(email);
		UserEntity reportedUser = null;
		String parentId = null;
		String chatText = null;

		boolean exists = reportRepository.isAlreadyReported(reporter, dto.getTargetId(), dto.getTargetType());
		if (exists) {
			log.warn("🚫 중복 신고 감지 - reporter: {}, targetId: {}, type: {}", reporter.getUserEmail(), dto.getTargetId(), dto.getTargetType());
			throw new ReportException(ReportErrorCode.DUPLICATE_REPORT);
		}

		// 대상 유저 찾기
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
					chatText = chat.getMessage();
					parentId = chat.getRoom().getRoomId();
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

		log.info("✅ 신고 저장 완료 - reportId: {}, 대상 유저: {}", report.getReportId(), reportedUser.getUserEmail());
	}

	// 신고 상세 조회
	public ReportResponseDTO findReport(Long id) {
		log.info("********** findReport() 호출 - reportId: {} **********", id);
		return new ReportResponseDTO(utils.findReportWithUser(id));
	}

	// 신고 상태 변경
	public ReportStatus updateStatus(ReportStatusDTO dto, long reportId,String email) {
		ReportStatus status = dto.getStatus();
		UserEntity sender = utils.findUser(email);
		ReportEntity report = utils.findReportWithUser(reportId);
		if (status == ReportStatus.ACCEPT) {
			UserEntity user = report.getReportedUser();
			log.info("📛 신고 수락됨 - 신고 유저: {}, 현재 누적: {}", user.getUserEmail(), user.getReportedCount());
			MessageEntity message = new MessageEntity(dto,sender,user);
			messageRepository.save(message);
			if (user.getReportedCount() > 2) {
				log.info("🚫 유저 정지 처리 - 유저: {}", user.getUserEmail());
				user.setReportedDate(LocalDateTime.now());
				user.setUserGrade(Grade.BANNED);
			}
			user.setReportedCount(user.getReportedCount() + 1);
		}

		report.setStatus(status);
		log.info("✅ 신고 상태 변경 완료 - reportId: {}, status: {}", reportId, status);
		return report.getStatus();
	}

	// 관리자용 신고 목록
	public ReportPageDTO getReportList(ReportSearchDTO dto) {
		log.info("********** getReportList() 호출 - page: {}, keyword: {}, status: {} **********", dto.getPage(), dto.getKeyword(), dto.getStatus());

		Pageable pageable = PageRequest.of(dto.getPage(), 20, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<ReportResponseDTO> list = reportRepository.findAllReports(dto.getKeyword(), dto.getStatus(), pageable)
				.map(ReportResponseDTO::new);

		log.info("📋 신고 목록 반환 - 전체 페이지 수: {}, 건수: {}", list.getTotalPages(), list.getTotalElements());
		return new ReportPageDTO(list.getContent(), list.getTotalPages());
	}

	// 내가 한 신고 조회
	public List<ReportResponseDTO> getMyReports(String email) {
		log.info("********** getMyReports() 호출 - email: {} **********", email);

		UserEntity user = utils.findUser(email);
		List<ReportEntity> reports = reportRepository.findByReporter(user);

		log.info("📌 나의 신고 건수: {}", reports.size());
		return reports.stream().map(ReportResponseDTO::new).collect(Collectors.toList());
	}

	private Long parseToLong(String id) {
		try {
			return Long.parseLong(id);
		} catch (NumberFormatException e) {
			log.error("🚨 잘못된 ID 입력 - id: {}", id);
			throw new IllegalArgumentException("다시 실행해주십시오");
		}
	}
}
