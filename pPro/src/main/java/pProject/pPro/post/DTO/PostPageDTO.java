package pProject.pPro.post.DTO;

import java.util.List;

import pProject.pPro.bookmark.DTO.PostBookmarkResponseDTO;

public class PostPageDTO {
	private List<PostListDTO> posts;
    private int postCount;

    public PostPageDTO(List<PostListDTO> posts,int pages) {
        this.posts = posts;
        this.postCount = pages;
    }

    public List<PostListDTO> getPosts() {
        return posts;
    }

    public int getPostCount() {
        return postCount;
    }
}
