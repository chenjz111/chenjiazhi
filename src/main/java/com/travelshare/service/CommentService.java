package com.travelshare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travelshare.dto.CommentDTO;
import com.travelshare.dto.CommentVO;
import com.travelshare.entity.Comment;

import java.util.List;

public interface CommentService extends IService<Comment> {

    void add(CommentDTO dto);

    List<Comment> list(String type, Long targetId);

    List<CommentVO> listWithUsername(String type, Long targetId);
}