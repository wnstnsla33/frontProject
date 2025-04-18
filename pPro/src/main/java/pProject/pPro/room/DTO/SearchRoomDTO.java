package pProject.pPro.room.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import org.springframework.web.bind.annotation.ModelAttribute;

@Getter
@Setter
@ToString
public class SearchRoomDTO {

    // 🔍 방 제목 검색
    private String title;

    // 🏷️ 방 카테고리 (스터디, 게임 등)
    private String roomType;

    // 📍 시/도
    private String sido;

    // 📍 시/군/구
    private String sigungu;

    // 📄 페이징
    private int page = 0;  // 기본값 0 페이지
    private String email;
}
