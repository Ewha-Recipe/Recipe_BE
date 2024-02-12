package com.example.teamproject.service;

import com.example.teamproject.dto.CommentDto;
import com.example.teamproject.entity.Comment;
import com.example.teamproject.entity.Food;
import com.example.teamproject.repository.CommentRepository;
import com.example.teamproject.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service // 서비스로 선언
public class CommentService {
    @Autowired
    private CommentRepository commentRepository; // 댓글로 리파지터리 객체 주입
    @Autowired
    private FoodRepository foodRepository; // 게시글 리파지터리 객체 주입

    public List<CommentDto> comments(Long foodId) {
     /*   // 1. 댓글 조회
        List<Comment> comments = commentRepository.findByFoodId(foodId);
        // 2. 엔티티 -> DTO 변환
        List<CommentDto> dtos = new ArrayList<CommentDto>();
        for (int i = 0; i < comments.size(); i++) { // 조회한 댓글 엔티티 수만큼 반복하기
            Comment c = comments.get(i); // 조회한 댓글 엔티티 하나씩 가져오기
            CommentDto dto = CommentDto.createCommentDto(c); // 엔티티를 DTO로 변환
            dtos.add(dto); // 변환한 DTO를 dtos 리스트에 삽입
        }*/
        // 3. 결과 반환
        return commentRepository.findByFoodId(foodId)// 엔티티 목록 조회
                .stream() // 댓글 엔티티 목록을 스트림으로 변환
                .map(comment -> CommentDto.createCommentDto(comment)) // 엔티티를 DTO로 매핑
                .collect(Collectors.toList()); // 스트림을 리스트로 변환


    }

    @Transactional
    public CommentDto create(Long foodId, CommentDto dto) {
        // 1. 게시글 조회 및 예외 발생
        Food food = FoodRepository.findById(foodId)
                .orElseThrow(() -> new IllegalArgumentException("댓글 생성 실패! " +
                        "대상 게시글이 없습니다.")); // 없으면 에러 메세지 출력
        // 2. 댓글 엔티티 생성
        Comment comment = Comment.createComment(dto, food);

        // 3. 댓글 엔티티를 DB에 저장
        Comment created = commentRepository.save(comment);

        // 4. DTO로 변환해 반환
        return CommentDto.createCommentDto(created);



    }

    public CommentDto update(Long comment_id, CommentDto dto) {
        // 1. 댓글 조회 및 예외 발생
        Comment target = commentRepository.findById(comment_id)
                .orElseThrow(() -> new IllegalArgumentException("댓글 수정 실패!" +
                                "대상 댓글이 없습니다."));
        // 2. 댓글 수정
        target.patch(dto);
        // 3. DB로 갱신
        Comment updated = commentRepository.save(target);
        // 4. 댓글 엔티티를 DTO로 변환 및 반환
        return CommentDto.createCommentDto(updated);
    }

    public CommentDto delete(Long comment_id) {
        // 1. 댓글 조회 및 예외 발생
        Comment target = commentRepository.findById(comment_id)
                .orElseThrow(() -> new IllegalArgumentException("댓글 삭제 실패! " +
                        "대상이 없습니다."));
        // 2. 댓글 삭제
        commentRepository.delete(target);
        // 3. 삭제 댓글을 DTO로 변환 및 반환
        return CommentDto.createCommentDto(target);
    }
}
