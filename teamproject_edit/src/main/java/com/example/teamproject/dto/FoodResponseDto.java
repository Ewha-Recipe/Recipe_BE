package com.example.teamproject.dto;

import com.example.teamproject.entity.Food;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL) // 이 부분 추가
public class FoodResponseDto {
    private int state;
    private Long food_id;
    private String title;
    private String content;
    private String username;
    private String food_image;
    private Food.Category category;
    private String ingredients;
    private Food.Difficulty difficulty;
    private LocalDateTime food_date;

    // 전체 게시글 조회를 위한 생성자
    public FoodResponseDto(Food food, boolean isDetail) {
        this.state = 200;
        this.food_id = food.getFood_id();
        this.title = food.getTitle();
        this.username = food.getUsername();
        this.food_image = food.getFood_image();
        this.food_date = food.getFood_date();
        this.category = food.getCategory();
        this.difficulty = food.getDifficulty();
        if (isDetail) {
            this.content = food.getContent();
            this.ingredients = food.getIngredients();
        }
    }
}