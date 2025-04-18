package pProject.pPro.post;

import org.apache.ibatis.annotations.Mapper;

import pProject.pPro.entity.PostEntity;

@Mapper
public interface PostMapper {
	PostEntity getPostDetail(Long postId);
}
