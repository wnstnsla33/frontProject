package pProject.pPro.reply;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

enum ReplyServiceEnum {
	SUCCESS,FAIL,EMAIL_NOTMATCH;
}
@Getter
@Setter
@AllArgsConstructor
public class ReplyServiceValue<T>{
	private ReplyServiceEnum enumVal;
	private T data;
	
}
