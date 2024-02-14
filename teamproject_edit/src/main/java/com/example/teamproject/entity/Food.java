package com.example.teamproject.entity;

import com.example.teamproject.dto.FoodForm;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity // 엔티티임을 선언. JPA에서 제공하는 어노테이션, 이 어노테이션이 붙은 클래스르 기반으로 DB 테이블 생성
// 테이블 이름은 클래스 이름과 동일하게 Food로 생성됨
@Getter // 롬복으로 게터 추가
@AllArgsConstructor // Food() 생성자를 대체하는 어노테이션 추가
@NoArgsConstructor // 기본 생성자 추가 어노테이션
@ToString // toString() 메소드를 대체하는 어노테이션 추가
public class Food {
    @Id // 엔티티의 대푯값으로 id 선언하고 @Id 어노테이션 붙임
    @GeneratedValue(strategy = GenerationType.IDENTITY) // DB가 id 자동 생성
    // Aritcle 엔티티 중 제목과 내용이 같은 것이 있더라도 대푯값 di로 다른 글임을 구분할 수 있음
    private Long food_id;
    @Column // title 필드 선언, DB에서 인식할 수 있게 @Column 붙임. DB 테이블의 열과 연결됨
    private String title;
    @Column // content 필드 선언, DB에서 인식할 수 있게 @Column 붙임. DB 테이블의 열과 연결됨
    private String content;


    public void patch(Food food) { // 수정할 내용이 있는 경우에만 동작
        if(food.title != null) // 수정 엔티티의 title에 갱신할 값이 있다면 this(target)의 title을 갱신함
            this.title = food.title;
        if(food.content != null) // 수정 엔티티의 content에 갱신할 값이 있다면 this(target)의 content를 갱신함
            this.content = food.content;
    }

    public void update(FoodForm form) {
        if(form.getTitle() != null) {
            this.title = form.getTitle();
        }
        if(form.getContent() != null) {
            this.content = form.getContent();
        }
    }
}

