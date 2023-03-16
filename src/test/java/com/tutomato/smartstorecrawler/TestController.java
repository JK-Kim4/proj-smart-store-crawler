package com.tutomato.smartstorecrawler;

import com.tutomato.smartstorecrawler.presentation.ApiController;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class TestController {

    @Test
    public void api_call_test(){
        ApiController apiController = new ApiController();

        apiController.apiCallTest("test");
    }
}
