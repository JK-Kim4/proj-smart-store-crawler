package com.tutomato.smartstorecrawler;

import com.tutomato.smartstorecrawler.presentation.ApiController;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;

import java.util.HashMap;

@SpringBootTest
public class TestController {

    @Test
    public void api_call_test() throws ParseException {
        ApiController apiController = new ApiController();

        String result = apiController.apiCallTest("https://openapi.naver.com/v1/search/shop.json?query=110256");

        System.out.println(result);

        JSONParser jsonparser = new JSONParser();

        HashMap<String, String>  obj = (HashMap<String, String>) jsonparser.parse(result);

    }

    @Test
    public void web_client_get_test(){
        ApiController apiController = new ApiController();

        Mono<String> result = apiController.webClientApiCall("https://openapi.naver.com/v1/search/shop.json", "110256");

        System.out.println(result.block());

    }
}
