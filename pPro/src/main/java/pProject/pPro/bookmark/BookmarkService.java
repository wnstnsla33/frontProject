package pProject.pPro.bookmark;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import pProject.pPro.EntityUtils;
import pProject.pPro.User.UserRepository;
import pProject.pPro.User.UserService;
import pProject.pPro.bookmark.exception.BookmarkErrorCode;
import pProject.pPro.bookmark.exception.BookmarkException;
import pProject.pPro.entity.BookmarkEntity;
import pProject.pPro.entity.PostEntity;
import pProject.pPro.entity.UserEntity;
import pProject.pPro.post.PostRepository;
import pProject.pPro.post.PostService;

@Service
@Transactional
@RequiredArgsConstructor
public class BookmarkService {
	private final BookmarkRepository bookmarkRepository;

	private final PostRepository postRepository;
	private final UserRepository userRepository;
	private final EntityUtils utils;
	public boolean toggle(Long postId, String email) {
		UserEntity user = utils.findUser(email);
		PostEntity post = utils.findPost(postId);
		Optional<BookmarkEntity> bookmark = bookmarkRepository.findBookmark(postId, email);
		if (bookmark.isPresent()) {
			bookmarkRepository.delete(bookmark.get());
			return false;
		} else {
			bookmarkRepository.save(new BookmarkEntity(post, user));
			return true;
		}
	}

	public BookmarkEntity getBookmark(Long postId, String email) {
		return bookmarkRepository.findBookmark(postId, email).orElseThrow(()->new BookmarkException(BookmarkErrorCode.NOT_FOUND_BOOKMARK));
	}
}
