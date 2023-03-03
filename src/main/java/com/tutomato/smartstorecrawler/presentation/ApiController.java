package com.tutomato.smartstorecrawler.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    /**
     * 상품 코드를 입력받아
     * 스마트스토어 등록 리스트 조회
     *
     */
    @GetMapping("/item/{itemCode}")
    public ResponseEntity getItemListByItemCode(
            @PathVariable("itemCode")String itemCOde){

        String apiUrl = "https://openapi.naver.com/v1/search/shop.json";
        String parameter = "&query";
        String apiId = "";
        String apiSecret = "";

        return new ResponseEntity<>(HttpStatus.OK);
    }


}
