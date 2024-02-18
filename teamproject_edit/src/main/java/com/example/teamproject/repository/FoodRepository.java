package com.example.teamproject.repository;

import java.util.ArrayList;
import org.springframework.data.repository.CrudRepository;
import com.example.teamproject.entity.Food;

// JPA에서 제공하는 리포지토리 인터페이스를 활용
// CrudRepository 인터페이스를 상속받아 ㅇ엔티티를 관리(생성, 조회, 수정, 삭제)할 수 있음
public interface FoodRepository extends CrudRepository<Food, Long> {
    @Override
    ArrayList<Food> findAll(); // Iterable -> ArrayList 수정
}
