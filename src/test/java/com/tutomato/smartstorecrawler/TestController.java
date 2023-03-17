package com.tutomato.smartstorecrawler;

import com.tutomato.smartstorecrawler.presentation.ApiController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

@SpringBootTest
public class TestController {

    @Test
    public void api_call_test(){
        ApiController apiController = new ApiController();

        apiController.apiCallTest("test");
    }

    @Test
    public void web_client_get_test(){
        ApiController apiController = new ApiController();

        Mono<String> result = apiController.webClientApiCall("https://openapi.naver.com/v1/search/shop.json", "110256");

        System.out.println(result.block());

    }
}
