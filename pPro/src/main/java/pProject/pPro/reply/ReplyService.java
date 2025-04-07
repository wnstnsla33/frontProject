package pProject.pPro.reply;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pProject.pPro.EntityUtils;
import pProject.pPro.User.UserRepository;
import pProject.pPro.entity.PostEntity;
import pProject.pPro.entity.ReplyEntity;
import pProject.pPro.entity.UserEntity;
import pProject.pPro.post.PostRepository;
import pProject.pPro.post.exception.PostException;
import pProject.pPro.post.exception.PostErrorCode;
import pProject.pPro.User.Exception.UserException;
import pProject.pPro.User.Exception.UserErrorCode;
import pProject.pPro.reply.DTO.ReplyListDTO;
import pProject.pPro.reply.DTO.ReplyRegDTO;
import pProject.pPro.reply.exception.ReplyException;
import pProject.pPro.reply.exception.ReplyErrorCode;

@Service
@Transactional
@RequiredArgsConstructor
public class ReplyService {

	private final UserRepository userRepository;
	private final PostRepository postRepository;
	private final ReplyRepository replyRepository;
	private final EntityUtils utils;
	public ReplyListDTO saveReply(Long postId, ReplyRegDTO replyRegDTO, String email) {
		UserEntity user = utils.findUser(email);
		PostEntity post = utils.findPost(postId);
		ReplyEntity parentReply = null;
		if (replyRegDTO.getParentReplyId() != null) {
			parentReply = replyRepository.findById(replyRegDTO.getParentReplyId())
					.orElseThrow(() -> new ReplyException(ReplyErrorCode.PARENT_NOT_FOUND));
		}

		ReplyEntity reply = new ReplyEntity(post, user, replyRegDTO, parentReply);
		ReplyEntity regReply = replyRepository.save(reply);
		post.setReplyCount(post.getReplyCount() + 1);
		return new ReplyListDTO(regReply);
	}

	public List<ReplyListDTO> findReplyByPost(Long postId) {
		List<ReplyEntity> replyList = replyRepository.findReplyByPost(postId, Sort.by(Sort.Direction.ASC, "id"));
		return replyList.stream().map(ReplyListDTO::new).toList();
	}

	public ReplyListDTO updateReply(Long replyId, String content, String email) {
		ReplyEntity replyEntity = utils.findReply(replyId);
		if (!replyEntity.getUser().getUserEmail().equals(email)) {
			throw new UserException(UserErrorCode.INVALID_ID, "작성자만 수정할 수 있습니다.");
		}
		replyEntity.setContent(content);
		replyEntity.setModifiedDate(LocalDate.now());
		return new ReplyListDTO(replyEntity);
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
}
