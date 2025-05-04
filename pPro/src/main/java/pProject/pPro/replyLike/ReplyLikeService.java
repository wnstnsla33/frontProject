package pProject.pPro.replyLike;

import java.util.Optional;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import pProject.pPro.entity.ReplyEntity;
import pProject.pPro.entity.ReplyLikeEntity;
import pProject.pPro.entity.UserEntity;
import pProject.pPro.global.ServiceUtils;
import pProject.pPro.replyLike.dto.ReplyLikeResponseDTO;

@Service
@Transactional
@RequiredArgsConstructor
public class ReplyLikeService {

	private final ServiceUtils utils;
	private final ReplyLikeRepository replyLikeRepository;
	
	public ReplyLikeResponseDTO saveReplyLike(String email,Long replyId) {
		UserEntity user = utils.findUser(email);
		Optional<ReplyLikeEntity> replyLike = replyLikeRepository.findReplyLike(user.getUserId(), replyId);
		ReplyEntity reply = utils.findReply(replyId);
		if(replyLike.isPresent()) {
			replyLikeRepository.deleteById(replyLike.get().getReplyLikeId());
			reply.decreaseLikeCount();
			return new ReplyLikeResponseDTO(false, reply.getLikeCount());
		}
		ReplyLikeEntity newReplyLike = new ReplyLikeEntity(reply, user);
		replyLikeRepository.save(newReplyLike);
		reply.increaseLikeCount();
		return new ReplyLikeResponseDTO(true, reply.getLikeCount());
	}
}
