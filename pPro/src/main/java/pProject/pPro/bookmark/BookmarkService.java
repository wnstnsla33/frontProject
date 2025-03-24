package pProject.pPro.bookmark;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pProject.pPro.User.UserRepository;
import pProject.pPro.User.UserService;
import pProject.pPro.bookmark.DTO.BookmarkResponseDTO;
import pProject.pPro.entity.BookmarkEntity;
import pProject.pPro.entity.PostEntity;
import pProject.pPro.entity.UserEntity;
import pProject.pPro.post.PostRepository;
import pProject.pPro.post.PostService;

@Service
@Transactional
public class BookmarkService {
	@Autowired
	private BookmarkRepository bookmarkRepository;

	@Autowired
	private PostRepository postRepository;
	@Autowired
	private UserRepository userRepository;
	private BookmarkResponseDTO bookmarkResponseDTO;

	public boolean toggle(Long postId, String email) {
		UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("유저를 찾을 수 없습니다."));
		PostEntity post = postRepository.findById(postId).orElseThrow(() -> new RuntimeException("게시글이 존재하지 않습니다."));
		Optional<BookmarkEntity> bookmark = bookmarkRepository.findBookmark(postId, email);
		if (bookmark.isPresent()) {
			bookmarkRepository.delete(bookmark.get());
			return false;
		} else {
			bookmarkRepository.save(new BookmarkEntity(post, user));
			return true;
		}
	}

	public ResponseEntity getMyBookmarkList(String email) {
		return bookmarkResponseDTO.bookmarkTypeSuccees(bookmarkRepository.bookmarkNumberByUser(email));
	}

	public BookmarkEntity getBookmark(Long postId, String email) {
		Optional<BookmarkEntity> bookmark = bookmarkRepository.findBookmark(postId, email);
		if (bookmark.isPresent()) {
			return bookmark.get();
		}
		else return null;
	}
}
