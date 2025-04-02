package pProject.pPro.reply;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pProject.pPro.User.UserRepository;
import pProject.pPro.entity.PostEntity;
import pProject.pPro.entity.ReplyEntity;
import pProject.pPro.entity.UserEntity;
import pProject.pPro.post.PostRepository;
import pProject.pPro.reply.DTO.ReplyListDTO;
import pProject.pPro.reply.DTO.ReplyRegDTO;
import pProject.pPro.reply.DTO.ReplyResponseDTO;

@Service
@Transactional
public class ReplyService {

	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PostRepository postRepository;
	@Autowired
	private ReplyRepository replyRepository;

	public ReplyServiceValue saveReply(Long postId, ReplyRegDTO replyRegDTO, String email) {
		// 유저와 게시글 정보 가져오기
		UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
		PostEntity post = postRepository.getPostDetail(postId)
				.orElseThrow(() -> new RuntimeException("Post not found"));

		// 부모 댓글 처리 (있으면 가져오고, 없으면 null)
		ReplyEntity parentReply = null;
		if (replyRegDTO.getParentReplyId() != null) {
			parentReply = replyRepository.findById(replyRegDTO.getParentReplyId())
					.orElseThrow(() -> new RuntimeException("Parent reply not found"));
		}

		try {
			// 부모 댓글이 있으면 부모 댓글을, 없으면 null로 저장
			ReplyEntity reply = new ReplyEntity(post, user, replyRegDTO, parentReply);
			ReplyEntity regReply = replyRepository.save(reply);
			post.setReplyCount(post.getReplyCount() + 1);
			return new ReplyServiceValue<ReplyListDTO>(ReplyServiceEnum.SUCCESS, new ReplyListDTO(regReply));
		} catch (Exception e) {
			e.printStackTrace();
			return new ReplyServiceValue<String>(ReplyServiceEnum.FAIL, "댓글 등록에 실패했습니다.");
		}
	}

	public List<ReplyListDTO> findReplyByPost(Long postId) {
		List<ReplyEntity> replyList = replyRepository.findReplyByPost(postId, Sort.by(Sort.Direction.ASC, "id"));
		List<ReplyListDTO> dtoList = replyList.stream().map(ReplyListDTO::new).toList(); // Java 16 이상이라면 .toList(), 아니면
																							// collect(Collectors.toList())
		return dtoList;
	}

	public ReplyServiceValue updateReply(Long ReplyId, String content, String email) {
		ReplyEntity replyEntity = replyRepository.findById(ReplyId).get();
		if (!replyEntity.getUser().getUserEmail().equals(email))
			return new ReplyServiceValue<ReplyListDTO>(ReplyServiceEnum.EMAIL_NOTMATCH, null);
		try {
			replyEntity.setContent(content);
			replyEntity.setModifiedDate(LocalDate.now());
		} catch (NoSuchElementException e) {
			e.printStackTrace();
			return new ReplyServiceValue<ReplyListDTO>(ReplyServiceEnum.FAIL, null);
		}
		return new ReplyServiceValue<ReplyListDTO>(ReplyServiceEnum.SUCCESS, new ReplyListDTO(replyEntity));
	}

	public ReplyServiceValue deleteReply(Long postId, Long replyId, String email) {
		Optional<PostEntity> postEntity = postRepository.findById(replyId);
		PostEntity post = postRepository.getPostDetail(postId)
				.orElseThrow(() -> new RuntimeException("Post not found"));
		ReplyEntity reply = replyRepository.findById(replyId).get();
		if (!reply.getUser().getUserEmail().equals(email))
			return new ReplyServiceValue<String>(ReplyServiceEnum.EMAIL_NOTMATCH, "등록한 계정이아닙니다.");
		try {
			replyRepository.deleteById(reply.getReplyId());
		} catch (Exception e) {
			e.printStackTrace();
			return new ReplyServiceValue<String>(ReplyServiceEnum.FAIL, "댓글 삭제에 실패했습니다");
			// TODO: handle exception
		}
		post.setReplyCount(post.getReplyCount()-1);
		return new ReplyServiceValue<String>(ReplyServiceEnum.SUCCESS, "댓글 삭제에 성공했습니다");
	}
	// 내가 적은 댓글 보기,

}
