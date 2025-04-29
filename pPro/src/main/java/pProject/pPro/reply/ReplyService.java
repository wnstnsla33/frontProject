package pProject.pPro.reply;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pProject.pPro.User.exception.UserErrorCode;
import pProject.pPro.User.exception.UserException;
import pProject.pPro.entity.PostEntity;
import pProject.pPro.entity.ReplyEntity;
import pProject.pPro.entity.ReplyLikeEntity;
import pProject.pPro.entity.UserEntity;
import pProject.pPro.global.ServiceUtils;
import pProject.pPro.post.exception.PostErrorCode;
import pProject.pPro.post.exception.PostException;
import pProject.pPro.reply.DTO.ReplyListDTO;
import pProject.pPro.reply.DTO.ReplyRegDTO;
import pProject.pPro.reply.exception.ReplyException;
import pProject.pPro.replyLike.ReplyLikeRepository;
import pProject.pPro.reply.exception.ReplyErrorCode;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReplyService {

	private final ReplyRepository replyRepository;
	private final ReplyLikeRepository replyLikeRepository;
	private final ServiceUtils utils;

	public ReplyListDTO saveReply(Long postId, ReplyRegDTO replyRegDTO, String email) {

		UserEntity user = utils.findUser(email);
		PostEntity post = utils.findPost(postId);

		ReplyEntity parentReply = null;
		if (replyRegDTO.getParentReplyId() != null) {
			parentReply = replyRepository.findById(replyRegDTO.getParentReplyId()).orElseThrow(() -> {
				log.warn("🚫 부모 댓글 없음 - parentReplyId: {}", replyRegDTO.getParentReplyId());
				return new ReplyException(ReplyErrorCode.PARENT_NOT_FOUND);
			});
		}

		ReplyEntity reply = new ReplyEntity(post, user, replyRegDTO, parentReply);
		ReplyEntity regReply = replyRepository.save(reply);
		post.setReplyCount(post.getReplyCount() + 1);

		return new ReplyListDTO(regReply,false);
	}

	public List<ReplyListDTO> findReplyByPost(Long postId, String email) {
		List<ReplyListDTO> replyList = null;
		if (email == null) {
			replyList = replyRepository.findReplyDTOByPost(postId); // 또는 withoutLike
			 replyList = buildReplyTree(replyList);
		} else {
			UserEntity user = utils.findUser(email);
			replyList = replyRepository.findReplyDTOByPostLogin(postId, user.getUserId()); // 또는 withoutLike
			replyList= buildReplyTree(replyList);
		}
		return replyList;
	}

	public ReplyListDTO updateReply(Long replyId, String content, String email) {

		ReplyEntity replyEntity = utils.findReply(replyId);
		if (!replyEntity.getUser().getUserEmail().equals(email)) {
			throw new UserException(UserErrorCode.INVALID_ID, "작성자만 수정할 수 있습니다.");
		}
		UserEntity user = utils.findUser(email); 
		replyEntity.setContent(content);
		replyEntity.setModifiedDate(LocalDate.now());
		Optional<ReplyLikeEntity> isLiked = replyLikeRepository.findReplyLike(user.getUserId(), replyId);
		if(isLiked.isPresent())return new ReplyListDTO(replyEntity,true);
		return new ReplyListDTO(replyEntity,false);
	}

	public void deleteReply(Long postId, Long replyId, String email) {

		PostEntity post = utils.findPost(postId);
		ReplyEntity reply = utils.findReply(replyId);
		if (!reply.getUser().getUserEmail().equals(email)) {
			throw new UserException(UserErrorCode.INVALID_ID, "작성자만 삭제할 수 있습니다.");
		}

		replyRepository.deleteById(reply.getReplyId());
		post.setReplyCount(post.getReplyCount() - 1);

	}
	
	public List<ReplyListDTO> buildReplyTree(List<ReplyListDTO> flatReplies) {
	    Map<Long, ReplyListDTO> replyMap = flatReplies.stream()
	        .collect(Collectors.toMap(ReplyListDTO::getReplyId, r -> r));

	    List<ReplyListDTO> rootReplies = new ArrayList();

	    for (ReplyListDTO reply : flatReplies) {
	        Long parentId = reply.getParentReplyId();
	        if (parentId == null) {
	            // 부모 댓글이면 root로
	            rootReplies.add(reply);
	        } else {
	            // 자식이면 부모 찾아서 add
	            ReplyListDTO parent = replyMap.get(parentId);
	            if (parent != null) {
	                parent.getReplys().add(reply);
	            }
	        }
	    }

	    return rootReplies;
	}
}
