package pProject.pPro.room.DTO;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pProject.pPro.RoomUser.DTO.HostUserStatus;
import pProject.pPro.entity.HostUserEntity;
import pProject.pPro.entity.RoomEntity;

@Getter
@Setter
@NoArgsConstructor
public class RoomDTO {
	private String roomId;
	@NotBlank(message = "채팅방 제목을 입력해주세요.")
	@Size(min = 1, max = 50, message = "방 제목은 1자 이상 25자 이하로 입력해주세요.")
	private String roomTitle;
	@NotBlank(message = "채팅방 타입을 선택해주세요.")
	private String roomType;
	private Long HostId;
	private MultipartFile roomSaveImg;
	private String roomImg;
	@NotBlank(message = "채팅방 소개를 입력해주세요.")
	@Size(min = 1, max = 1000, message = "채팅방 소개는 1000자 이하로 입력해주세요.")
	private String roomContent;
	@Min(value = 1, message = "최소 1명 이상의 인원이 필요합니다.")
	@Max(value = 30, message = "최대 인원은 30명까지 가능합니다.")
	private int maxParticipants;
	private int curPaticipants;
	private String recentChat;
	private String hostName;
	@Size(min = 4, max = 30, message = "비밀번호는 4자 이상 30자 이하로 입력해주세요.")
	private String secretePassword;
	private boolean isPrivate = false;
	@NotNull(message = "모임 시간을 선택해주세요.")
	@Future(message = "시간을 뒤로 설정하셔야 합니다.")
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	private LocalDateTime meetingTime;
	private LocalDateTime roomModifiedDate;
	private LocalDateTime roomCreatedAt;
	@NotBlank(message = "시/도 정보를 입력해주세요.")
	private String sido;
	@NotBlank(message = "시/군/구 정보를 입력해주세요.")
	private String sigungu;
	private List<RoomMemberDTO> roomMembers;

	public RoomDTO(RoomEntity room) {
		if (room.getSecretePassword() != null) {
			this.isPrivate = true;
			this.roomId = room.getRoomId();
			this.roomTitle = room.getRoomTitle();
			this.hostName = room.getCreateUser().getUserName();
		} else {
			this.roomId = room.getRoomId();
			this.roomTitle = room.getRoomTitle();
			this.roomType = room.getRoomType();
			this.roomContent = room.getRoomContent();
			this.maxParticipants = room.getRoomMaxParticipants();
			this.meetingTime = room.getMeetingTime();
			this.curPaticipants = room.getCurPaticipants();
			this.roomCreatedAt = room.getRoomCreatDate();
			this.roomModifiedDate = room.getRoomModifiedDate();
			this.hostName = room.getCreateUser().getUserNickName();
			this.roomImg = room.getRoomImg();
			this.sido = room.getAddress().getSido();
			this.sigungu = room.getAddress().getSigungu();
			this.HostId = room.getCreateUser().getUserId();
			this.recentChat = calculateTimeAgo(room.getRecentChat());
			this.roomMembers = room.getHostUsers().stream()
				    .filter(hostUser -> hostUser.getStatus() == HostUserStatus.JOINED)
				    .map(hostUser -> new RoomMemberDTO(
				        hostUser.getUser().getUserImg(),
				        hostUser.getUser().getUserNickName(),
				        hostUser.getUser().getUserId()
				    ))
				    .toList();
			
		}

	}

	public RoomDTO(RoomEntity room, boolean verify) {
		this.roomId = room.getRoomId();
		this.roomTitle = room.getRoomTitle();
		this.roomType = room.getRoomType();
		this.roomContent = room.getRoomContent();
		this.maxParticipants = room.getRoomMaxParticipants();
		this.meetingTime = room.getMeetingTime();
		this.curPaticipants = room.getCurPaticipants();
		this.roomCreatedAt = room.getRoomCreatDate();
		this.roomModifiedDate = room.getRoomModifiedDate();
		this.hostName = room.getCreateUser().getUserName();
		this.roomImg = room.getRoomImg();
		this.HostId = room.getCreateUser().getUserId();
		this.recentChat = calculateTimeAgo(room.getRecentChat());
		this.roomMembers = room.getHostUsers().stream()
			    .filter(hostUser -> hostUser.getStatus() == HostUserStatus.JOINED)
			    .map(hostUser -> new RoomMemberDTO(
			        hostUser.getUser().getUserImg(),
			        hostUser.getUser().getUserNickName(),
			        hostUser.getUser().getUserId()
			    ))
			    .toList();
		this.isPrivate = room.getSecretePassword() != null ? true : false;
	}

	public RoomDTO(HostUserEntity hostUserEntity) {
		if (hostUserEntity.getRoom().getSecretePassword() != null) {
			this.isPrivate = true;
			this.roomId = hostUserEntity.getRoom().getRoomId();
			this.roomTitle = hostUserEntity.getRoom().getRoomTitle();
			this.hostName = hostUserEntity.getRoom().getCreateUser().getUserName();
		} else {
			this.roomId = hostUserEntity.getRoom().getRoomId();
			this.roomTitle = hostUserEntity.getRoom().getRoomTitle();
			this.roomType = hostUserEntity.getRoom().getRoomType();
			this.roomContent = hostUserEntity.getRoom().getRoomContent();
			this.maxParticipants = hostUserEntity.getRoom().getRoomMaxParticipants();
			this.meetingTime = hostUserEntity.getRoom().getMeetingTime();
			this.curPaticipants = hostUserEntity.getRoom().getCurPaticipants();
			this.roomCreatedAt = hostUserEntity.getRoom().getRoomCreatDate();
			this.roomModifiedDate = hostUserEntity.getRoom().getRoomModifiedDate();
			this.hostName = hostUserEntity.getRoom().getCreateUser().getUserName();
			this.roomImg = hostUserEntity.getRoom().getRoomImg();
			this.recentChat = calculateTimeAgo(hostUserEntity.getRoom().getRecentChat());
			this.HostId = hostUserEntity.getRoom().getCreateUser().getUserId();
		}

	}

	public RoomDTO(HostUserEntity hostUserEntity, boolean verify) {
		this.roomId = hostUserEntity.getRoom().getRoomId();
		this.roomTitle = hostUserEntity.getRoom().getRoomTitle();
		this.roomType = hostUserEntity.getRoom().getRoomType();
		this.roomContent = hostUserEntity.getRoom().getRoomContent();
		this.maxParticipants = hostUserEntity.getRoom().getRoomMaxParticipants();
		this.meetingTime = hostUserEntity.getRoom().getMeetingTime();
		this.curPaticipants = hostUserEntity.getRoom().getCurPaticipants();
		this.roomCreatedAt = hostUserEntity.getRoom().getRoomCreatDate();
		this.roomModifiedDate = hostUserEntity.getRoom().getRoomModifiedDate();
		this.hostName = hostUserEntity.getRoom().getCreateUser().getUserName();
		this.roomImg = hostUserEntity.getRoom().getRoomImg();
		this.HostId = hostUserEntity.getRoom().getCreateUser().getUserId();
		this.recentChat = calculateTimeAgo(hostUserEntity.getRoom().getRecentChat());
	}
	public String calculateTimeAgo(LocalDateTime time) {
	    if (time == null) {
	        return "";
	    }
	    LocalDateTime now = LocalDateTime.now();
	    Duration duration = Duration.between(time, now);

	    long minutes = duration.toMinutes();
	    long hours = duration.toHours();
	    long days = duration.toDays();

	    if (minutes < 1) {
	        return "방금 전";
	    } else if (minutes < 60) {
	        return minutes + "분 전";
	    } else if (hours < 24) {
	        return hours + "시간 전";
	    } else if (days < 30) {
	        return days + "일 전";
	    } else {
	        return "오래 전";
	    }
	}
}
