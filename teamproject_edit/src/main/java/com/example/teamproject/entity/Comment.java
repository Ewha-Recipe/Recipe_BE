package com.example.teamproject.entity;

import com.example.teamproject.dto.CommentDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY) // DB가 자동으로 1씩 증가
    private Long comment_id; // 대표키
    @ManyToOne
    @JoinColumn(name="food_e_id")
    private Food food; // 해당 댓글의 부모 (Food) 게시글
    @Column
    private String username; // 댓글 단 사람의 닉네임
    @Column
    private String content; // 댓글 본문


    public static Comment createComment(CommentDto dto, Food food) {
        // 예외 발생
        if (dto.getComment_id() != null)
            throw new IllegalArgumentException("댓글 생성 실패! 댓글의 id가 없어야 합니다.");
        if (dto.getFoodId() != food.getFood_id())
        throw new IllegalArgumentException("댓글 생성 실패! 게시글의 id가 잘못됐습니다.")
        // 엔티티 생성 및 반환
        return new Comment(
                dto.getComment_id(), // 댓글 아이디
                food, // 댓글 단 Food 게시글 아이디
                dto.getUsername(), // 댓글단 사람의 닉네임
                dto.getContent() // 댓글 본문
        );
    }


    public void patch(CommentDto dto) {
        //예외 발생
        if(this.comment_id != dto.getComment_id())
            throw new IllegalArgumentException("댓글 수정 실패! 잘못된 id가 입력됐습니다.");

        //객체 갱신
        if (dto.getUsername() != null) //수정할 닉네임 데이터가 있다면
            this.username = dto.getUsername(); //내용 반영
        if (dto.getContent() != null) //수정할 본문 데이터가 있다면
            this.content = dto.getContent(); //내용 반영
    }
}
