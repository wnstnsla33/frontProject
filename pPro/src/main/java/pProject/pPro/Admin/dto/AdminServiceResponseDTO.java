package pProject.pPro.Admin.dto;

import lombok.Getter;
import lombok.Setter;
import pProject.pPro.User.UserEnum;
@Getter
@Setter
public class AdminServiceResponseDTO<T> {
	private AdminEnum status;
	private T data; // 성공했을 때 찾은 이메일 반환
	private int pages;
	// 생성자
	public AdminServiceResponseDTO(AdminEnum status, T data,int pages) {
		this.status = status;
		this.data = data;
		this.pages=pages;
	}
	public AdminServiceResponseDTO(AdminEnum status, T data) {
		this.status = status;
		this.data = data;
	}
	public AdminServiceResponseDTO(AdminEnum status) {
		this.status = status;
	}
	public T getData() {
		return data;
	}
	
}
