package com.example.teamproject.controller;

import com.example.teamproject.dto.CommentDto;
import com.example.teamproject.dto.FoodForm;
import com.example.teamproject.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Controller
public class CommentController {
    @Autowired
    private CommentService commentService;

    //1. 댓글 조회
    @GetMapping("/foods/{foodId}/comments") // 댓글 조회 요청 접수
    public ResponseEntity<List<CommentDto>> comments(@PathVariable Long foodId)
    { // comments() 메서드 생성

        //서비스에 위임
        List<CommentDto> dtos = commentService.comments(foodId);
        //결과 응답
        return ResponseEntity.status(HttpStatus.OK).body(dtos);
    }

    //2. 댓글 생성
    @PostMapping("/foods/{foodId}/comments") //댓글 생성 요청 접수
    public ResponseEntity<CommentDto> create(@PathVariable Long foodId,
                                             @RequestBody CommentDto dto) { //create() 메서드 생성
        // 서비스에 위임
        CommentDto createdDto = commentService.create(foodId, dto);
        // 결과 응답
        return ResponseEntity.status(HttpStatus.OK).body(createdDto);


    }
    //3. 댓글 수정
    @PatchMapping("foods/comments/{comment_id}")
    public ResponseEntity<CommentDto> update(@PathVariable Long comment_id, @RequestBody CommentDto dto) {
        //서비스에 위임
        CommentDto updatedDto = commentService.update(comment_id, dto);
        //결과 응담
        return ResponseEntity.status(HttpStatus.OK).body(updatedDto);
    }

    //4. 댓글 삭제
    @DeleteMapping("foods/comments/{comment_id}")
    public ResponseEntity<CommentDto> delete(@PathVariable Long comment_id) {
        // 서비스에 위임
        CommentDto deletedDto = commentService.delete(comment_id);
        //결과 응답
        return ResponseEntity.status(HttpStatus.OK).body(deletedDto);

    }
}
