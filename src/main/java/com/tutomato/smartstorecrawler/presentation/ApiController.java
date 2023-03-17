package com.tutomato.smartstorecrawler.presentation;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ApiController {

    /**
     * 상품 코드를 입력받아
     * 스마트스토어 등록 리스트 조회
     *
     */
    @GetMapping("/item/{itemCodes}")
    public ResponseEntity getItemListByItemCode(
            @PathVariable("itemCodes")String[] itemCodes){
        //01. item code
        //엑셀 데이터 내 code 컬럼 데이터 수집 -> String array parameter로 apiCall()호출
        String[] sampleCodes = {"110249", "110247", "110256", "110254"};

        for(String s : sampleCodes){
            String apiUrl = "https://openapi.naver.com/v1/search/shop.json?query="+s;
            String result = apiCallTest(apiUrl);

            System.out.println(result.toString());
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /*API 호출 메소드*/
    public String apiCallTest(
            String apiURL){

        String clientId = "DC2ZrI9OYy7e164mXC5N";
        String clientSecret = "q2vsrv1Y8R";

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
            System.out.println(response.toString());
            return  response.toString();
        } catch (Exception e) {
            System.out.println(e);
            return "";
        }

    }

    public Mono<String> webClientApiCall(String baseUrl, String code){
        Map<String, String> result = new HashMap<>();
        WebClient wc = getWebClientInstance(baseUrl);

        return wc.get()
                .uri("ttp://211.255.17.229:9090/kr/api/books?pageNum=1&pageSize=10")
                .retrieve()
                .bodyToMono(String.class);

    }

    private WebClient getWebClientInstance(String baseUrl){
        WebClient wc = WebClient.builder().baseUrl(baseUrl).build();
        return wc;
    }


}
