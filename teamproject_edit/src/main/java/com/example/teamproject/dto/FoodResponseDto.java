package com.example.teamproject.dto;

import com.example.teamproject.entity.Food;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FoodResponseDto {
    private int state;
    private Long food_id;
    private String title;
    private String content;

    public FoodResponseDto(Food food) {
        this.state = 200;
        this.food_id = food.getFood_id();
        this.title = food.getTitle();
        this.content = food.getContent();
    }
}