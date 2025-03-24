package pProject.pPro.User.DTO;

import pProject.pPro.User.UserFindResult;

public class userServiceResponseDTO {
	private UserFindResult status;
	private String data; // 성공했을 때 찾은 이메일 반환

	// 생성자
	public userServiceResponseDTO(UserFindResult status, String data) {
		this.status = status;
		this.data = data;
	}

	// Getter
	public UserFindResult getStatus() {
		return status;
	}

	public String getData() {
		return data;
	}
}
