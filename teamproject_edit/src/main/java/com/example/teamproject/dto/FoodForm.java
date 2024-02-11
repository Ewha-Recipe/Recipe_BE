package com.example.teamproject.dto;

import com.example.teamproject.entity.Food;
import lombok.AllArgsConstructor;
import lombok.ToString;

@AllArgsConstructor // 클래스 안쪽의 모든 필드(title, content)들을 매개변수로 하는 생성자가 자동으로 만들어짐
@ToString // toString() 메소드를 사용하는 것과 같은 효과
public class FoodForm {
    private Long id; // edit.mustache에 input 태그로 id를 추가했으므로 DTO에도 id 추가
    private String title; // 제목을 받을 필드
    private String content; // 내용을 받을 필드

    public Food toEntity() {
        // Food 클래스의 생성자 형식에 맞게 작성(id, title, content를 매개변수로 받음)
        // 객체에 id 정보는 없으므로 첫 번째 전달값은 null
        return new Food(id, title, content); // 생성자 입력 양식에 맞게 작성
    }
}