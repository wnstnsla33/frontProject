package pProject.pPro.reply.DTO;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pProject.pPro.entity.ReplyEntity;

@Getter
@Setter
@NoArgsConstructor
public class ReplyListDTO {
    private Long replyId;
    private Long parentReplyId;
    private Long userId;
    private String content;
    private String userNickname;
    private String userImg;
    private LocalDate createDate;
    private LocalDate modifiedDate;
    private Long postId;
    private boolean isLiked;
    private int likeCount;
    private List<ReplyListDTO> replys = new ArrayList();

    public ReplyListDTO(ReplyEntity reply, boolean isLiked) {
        this.replyId = reply.getReplyId();
        this.parentReplyId = reply.getParent() != null ? reply.getParent().getReplyId() : null;
        this.userId = reply.getUser().getUserId();
        this.content = reply.getContent();
        this.userNickname = reply.getUser().getUserNickName();
        this.userImg = reply.getUser().getUserImg();
        this.createDate = reply.getCreateDate();
        this.modifiedDate = reply.getModifiedDate();
        this.postId = reply.getPost().getPostId();
        this.isLiked = isLiked;
        this.likeCount = reply.getLikeCount();
    }
    public ReplyListDTO(
    	    Long replyId,
    	    Long parentReplyId,
    	    Long userId,
    	    String content,
    	    String userNickname,
    	    String userImg,
    	    LocalDate createDate,
    	    LocalDate modifiedDate,
    	    Long postId,
    	    boolean isLiked,
    	    int likeCount
    	) {
    	    this.replyId = replyId;
    	    this.parentReplyId = parentReplyId;
    	    this.userId = userId;
    	    this.content = content;
    	    this.userNickname = userNickname;
    	    this.userImg = userImg;
    	    this.createDate = createDate;
    	    this.modifiedDate = modifiedDate;
    	    this.postId = postId;
    	    this.isLiked = isLiked;
    	    this.likeCount = likeCount;
    	}

}
