package pProject.pPro.bookmark;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pProject.pPro.User.UserRepository;
import pProject.pPro.bookmark.DTO.PostBookmarkResponseDTO;
import pProject.pPro.bookmark.exception.BookmarkErrorCode;
import pProject.pPro.bookmark.exception.BookmarkException;
import pProject.pPro.entity.BookmarkEntity;
import pProject.pPro.entity.PostEntity;
import pProject.pPro.entity.UserEntity;
import pProject.pPro.global.ServiceUtils;
import pProject.pPro.post.PostRepository;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class BookmarkService {
	private final BookmarkRepository bookmarkRepository;
	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final ServiceUtils utils;

	public PostBookmarkResponseDTO toggle(Long postId, String email) {
		log.info("********** toggle() 호출 - postId: {}, email: {} **********", postId, email);

		UserEntity user = utils.findUser(email);
		PostEntity post = utils.findPost(postId);
		Optional<BookmarkEntity> bookmark = bookmarkRepository.findBookmark(postId, email);
		boolean isBookmarked;
		if (bookmark.isPresent()) {
			bookmarkRepository.delete(bookmark.get());
			post.setBookmarkCount(post.getBookmarkCount() - 1);
			isBookmarked=false;
		} else {
			bookmarkRepository.save(new BookmarkEntity(post, user));
			 post.setBookmarkCount(post.getBookmarkCount() + 1);
			 isBookmarked=true;
		}
		return new PostBookmarkResponseDTO(post, isBookmarked);
	}

	public BookmarkEntity getBookmark(Long postId, String email) {
		log.info("********** getBookmark() 호출 - postId: {}, email: {} **********", postId, email);

		return bookmarkRepository.findBookmark(postId, email)
				.orElseThrow(() -> {
					log.warn("********** 북마크 없음 - postId: {}, email: {} **********", postId, email);
					return new BookmarkException(BookmarkErrorCode.NOT_FOUND_BOOKMARK);
				});
	}
}
