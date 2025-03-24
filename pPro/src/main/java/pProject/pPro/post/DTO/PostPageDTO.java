package pProject.pPro.post.DTO;

import java.util.List;

public class PostPageDTO {
	private List<PostListDTO> posts;
    private Long postCount;

    public PostPageDTO(List<PostListDTO> posts,Long pages) {
        this.posts = posts;
        this.postCount = pages;
    }

    public List<PostListDTO> getPosts() {
        return posts;
    }

    public Long getPostCount() {
        return postCount;
    }
}
