//package pProject.pPro.entity;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//import org.hibernate.annotations.ManyToAny;
//
//import jakarta.persistence.Column;
//import jakarta.persistence.Entity;
//import jakarta.persistence.FetchType;
//import jakarta.persistence.GeneratedValue;
//import jakarta.persistence.GenerationType;
//import jakarta.persistence.Id;
//import jakarta.persistence.JoinColumn;
//import jakarta.persistence.ManyToOne;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import pProject.pPro.RoomUser.DTO.RoomAddress;
//
//@Entity
//@Getter
//@Setter
//@NoArgsConstructor
//public class PrivateChatRoomEntity {
//	@Id
//	@GeneratedValue(strategy = GenerationType.SEQUENCE)
//	private Long privateChatRoomId;
//	
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "my_id")
//	private UserEntity my;
//	
//	@ManyToOne(fetch = FetchType.LAZY)
//	@JoinColumn(name = "friends_id")
//	private UserEntity friend;
//	
//	private LocalDateTime createdTime;
//}
