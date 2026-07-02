package com.travelshare.controller;

import com.travelshare.common.Result;
import com.travelshare.dto.CommentDTO;
import com.travelshare.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/add")
    public Result add(@RequestBody CommentDTO dto){

        commentService.add(dto);

        return Result.success();
    }

    @GetMapping("/list")
    public Result list(String type, Long targetId){
        return Result.success(commentService.list(type, targetId));
    }

    @GetMapping("/list-with-username")
    public Result listWithUsername(String type, Long targetId){
        return Result.success(commentService.listWithUsername(type, targetId));
    }
}