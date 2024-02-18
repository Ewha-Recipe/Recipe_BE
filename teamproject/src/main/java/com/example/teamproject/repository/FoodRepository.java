package com.example.teamproject.repository;

import java.util.ArrayList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import com.example.teamproject.entity.Food;

// JPA에서 제공하는 리포지토리 인터페이스를 활용
// CrudRepository 인터페이스를 상속받아 ㅇ엔티티를 관리(생성, 조회, 수정, 삭제)할 수 있음
public interface FoodRepository extends JpaRepository<Food, Long> {
    @Override
    @Query("SELECT f FROM Food f ORDER BY f.food_date DESC")
    ArrayList<Food> findAll(); // Iterable -> ArrayList 수정
}
