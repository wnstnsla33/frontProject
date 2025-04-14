package pProject.pPro.reply;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pProject.pPro.User.exception.UserErrorCode;
import pProject.pPro.User.exception.UserException;
import pProject.pPro.entity.PostEntity;
import pProject.pPro.entity.ReplyEntity;
import pProject.pPro.entity.UserEntity;
import pProject.pPro.global.ServiceUtils;
import pProject.pPro.post.exception.PostErrorCode;
import pProject.pPro.post.exception.PostException;
import pProject.pPro.reply.DTO.ReplyListDTO;
import pProject.pPro.reply.DTO.ReplyRegDTO;
import pProject.pPro.reply.exception.ReplyException;
import pProject.pPro.reply.exception.ReplyErrorCode;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ReplyService {

	private final ReplyRepository replyRepository;
	private final ServiceUtils utils;

	public ReplyListDTO saveReply(Long postId, ReplyRegDTO replyRegDTO, String email) {
		log.info("********** saveReply() 호출 - postId: {}, parentReplyId: {}, email: {} **********",
				postId, replyRegDTO.getParentReplyId(), email);

		UserEntity user = utils.findUser(email);
		PostEntity post = utils.findPost(postId);

		ReplyEntity parentReply = null;
		if (replyRegDTO.getParentReplyId() != null) {
			parentReply = replyRepository.findById(replyRegDTO.getParentReplyId())
					.orElseThrow(() -> {
						log.warn("🚫 부모 댓글 없음 - parentReplyId: {}", replyRegDTO.getParentReplyId());
						return new ReplyException(ReplyErrorCode.PARENT_NOT_FOUND);
					});
		}

		ReplyEntity reply = new ReplyEntity(post, user, replyRegDTO, parentReply);
		ReplyEntity regReply = replyRepository.save(reply);
		post.setReplyCount(post.getReplyCount() + 1);

		log.info("✅ 댓글 저장 완료 - replyId: {}", regReply.getReplyId());
		return new ReplyListDTO(regReply);
	}

	public List<ReplyListDTO> findReplyByPost(Long postId) {
		log.info("********** findReplyByPost() 호출 - postId: {} **********", postId);

		List<ReplyEntity> replyList = replyRepository.findReplyByPost(postId, Sort.by(Sort.Direction.ASC, "id"));
		return replyList.stream().map(ReplyListDTO::new).toList();
	}

	public ReplyListDTO updateReply(Long replyId, String content, String email) {
		log.info("********** updateReply() 호출 - replyId: {}, email: {} **********", replyId, email);

		ReplyEntity replyEntity = utils.findReply(replyId);
		if (!replyEntity.getUser().getUserEmail().equals(email)) {
			log.warn("🚫 댓글 수정 권한 없음 - 요청자: {}, 작성자: {}", email, replyEntity.getUser().getUserEmail());
			throw new UserException(UserErrorCode.INVALID_ID, "작성자만 수정할 수 있습니다.");
		}

		replyEntity.setContent(content);
		replyEntity.setModifiedDate(LocalDate.now());

		log.info("✅ 댓글 수정 완료 - replyId: {}", replyId);
		return new ReplyListDTO(replyEntity);
	}

	public void deleteReply(Long postId, Long replyId, String email) {
		log.info("********** deleteReply() 호출 - postId: {}, replyId: {}, email: {} **********", postId, replyId, email);

		PostEntity post = utils.findPost(postId);
		ReplyEntity reply = utils.findReply(replyId);
		if (!reply.getUser().getUserEmail().equals(email)) {
			log.warn("🚫 댓글 삭제 권한 없음 - 요청자: {}, 작성자: {}", email, reply.getUser().getUserEmail());
			throw new UserException(UserErrorCode.INVALID_ID, "작성자만 삭제할 수 있습니다.");
		}

		replyRepository.deleteById(reply.getReplyId());
		post.setReplyCount(post.getReplyCount() - 1);

		log.info("🗑️ 댓글 삭제 완료 - replyId: {}", replyId);
	}
}
