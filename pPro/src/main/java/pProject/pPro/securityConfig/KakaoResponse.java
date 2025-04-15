package pProject.pPro.securityConfig;

import java.util.Map;

public class KakaoResponse implements OAuth2Response {

    private final Map<String, Object> attributes;
    private final Map<String, Object> kakaoAccount;
    private final Map<String, Object> profile;

    public KakaoResponse(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        this.profile = (Map<String, Object>) kakaoAccount.get("profile");
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getproviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getName() {
    	return profile.get("nickname").toString();
    }

    @Override
    public String getBirthDay() {
        return null;
    }

    @Override
    public String getAge() {
    	return "15";
    }

    @Override
    public String getSex() {
    	return null;
    }
}
