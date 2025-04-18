package pProject.pPro.securityConfig;

import java.util.Map;

public class GoogleResponse implements OAuth2Response{

	 private final Map<String, Object> attribute;
	 
	 public GoogleResponse(Map<String, Object> attribute) {

	        this.attribute = attribute;
	    }
	@Override
	public String getProvider() {
		// TODO Auto-generated method stub
		return "google";
	}

	@Override
	public String getproviderId() {
		// TODO Auto-generated method stub
		 return attribute.get("sub").toString();
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		 return attribute.get("name").toString();
	}

	@Override
	public String getBirthDay() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAge() {
		// TODO Auto-generated method stub
		return "15";
	}

	@Override
	public String getSex() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
