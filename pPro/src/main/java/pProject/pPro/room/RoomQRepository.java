package pProject.pPro.room;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;
import pProject.pPro.entity.QHostUserEntity;
import pProject.pPro.entity.QRoomEntity;
import pProject.pPro.entity.RoomEntity;

@Repository
@RequiredArgsConstructor
public class RoomQRepository {
	private final JPAQueryFactory queryFactory;

	public Page<RoomEntity> searchRooms(String title, String roomType, String sido, String sigungu, Pageable pageable) {
	    QRoomEntity room = QRoomEntity.roomEntity;
	    QHostUserEntity hostUser = QHostUserEntity.hostUserEntity;

	    BooleanBuilder condition = buildSearchCondition(room, title, roomType, sido, sigungu);

	    List<RoomEntity> content = queryFactory
	            .selectFrom(room)
	            .leftJoin(room.hostUsers, hostUser).fetchJoin()
	            .leftJoin(hostUser.user).fetchJoin()
	            .where(condition)
	            .offset(pageable.getOffset())
	            .limit(pageable.getPageSize())
	            .fetch();

	    long total = queryFactory
	            .select(room.count())
	            .from(room)
	            .where(condition)
	            .fetchOne();

	    return new PageImpl<>(content, pageable, total);
	}
	
	
	
	
	
	
	

	private BooleanBuilder buildSearchCondition(QRoomEntity room, String title, String roomType, String sido, String sigungu) {
	    BooleanBuilder builder = new BooleanBuilder();

	    if (title != null && !title.isBlank()) {
	        builder.and(room.roomTitle.containsIgnoreCase(title));
	    }
	    if (roomType != null && !roomType.isBlank()) {
	        builder.and(room.roomType.eq(roomType));
	    }
	    if (sido != null && !sido.isBlank()) {
	        builder.and(room.address.sido.eq(sido));
	    }
	    if (sigungu != null && !sigungu.isBlank()) {
	        builder.and(room.address.sigungu.eq(sigungu));
	    }

	    builder.and(room.meetingTime.goe(LocalDateTime.now())); // 오늘 이후 모임만

	    return builder;
	}


}
