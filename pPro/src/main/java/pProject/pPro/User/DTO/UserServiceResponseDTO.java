package pProject.pPro.User.DTO;

import pProject.pPro.User.UserEnum;

public class UserServiceResponseDTO<T> {
	private UserEnum status;
	private T t; // 성공했을 때 찾은 이메일 반환

	// 생성자
	
	public UserServiceResponseDTO(UserEnum status, T t) {
		this.status = status;
		this.t = t;
	}
	public UserServiceResponseDTO(UserEnum status) {
		this.status = status;
	}
	// Getter
	public UserEnum getStatus() {
		return status;
	}

	public T getData() {
		return t;
	}
}
