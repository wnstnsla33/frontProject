package pProject.pPro.entity;

public enum Grade {
    BRONZE,  // 레벨 1~3은 브론즈
    SILVER,  // 레벨 4~6은 실버
    GOLD,    // 레벨 7~9는 골드
    VIP,   // 레벨 10~12는 VIP
    ADMIN,
	BANNED;
    // 레벨로 등급 찾기
    public static Grade fromLevel(int level) {
        if (level >= 1 && level <= 3) {
            return BRONZE;
        } else if (level >= 4 && level <= 6) {
            return SILVER;
        } else if (level >= 7 && level <= 9) {
            return GOLD;
        } else if (level >= 10 ) {
            return VIP;
        } else {
            throw new IllegalArgumentException("Invalid level: " + level);
        }
    }

    // 등급 이름 반환
    public String getName() {
        return this.name(); // enum 이름을 반환 (BRONZE, SILVER, GOLD, VIP)
    }
}