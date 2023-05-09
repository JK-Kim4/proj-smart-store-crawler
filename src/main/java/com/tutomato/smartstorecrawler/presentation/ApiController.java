package com.tutomato.smartstorecrawler.presentation;

import com.google.gson.Gson;
import com.tutomato.smartstorecrawler.domain.Item;
import com.tutomato.smartstorecrawler.domain.ItemResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

@Controller
@RequestMapping("/api")
public class ApiController {

    private final Logger logger = LoggerFactory.getLogger(ApiController.class);

    private final String baseRequestUrl = "https://openapi.naver.com/v1/search/shop.json?sort=asc&display=1&start=1&query=";

    @PostMapping("/doCrawling")
    @ResponseBody
    public String doCrawling(
            @RequestBody List<Item> items,
            HttpServletResponse response){

        try {

            for(Item i : items){
                ItemResponse result = getLowerPrice(i.getProductNumber1());

                if(result.getItems() != null && result.getItems().size() > 0){
                    i.setLowerPrice(result.getItems().get(0).getLprice());
                    i.setShopUrl(result.getItems().get(0).getLink());
                }
                Thread.sleep(500);
            }

            downloadExcel(response,items);

        }catch (Exception e){
            logger.error("[ERROR occur]",e);
        }



        return items.toString();
    }

    private ItemResponse getLowerPrice(String productNumber){

        String clientId = "DC2ZrI9OYy7e164mXC5N";
        String clientSecret = "q2vsrv1Y8R";
        String apiURL = baseRequestUrl + productNumber;
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

    private void downloadExcel(HttpServletResponse response, List<Item> items) throws Exception{

        try {
            SXSSFWorkbook workbook = new SXSSFWorkbook(1000);
            Sheet sheet = workbook.createSheet("최저가_검색");

            final String fileName = "최저가_검색";

            final String[] headers = {"NO", "품번1", "품번2", "상품명", "원가", "판매가", "최저가", "주소"};
            Row row = sheet.createRow(0);

            //header
            for(int i = 0; i < headers.length; i++){
                Cell cell = row.createCell(i);
                cell.setCellValue(headers[i]);
            }

            //body
            for(int i = 0; i < items.size(); i++){
                row = sheet.createRow(i + 1);

                Cell cell = null;

                cell = row.createCell(0);
                cell.setCellValue(i+1);

                cell = row.createCell(1);
                cell.setCellValue(items.get(i).getProductNumber1());

                cell = row.createCell(2);
                cell.setCellValue(items.get(i).getProductNumber2());

                cell = row.createCell(3);
                cell.setCellValue(items.get(i).getName());


                cell = row.createCell(4);
                cell.setCellValue(items.get(i).getOrgPrice());

                cell = row.createCell(5);
                cell.setCellValue(items.get(i).getSalePrice());

                cell = row.createCell(6);
                cell.setCellValue(items.get(i).getLowerPrice());

                cell = row.createCell(7);
                cell.setCellValue(items.get(i).getShopUrl());


            }

            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition",
                    "attachment;filename="+ URLEncoder.encode(fileName, "UTF-8")+".xlsx");

            workbook.write(response.getOutputStream());
            workbook.close();

        }catch (Exception e){
            logger.error("ERROR", e);
        }
    }

}
