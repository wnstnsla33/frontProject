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
import jakarta.websocket.server.ServerEndpoint;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pProject.pPro.Report.DTO.CreateReportDTO;
import pProject.pPro.Report.DTO.ReportResponseDTO;
import pProject.pPro.Report.DTO.ReportServiceDTO;
import pProject.pPro.Report.DTO.SearchDTO;
import pProject.pPro.Report.exception.ReportException;
import pProject.pPro.User.UserRepository;
import pProject.pPro.User.Exception.UserErrorCode;
import pProject.pPro.User.Exception.UserException;
import pProject.pPro.chat.ChatRepository;
import pProject.pPro.chat.exception.ChatException;
import pProject.pPro.entity.ChatEntity;
import pProject.pPro.entity.Grade;
import pProject.pPro.entity.PostEntity;
import pProject.pPro.entity.ReportEntity;
import pProject.pPro.entity.RoomEntity;
import pProject.pPro.entity.UserEntity;
import pProject.pPro.post.PostRepository;
import pProject.pPro.post.exception.PostErrorCode;
import pProject.pPro.post.exception.PostException;
import pProject.pPro.room.RoomRepository;
import pProject.pPro.room.excption.RoomErrorCode;
import pProject.pPro.room.excption.RoomException;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ReportService {
	private ReportRepository reportRepository;
	private UserRepository userRepository;
	private RoomRepository roomRepository;
	private PostRepository postRepository;
	private ChatRepository chatRepository;

	// 신고 생성
	public void createReport(CreateReportDTO dto, String email) {
		UserEntity reporter = findUserByEmail(email);
		UserEntity reportedUser = null;
		boolean exists = reportRepository.isAlreadyReported(reporter.getUserId(), dto.getTargetId(),
				dto.getTargetType());
		if (exists) {
			throw new ReportException("이미 신고하신 대상입니다.", "DUPLICATE_REPORT");
		}
		// reportedUserId가 없으면 대상에서 user 찾아서 넣어주기
		if (dto.getReportedUserId() != null) {
			reportedUser = findUserById(dto.getReportedUserId());
		} else {
			switch (dto.getTargetType()) {
			case ROOM -> {
				RoomEntity room = roomRepository.findById(dto.getTargetId())
						.orElseThrow(() -> new RoomException(RoomErrorCode.INVALID_ID));
				reportedUser = room.getCreateUser();
			}
			case POST -> {
				PostEntity post = postRepository.findById(parseToLong(dto.getTargetId()))
						.orElseThrow(() -> new PostException(PostErrorCode.POST_NOT_FOUND));
				reportedUser = post.getUser();
			}
			case CHAT -> {
				ChatEntity chat = chatRepository.findById(parseToLong(dto.getTargetId()))
						.orElseThrow(() -> new ChatException("해당 채팅이 존재하지 않습니다.", "INVALID_ID"));
				reportedUser = chat.getUser();
			}
			}
		}

		ReportEntity report = new ReportEntity(dto, reporter, reportedUser);
		reportRepository.save(report);
	}

	// 신고 검색
	public ReportResponseDTO findReport(Long id) {
		return new ReportResponseDTO(
				reportRepository.findReport(id).orElseThrow(() -> new ReportException("해당 신고가 없습니다.", "INVALID_ID")));
	}

	// 신고 상태 변경
	public void updateStatus(ReportStatus status, long reportId) {
		ReportEntity report = findReportWithUserInfo(reportId);
		if (status == ReportStatus.ACCEPT) {
			UserEntity user = report.getReportedUser();
			if (user.getReportedCount() > 2) {
				user.setReportedDate(LocalDateTime.now());
				user.setUserGrade(Grade.BANNED);
			}//3번부터 계속 한달씩 BANNED상태
			user.setReportedCount(user.getReportedCount()+1);
		}
		report.setStatus(status);
	}

	// 신고 리스트
	public Page<ReportResponseDTO> getReportList(SearchDTO dto) {
		Pageable pageable = PageRequest.of(dto.getPage(), 20, Sort.by(Sort.Direction.DESC, "createdAt"));
		return reportRepository.findAllReports(dto.getKeyword(), dto.getStatus(), pageable).map(ReportResponseDTO::new);
	}

	// 나의 신고 리스트
	public List<ReportResponseDTO> getMyReports(String email) {
		UserEntity user = findUserByEmail(email);
		List<ReportEntity> reports = reportRepository.findByReporter(user);
		return reports.stream().map(ReportResponseDTO::new).collect(Collectors.toList());
	}

	public UserEntity findUserByEmail(String email) {
		return userRepository.findByEmail(email).orElseThrow(() -> new UserException(UserErrorCode.INVALID_NAME));
	}

	public UserEntity findUserById(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new UserException(UserErrorCode.INVALID_ID));
	}

	public ReportEntity findReportWithUserInfo(Long ReportId) {
		return reportRepository.findReport(ReportId)
				.orElseThrow(() -> new ReportException("해당 아이디가 없습니다.", "INVALID_ID"));
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
