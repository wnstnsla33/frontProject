package pProject.pPro.RoomUser;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import pProject.pPro.RoomUser.DTO.RoomUserEnum;
import pProject.pPro.entity.HostUserEntity;
import pProject.pPro.entity.RoomEntity;
import pProject.pPro.entity.UserEntity;

@Service
@Transactional
@RequiredArgsConstructor
public class HostUserService {
	
	private final HostUserRepository hostUserRepository;
	
	public RoomUserEnum saveRoomUser(UserEntity user,RoomEntity room) {
		HostUserEntity hostUserEntity = new  HostUserEntity(room, user);
		hostUserEntity.setJoinedTime(LocalDateTime.now());
		try {
			hostUserRepository.save(hostUserEntity);
			return RoomUserEnum.SUCCES;
		}catch (Exception e) {
			e.printStackTrace();
			// TODO: handle exception
			return RoomUserEnum.FAIL;
		}
	}
	
}
