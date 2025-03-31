package pProject.pPro.bookmark;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import pProject.pPro.bookmark.DTO.BookmarkResponseDTO;
import pProject.pPro.bookmark.DTO.PostBookmarkResponseDTO;
import pProject.pPro.entity.BookmarkEntity;
import pProject.pPro.entity.PostEntity;
import pProject.pPro.post.PostService;
import pProject.pPro.post.DTO.PostControllerDTO;
import pProject.pPro.post.DTO.PostServiceDTO;

@RestController
public class BookmarkController {
	@Autowired
	BookmarkService bookmarkService;
	@Autowired
	PostService postService;
	
	private BookmarkResponseDTO bookmarkResponseDTO;
	
	void makeMessage(String methodName) {
		System.out.println("********************************"+methodName);
	}
	@PostMapping("/post/bookmark/{postId}")
	@Transactional
	public ResponseEntity postRemoveBookmark(@PathVariable("postId")Long postId,@AuthenticationPrincipal UserDetails loginUser) {
		makeMessage("bookmark/postId");
		boolean isBookmarked = bookmarkService.toggle(postId, loginUser.getUsername());
		PostServiceDTO<PostEntity> updatePost = postService.incrementBookmarkCount(postId, isBookmarked);
		return bookmarkResponseDTO.bookmarkTypeSuccees(new PostBookmarkResponseDTO(updatePost.getData(), isBookmarked));
	}
	
//	@GetMapping("post/regBookmark/{postId}")
//	public ResponseEntity getBookmark(@PathVariable("postId")Long postId,@AuthenticationPrincipal UserDetails user) {
//		makeMessage("getMybookmark");
//		BookmarkEntity bookmark = bookmarkService.getBookmark(postId, user.getUsername());
//		if(bookmark==null)return null;
//		else return bookmarkResponseDTO.bookmarkTypeSuccees(new BookmarkResponseDTO());
//	}
	@GetMapping("/post/bookmarkList")
	public ResponseEntity getBookmarkList(@AuthenticationPrincipal UserDetails user) {
		makeMessage("내 북마크리스트");
		return bookmarkService.getMyBookmarkList(user.getUsername());
	}
}
