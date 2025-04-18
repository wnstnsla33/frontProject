package pProject.pPro.securityConfig;


import java.time.Year;
import java.util.Map;

public class NaverResponse implements OAuth2Response{

	private final Map<String,Object> attribute;
	
	public NaverResponse(Map<String, Object> attribute) {
		this.attribute=(Map<String,Object>)attribute.get("response");
	}
	@Override
	public String getProvider() {
		// TODO Auto-generated method stub
		return "naver";
	}

	@Override
	public String getproviderId() {
		return attribute.get("id").toString();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return attribute.get("name").toString();
	}
	@Override
	public String getBirthDay() {
		// TODO Auto-generated method stub
		return attribute.get("birthyear").toString()+"-"+attribute.get("birthday").toString();
	}
	@Override
	public String getAge() {
		String birthYearStr = attribute.get("birthyear").toString();
        int currentYear = Year.now().getValue();
        int birthYear = Integer.parseInt(birthYearStr);
        int age = currentYear - birthYear;
		return String.valueOf(age);
	}
	@Override
	public String getSex() {
		// TODO Auto-generated method stub
		return attribute.get("gender").toString();
	}

	

}
