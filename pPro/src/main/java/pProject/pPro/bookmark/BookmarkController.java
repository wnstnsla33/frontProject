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

import pProject.pPro.CommonResponse;
import pProject.pPro.bookmark.DTO.PostBookmarkResponseDTO;
import pProject.pPro.entity.BookmarkEntity;
import pProject.pPro.entity.PostEntity;
import pProject.pPro.post.PostService;

@RestController
public class BookmarkController {
	@Autowired
	BookmarkService bookmarkService;
	@Autowired
	PostService postService;
	
	
	@PostMapping("/post/bookmark/{postId}")
	@Transactional
	public ResponseEntity postRemoveBookmark(@PathVariable("postId")Long postId,@AuthenticationPrincipal UserDetails loginUser) {
		boolean isBookmarked = bookmarkService.toggle(postId, loginUser.getUsername());
		PostEntity updatePost = postService.incrementBookmarkCount(postId, isBookmarked);
		return ResponseEntity.ok(CommonResponse.success("북마크 되었습니다.",new PostBookmarkResponseDTO(updatePost, isBookmarked)));
	}
	
}
