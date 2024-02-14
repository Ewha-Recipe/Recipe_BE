package com.example.teamproject.controller;


import com.example.teamproject.entity.Food; // Food이라는 Entity 타입 인식
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

@Slf4j // 로깅 기능을 위한 어노테이션 추가
@Controller // 컨트롤러 선언
public class FoodController {
    @Autowired // 스프링 부트가 미리 생성해 놓은 리포지토리 객체 주입(DI, 의존성 주입)
    private FoodRepository foodRepository; // foodRepository 객체 선언
    // 스프링부트는 객체 자동으로 생성하므로 따로 구현체 만들 필요 없음
//    @GetMapping("/foods/new") // URL 요청 접수, localhost:8080/foods에 뷰 페이지를 반환하도록 함
//    public String newFoodForm(){ // 메소드 생성 및 반환값 작성
//        return "/foods/new"; // 반환값으로 뷰 페이지의 이름을 적어줌, 파일 경로까지 포함
//    }

    @GetMapping("/foods/new") // "/foods" URL에 GET 요청이 들어왔을 때 이를 처리하는 메소드
    public String newFoodForm(){
        return "/foods/new"; //  "/foods/new"라는 뷰 페이지를 반환
    }


    // 뷰 페이지에서 폼 데이터를 post 방식으로 전송했으므로 컨트롤러에서 받을 때 @PostMapping()으로 받음(@GetMapping X)
//    @PostMapping("/foods/create")  // 괄호 안에는 받는 URL 주소, 정보를 create로 보내는 것이므로 다음과 같이 설정
//    public String createFood(FoodForm form){ // 폼 데이터를 DTO로 받기
//        log.info(form.toString());
//        // System.out.println(form.toString()); // DTO에 폼 데이터가 잘 담겼는지 확인
//        // 1) DTO를 엔티티로 변환
//        // form 객체의 toEntity() 메소드 호출하여 그 반환값을 Food 타입의 food 엔티티에 저장
//        Food food = form.toEntity();
//        log.info(food.toString());
//        // System.out.println(food.toString()); // DTO가 엔티티로 잘 변환되는지 확인 출력
//
//        // 2) 리포지토리로 엔티티를 DB에 저장
//        Food saved = foodRepository.save(food); // food 엔티티를 저장해 saved 객체에 반환
//        log.info(saved.toString());
//        // System.out.println(saved.toString()); // food이 DB에 잘 저장되는지 확인 출력
//
//        // 형식 맞추기, 반환형이 String이기 때문에 빈 문자열 리턴 >> 리다이렉트를 작성할 위치!
//        // 입력 페이지에서 입력 이후 상세 페이지로 이동할 수 있도록 함
//        return "redirect:/foods/" + saved.getFood_id();
//
//    }

//    @PostMapping("/foods") // "/foods" URL에 POST 요청이 들어왔을 때 이를 처리하는 메소드
//    // 사용자가 작성한 게시글 정보(FoodForm 객체)를 받아서 이를 Food 엔티티 객체로 변환, DB에 저장
//    // DB에 저장된 결과를 로그로 출력하고, 저장된 게시글의 상세 페이지로 리다이렉트하는 URL을 반환
//    public String createFood(@RequestBody FoodForm form){
//        log.info(form.toString());
//
//        Food food = form.toEntity();
//        log.info(food.toString());
//
//        Food saved = foodRepository.save(food);
//        log.info(saved.toString());
//
//        return "redirect:/foods/" + saved.getFood_id(); // 상세 페이지로 리다이렉트
//    }

    @PostMapping("/foods")
    public ResponseEntity<Map<String, Object>> createFood(@RequestBody FoodForm form){
        log.info(form.toString());

        Food food = form.toEntity();
        log.info(food.toString());

        Food saved = foodRepository.save(food);
        log.info(saved.toString());

        Map<String, Object> response = new HashMap<>();
        response.put("state", 200);
        response.put("food_id", saved.getFood_id());
        response.put("title", saved.getTitle());
        response.put("content", saved.getContent());

        return ResponseEntity.ok(response);
    }



//    @GetMapping("/foods/{food_id}")
//    // @PathVariable은 URL 요청으로 들어온 전달값을 컨트롤러의 매개변수로 가져오는 어노테이션
//    // 매개변수로 id 받아오기
//    // 모델을 사용하기 위해 show() 메소드의 매개변수로 model 객체를 받아옴
//    public String show(@PathVariable Long id, Model model){
//        log.info("id = " + id); // id를 잘 받았는지 확인하는 로그 찍기
//        // 1. id를 조회해 DB에서 해당 데이터 가져오기
//        // id값으로 데이터를 찾을 때 해당 id가 없으면 null을 반환
//        Food foodEntity = foodRepository.findById(id).orElse(null);
//        // 2. 가져온 데이터를 모델에 등록하기
//        // 모델에 데이터를 등록할 때는 addAttribute() 메소드 사용, 파라미터는 이름과 값
//        model.addAttribute("food", foodEntity);
//        // 3. 조회한 데이터를 사용자에게 보여주기 위한 뷰 페이지 만들고 반환하기
//        // articles 디렉토리 안에 show.mustache 파일 반환
//        return "foods/show"; // 목록으로 돌아가기 위한 링크를 넣을 뷰 파일 확인
//        // show는 mustache 파일, html 파일로 바꿔야 함
//    }

    @GetMapping("/foods/{food_id}")
    public ResponseEntity<Map<String, Object>> show(@PathVariable Long food_id){
        log.info("food_id = " + food_id);
        Food foodEntity = foodRepository.findById(food_id).orElse(null);

        Map<String, Object> response = new HashMap<>();
        if (foodEntity != null) {
            response.put("state", 200);
            response.put("food_id", foodEntity.getFood_id());
            response.put("title", foodEntity.getTitle());
            response.put("content", foodEntity.getContent());
        } else {
            response.put("state", 400);
            response.put("message", "No food found with given id");
        }

        return ResponseEntity.ok(response);
    }


//    @GetMapping("/foods")
//    // localhost:8080/foods URL 요청을 받음, 이 요청이 들어오면 index() 메소드 수행
//    public String index(Model model){ // model 객체 받아오기
//        // 1. DB에서 모든 Food 데이터 가져오기
//        ArrayList<Food> foodEntityList = foodRepository.findAll();
//        // 2. 가져온 food 묶음을 모델에 등록하기
//        model.addAttribute("foodList", foodEntityList); // foodEntityList 등록
//        // 3. 사용자에게 보여줄 뷰 페이지 설정하기
//        return "foods/index"; // index는 mustache 파일, html 파일로 바꿔야 함
//    }

    @GetMapping("/foods")
    public ResponseEntity<List<Map<String, Object>>> index(){
        List<Food> foodEntityList = foodRepository.findAll();

        List<Map<String, Object>> response = new ArrayList<>();
        for (Food food : foodEntityList) {
            Map<String, Object> foodData = new HashMap<>();
            foodData.put("food_id", food.getFood_id());
            foodData.put("title", food.getTitle());
            foodData.put("content", food.getContent());
            response.add(foodData);
        }

        return ResponseEntity.ok(response);
    }


    // 뷰 페이지에서 변수를 사용할 때는 {{중괄호 2개}}, 컨트롤러에서 URL 변수를 사용할 때는 {중괄호 1개}
//    @GetMapping("/foods/{food_id}/edit") // show.mustache 파일에서 연결 요청한 주소 URL을 작성
//    // 수정 요청을 받아 처리할 edit() 메소드를 작성하고, 반환할 수정 페이지를 articles 디렉토리 안에 edit.mustache 파일로 설정
//    // id는 위 URL 주소로부터 매개변수로 받아오고 자료형은 Long으로 작성
//    // GetMapping() 어노테이션의 URL 주소에 있는 id를 받아오는 것이므로 데이터 타입 앞에 @PathVariable 어노테이션 추가
//    // 모델을 사용하기 위해 메소드의 매개변수로 model 객체를 받아옴
//    public String edit(@PathVariable Long id, Model model){
//
//        // 수정할 데이터 가져오기
//        // DB에서 데이터를 가져올 때 리포지토리를 이용 >> articleRepository의 findById(id)메소드로 데이터를 찾아 가져옴
//        // 만약 데이터를 찾지 못하면 null을 반환, 데이터를 찾았다면 Article 타입의 articleEntity로 저장
//        Food foodEntity = foodRepository.findById(id).orElse(null);
//
//        // 모델에 데이터 등록하기
//        // addAttribute() 메소드로 모델에 데이터 등록
//        // food이라는 이름으로 앞에서 가져온 foodEntity를 등록
//        // >> DB에서 가져온 데이터를 food이라는 이름으로 뷰 페이지에서 사용
//        model.addAttribute("food", foodEntity);
//
//        // 뷰 페이지 설정하기
//        return "articles/edit"; // edit은 mustache 파일, html 파일로 바꿔야 함
//    }
//
//    // URL 요청을 접수, 데이터 수정 요청이지만 post 방식으로 요청받았으므로 @PostMapping() 사용
//    // 괄호 안에는 edit.mustache에서 정의한 "/articles/update"를 작성
//    @PostMapping("/foods/update")
//    public String update(FoodForm form){ // 매개변수로 DTO 받아오기
//        log.info(form.toString());
//        // 1. DTO를 엔티티로 변환하기 >> 이미 DTO를 엔티티로 변환하는 toEntity() 메소드가 만들어짐
//        // form.toEntity() 메소드를 호출해 그 반환값을 Food 타입의 foodEntity라는 이름으로 받음
//        Food foodEntity = form.toEntity(); // DTO(form)를 엔티티(foodEntity)로 변환
//        log.info(foodEntity.toString()); // 엔티티로 잘 변환됐는지 로그 찍기
//
//        // 2. 엔티티를 DB에 저장하기
//
//        // 2-1 DB에서 기존 데이터 가져오기
//        // findById()는 리포지토리가 자동으로 제공하는 메소드이며 ()안에는 찾는 id값을 작성
//        // 여기서는 앞에서 가져온 foodEntity에 getId() 메소드를 호출해 id값을 집어넣음
//        // fidnById(foodEntity.getId()) 메소드를 호출해 반환받은 데이터를 Food 타입의 target 변수에 저장, 만약 데이터가 없다면 null 반환
//        Food target = foodRepository.findById(foodEntity.getFood_id()).orElse(null);
//
//        // 2-2. 기존 데이터의 값을 갱신하기
//        // 기존 데이터가 있다면 foodRepository의 save() 메소드를 호출해 foodEntity에 저장된 내용을 DB로 갱신
//        if (target != null){
//            foodRepository.save(foodEntity); // 엔티티를 DB에 저장(갱신)
//        }
//
//        // 3. 수정 결과 페이지로 리다이렉트하기
//        return "redirect:/foods/" + foodEntity.getFood_id();
//    }

    @GetMapping("/foods/edit/{food_id}")
    public String edit(@PathVariable Long food_id, Model model){
        Food foodEntity = foodRepository.findById(food_id).orElse(null);
        model.addAttribute("food", foodEntity);
        return "foods/edit";
    }

//    @PatchMapping("/foods/{food_id}")
//    public String update(@PathVariable Long food_id, FoodForm form, Model model) {
//        Food target = foodRepository.findById(food_id).orElse(null);
//
//        if (target != null){
//            target.update(form);
//            foodRepository.save(target);
//        }
//
//        model.addAttribute("food", target);
//        return "redirect:/foods/" + food_id;
//    }

    @PatchMapping("/foods/{food_id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long food_id, @RequestBody FoodForm form) {
        Food target = foodRepository.findById(food_id).orElse(null);
        Map<String, Object> response = new HashMap<>();

        if (target != null){
            target.update(form);
            Food saved = foodRepository.save(target);

            response.put("state", 200);
            response.put("food_id", saved.getFood_id());
            response.put("title", saved.getTitle());
            response.put("content", saved.getContent());
        } else {
            response.put("state", 400);
            response.put("message", "No food found with given id");
        }

        return ResponseEntity.ok(response);
    }



//    @GetMapping("/foods/{food_id}/delete") // URL 요청 접수, HTML은 Delete 메소드를 제공하지 않기 때문에 Get으로 대체
//    // id변수는 @GetMapping의 URL 주소에서 가져오기 때문에 매개변수 필요
//    // RedirectAttributes 객체를 사용하려면 delete() 메서드의 매개변수로 받아와야 하며 객체 이름은 rttr로 함
//    public String delete(@PathVariable Long id, RedirectAttributes rttr) {
//        log.info("삭제 요청이 들어왔습니다!!"); // delete() 메서드가 잘 동작하는지 확인하는 로그 추가
//
//        // 1. 삭제할 대상 가져오기
//        Food target = foodRepository.findById(id).orElse(null); // 리포지토리 이용하여 DB에 해당 id를 가진 데이터가 있는지 찾기
//        log.info(target.toString()); // target에 데이터가 있는지 없는지 확인하는 로그
//
//        // 2. 대상 엔티티 삭제하기
//        if(target != null){ // target이 null값인지 아닌지 확인
//            foodRepository.delete(target); // 매개변수로 삭제 대상 target을 넣으면 리포지토리가 DB에서 target을 삭제함
//            // 객체명.addFlashAttribute(넘겨주련는 키 문자열, 넘겨주려는 값 객체) >> 휘발성 데이터 등록
//            rttr.addFlashAttribute("msg", "삭제됐습니다!");
//        }
//        // 3. 결과 페이지로 리다이렉트하기
//        return "redirect:/foods"; // 목록 페이지로 돌아가야 함
//    }

//    @DeleteMapping("/foods/{food_id}")
//    public String delete(@PathVariable Long food_id, RedirectAttributes rttr) {
//        log.info("삭제 요청이 들어왔습니다!!");
//
//        Food target = foodRepository.findById(food_id).orElse(null);
//        log.info(target != null ? target.toString() : "해당 id를 가진 게시글이 없습니다.");
//
//        if(target != null){
//            foodRepository.delete(target);
//            rttr.addFlashAttribute("msg", "삭제됐습니다!");
//        }
//
//        return "redirect:/foods";
//    }
    @DeleteMapping("/foods/{food_id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long food_id){
        log.info("삭제 요청이 들어왔습니다!!");

        Food target = foodRepository.findById(food_id).orElse(null);
        log.info(target != null ? target.toString() : "해당 id를 가진 게시글이 없습니다.");

        Map<String, Object> response = new HashMap<>();
        if(target != null){
            foodRepository.delete(target);
            response.put("state", 200);
            response.put("message", "삭제됐습니다!");
        } else {
            response.put("state", 400);
            response.put("message", "No food found with given id");
        }

        return ResponseEntity.ok(response);
    }


}

