package com.travelshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travelshare.dto.CommentDTO;
import com.travelshare.dto.CommentVO;
import com.travelshare.entity.Comment;
import com.travelshare.entity.User;
import com.travelshare.mapper.CommentMapper;
import com.travelshare.mapper.UserMapper;
import com.travelshare.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl
        extends ServiceImpl<CommentMapper, Comment>
        implements CommentService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public void add(CommentDTO dto) {
        Comment comment = new Comment();
        comment.setTargetType(dto.getTargetType());
        comment.setTargetId(dto.getTargetId());
        comment.setUserId(dto.getUserId());
        comment.setContent(dto.getContent());
        this.save(comment);
    }

    @Override
    public List<Comment> list(String type, Long targetId) {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("target_type", type);
        wrapper.eq("target_id", targetId);
        return this.list(wrapper);
    }

    @Override
    public List<CommentVO> listWithUsername(String type, Long targetId) {
        QueryWrapper<Comment> wrapper = new QueryWrapper<>();
        wrapper.eq("target_type", type);
        wrapper.eq("target_id", targetId);
        wrapper.orderByAsc("create_time");
        List<Comment> comments = this.list(wrapper);

        return comments.stream().map(c -> {
            CommentVO vo = new CommentVO();
            vo.setId(c.getId());
            vo.setTargetType(c.getTargetType());
            vo.setTargetId(c.getTargetId());
            vo.setUserId(c.getUserId());
            vo.setContent(c.getContent());
            vo.setCreateTime(c.getCreateTime());
            // 查询用户名
            User user = userMapper.selectById(c.getUserId());
            vo.setUsername(user != null ? user.getUsername() : "未知用户");
            return vo;
        }).collect(Collectors.toList());
    }
}
