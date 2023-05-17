package com.tutomato.smartstorecrawler.presentation;

import com.google.gson.Gson;
import com.tutomato.smartstorecrawler.domain.CommonValue;
import com.tutomato.smartstorecrawler.domain.Item;
import com.tutomato.smartstorecrawler.domain.ItemResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Controller
@RequestMapping("/api")
public class ApiController {

    @Value("${naver.api.clientId}")
    private String clientId;

    @Value("${naver.api.clientSecret}")
    private String clientSecret;

    private final Logger logger = LoggerFactory.getLogger(ApiController.class);

    @PostMapping("/doCrawling")
    @ResponseBody
    public List<Item> doCrawling(
            @RequestBody List<Item> items){
        logger.info("request body data size = {}", items.size());

        for(Item i : items){
            ItemResponse response1 = getLowerPrice(i.getProductNumber1());
            if(response1.getItems().size() > 0){
                i.setLowerPrice(response1.getItems().get(0).getLprice());
                i.setShopUrl(response1.getItems().get(0).getLink());
            }

        }
        return items;
    }

    private ItemResponse getLowerPrice(String productNumber){

        String apiURL = CommonValue.API_BASE_URL + productNumber;
        ItemResponse result = new ItemResponse();

        try {
            URL url = new URL(apiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("X-Naver-Client-Id", clientId);
            con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
            con.setDoOutput(true);
            int responseCode = con.getResponseCode();
            BufferedReader br;
            if(responseCode==200) { // 정상 호출
                br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            } else {  // 에러 발생
                br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
            }
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = br.readLine()) != null) {
                response.append(inputLine);
            }
            br.close();

            /*JSON parsing*/
            Gson gson = new Gson();
            result = gson.fromJson(response.toString(), ItemResponse.class);

            return  result;
        } catch (Exception e) {
            System.out.println(e);
            return result;
        }

    }

}
