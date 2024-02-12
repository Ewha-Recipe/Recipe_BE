package com.example.teamproject.repository;


import com.example.teamproject.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    //특정 게시글의 모든 댓글 조회
    @Query(value="SELECT * FROM comment WHERE food_e_id = :foodId", nativeQuery = true) // value 속성에 실행하려는 쿼리 작성
    List<Comment> findByFoodId(Long foodId);

    //특정 닉네임의 모든 댓글 조회
    List<Comment> findByUsername(String username);

}
