package com.example.teamproject.controller;


import com.example.teamproject.dto.FoodResponseDto;
import com.example.teamproject.entity.Food; // Food이라는 Entity 타입 인식
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.example.teamproject.dto.FoodForm; // FoodForm 패키지 자동으로 임포트
import com.example.teamproject.repository.FoodRepository;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j // 로깅 기능을 위한 어노테이션 추가
@RestController // 컨트롤러 선언
public class FoodController {
    @Autowired // 스프링 부트가 미리 생성해 놓은 리포지토리 객체 주입(DI, 의존성 주입)
    private FoodRepository foodRepository; // foodRepository 객체 선언

    @PostMapping("/foods")
    public ResponseEntity<FoodResponseDto> createFood(@RequestBody FoodForm foodForm){
        log.info(foodForm.toString());

        Food food = foodForm.toEntity();
        log.info(food.toString());

        Food saved = foodRepository.save(food);
        log.info(saved.toString());

        FoodResponseDto response = new FoodResponseDto(saved, true);

        return ResponseEntity.ok(response);
    }


    @GetMapping("/foods/{food_id}")
    public ResponseEntity<FoodResponseDto> show(@PathVariable Long food_id){
        log.info("food_id = " + food_id);
        Food foodEntity = foodRepository.findById(food_id).orElse(null);

        if (foodEntity == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        FoodResponseDto response = new FoodResponseDto(foodEntity, true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/foods")
//    public ResponseEntity<List<FoodResponseDto>> index(){
//        List<Food> foodEntityList = foodRepository.findAll();
//
//        List<FoodResponseDto> response = foodEntityList.stream()
//                .map(FoodResponseDto::new)
//                .collect(Collectors.toList());
//
//        return ResponseEntity.ok(response);
//    }
    public List<FoodResponseDto> getFoods() {
        List<Food> foods = foodRepository.findAll();
        return foods.stream()
                .map(food -> new FoodResponseDto(food, false))
                .collect(Collectors.toList());
    }

    @PatchMapping("/foods/{food_id}")
    public ResponseEntity<FoodResponseDto> update(@PathVariable Long food_id, @RequestBody FoodForm form) {
        Food target = foodRepository.findById(food_id).orElse(null);

        if (target == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        target.update(form);
        Food saved = foodRepository.save(target);

        FoodResponseDto response = new FoodResponseDto(saved, true);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/foods/{food_id}")
    public ResponseEntity<String> delete(@PathVariable Long food_id){
        log.info("삭제 요청이 들어왔습니다!!");

        Food target = foodRepository.findById(food_id).orElse(null);
        log.info(target != null ? target.toString() : "해당 id를 가진 게시글이 없습니다.");

        if (target == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No food found with given id");
        }

        foodRepository.delete(target);
        return ResponseEntity.ok("삭제됐습니다!");
    }
}

