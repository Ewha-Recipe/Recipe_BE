package com.example.teamproject.dto;

import com.example.teamproject.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString
public class CommentDto {
    private Long comment_id; // 댓글의 id
    private Long foodId; // 해당 댓글의 부모 (Food) 게시글 id
    private String username; //  댓글 단 사람의 닉네임
    private String content; // 댓글 본문

    public static CommentDto createCommentDto(Comment comment) {
        return new CommentDto(
                comment.getComment_id(), // 댓글의 엔티티의 id
                comment.getFood().getFood_id(), // 댓글 엔티티가 속한 Food 게시글의 id
                comment.getUsername(), // 댓글 엔티티의 닉네임
                comment.getContent() // 댓글 엔티티의 content

        );
    }
}
