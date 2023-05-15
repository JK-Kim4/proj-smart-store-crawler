package com.tutomato.smartstorecrawler.presentation;

import com.google.gson.Gson;
import com.tutomato.smartstorecrawler.domain.Item;
import com.tutomato.smartstorecrawler.domain.ItemResponse;
import com.tutomato.smartstorecrawler.domain.TestVo;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/api")
public class ApiController {

    private final Logger logger = LoggerFactory.getLogger(ApiController.class);

    private final String baseRequestUrl = "https://openapi.naver.com/v1/search/shop.json?sort=asc&display=1&start=1&query=";

    @PostMapping("/doCrawling")
    @ResponseBody
    public List<Item> doCrawling(
            @RequestBody List<Item> items,
            HttpServletResponse response){
        logger.info("request body data size = {}", items.size());

        for(Item i : items){
            ItemResponse response1 = getLowerPrice(i.getProductNumber1());
            System.out.println("item response = " +response1.toString());
            System.out.println("item response item list = " +response1.getItems().toString());

            if(response1.getItems().size() > 0){
                i.setLowerPrice(response1.getItems().get(0).getLprice());
                i.setShopUrl(response1.getItems().get(0).getLink());
            }

        }
        return items;
    }

    @GetMapping("/get/excel/users")
    public void excel(HttpServletRequest req, HttpServletResponse res) {
        try {
            List<TestVo> listData = new ArrayList<TestVo>();
            TestVo testVo = new TestVo();

            testVo.setUserName("홍길동");
            testVo.setUserAge("20");
            testVo.setAddress("서울시");
            listData.add(testVo);

            testVo = new TestVo();
            testVo.setUserName("김길동");
            testVo.setUserAge("25");
            testVo.setAddress("부산시");
            listData.add(testVo);

            testVo = new TestVo();
            testVo.setUserName("강길동");
            testVo.setUserAge("23");
            testVo.setAddress("충북");
            listData.add(testVo);

            if(listData != null && listData.size() > 0) {
                final String fileName = "userList.xlsx";

                /* 엑셀 그리기 */
                final String[] colNames = {
                        "No", "성명", "나이", "거주지"
                };

                // 헤더 사이즈
                final int[] colWidths = {
                        3000, 5000, 5000, 3000
                };

                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet sheet = null;
                XSSFCell cell = null;
                XSSFRow row = null;

                //Font
                Font fontHeader = workbook.createFont();
                fontHeader.setFontName("맑은 고딕");	//글씨체
                fontHeader.setFontHeight((short)(9 * 20));	//사이즈

                Font font9 = workbook.createFont();
                font9.setFontName("맑은 고딕");	//글씨체
                font9.setFontHeight((short)(9 * 20));	//사이즈
                // 엑셀 헤더 셋팅
                CellStyle headerStyle = workbook.createCellStyle();
                headerStyle.setFont(fontHeader);
                // 엑셀 바디 셋팅
                CellStyle bodyStyle = workbook.createCellStyle();
                bodyStyle.setFont(font9);

                // 엑셀 왼쪽 설정
                CellStyle leftStyle = workbook.createCellStyle();
                leftStyle.setFont(font9);

                //rows
                int rowCnt = 0;
                int cellCnt = 0;
                int listCount = listData.size();

                // 엑셀 시트명 설정
                sheet = workbook.createSheet("사용자현황");
                row = sheet.createRow(rowCnt++);
                //헤더 정보 구성
                for (int i = 0; i < colNames.length; i++) {
                    cell = row.createCell(i);
                    cell.setCellStyle(headerStyle);
                    cell.setCellValue(colNames[i]);
                    sheet.setColumnWidth(i, colWidths[i]);	//column width 지정
                }
                //데이터 부분 생성
                for(TestVo vo : listData) {
                    cellCnt = 0;
                    row = sheet.createRow(rowCnt++);
                    // 넘버링
                    cell = row.createCell(cellCnt++);
                    cell.setCellStyle(bodyStyle);
                    cell.setCellValue(listCount--);
                    // 성명
                    cell = row.createCell(cellCnt++);
                    cell.setCellStyle(bodyStyle);
                    cell.setCellValue(vo.getUserName());
                    // 나이
                    cell = row.createCell(cellCnt++);
                    cell.setCellStyle(bodyStyle);
                    cell.setCellValue(vo.getUserAge());

                    // 주소
                    cell = row.createCell(cellCnt++);
                    cell.setCellStyle(bodyStyle);
                    cell.setCellValue(vo.getAddress());
                }
                res.setContentType("application/vnd.ms-excel");
                // 엑셀 파일명 설정
                res.setHeader("Content-Disposition", "attachment;filename=" + fileName);
                try {
                    workbook.write(res.getOutputStream());
                } catch(IOException e) {
                    e.printStackTrace();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }catch(Exception e) {
            e.printStackTrace();
        }
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

    private void downloadExcel(HttpServletResponse res, List<Item> items) {

        /*try {
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

                Item item = items.get(i);

                logger.info("excel download object " +i+" = " +item.toString());

                Cell cell = null;

                cell = row.createCell(0);
                cell.setCellValue(i+1);

                cell = row.createCell(1);
                cell.setCellValue(item.getProductNumber1());

                cell = row.createCell(2);
                cell.setCellValue(item.getProductNumber2());

                cell = row.createCell(3);
                cell.setCellValue(item.getName());

                cell = row.createCell(4);
                cell.setCellValue(item.getOrgPrice());

                cell = row.createCell(5);
                cell.setCellValue(item.getSalePrice());

                cell = row.createCell(6);
                cell.setCellValue(item.getLowerPrice());

                cell = row.createCell(7);
                cell.setCellValue(item.getShopUrl());


            }

            response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename="+ URLEncoder.encode(fileName, "UTF-8")+".xlsx");


            workbook.write(response.getOutputStream());
            workbook.close();

        }*/
        try {
            List<TestVo> listData = new ArrayList<TestVo>();
            TestVo testVo = new TestVo();

            testVo.setUserName("홍길동");
            testVo.setUserAge("20");
            testVo.setAddress("서울시");
            listData.add(testVo);

            testVo = new TestVo();
            testVo.setUserName("김길동");
            testVo.setUserAge("25");
            testVo.setAddress("부산시");
            listData.add(testVo);

            testVo = new TestVo();
            testVo.setUserName("강길동");
            testVo.setUserAge("23");
            testVo.setAddress("충북");
            listData.add(testVo);

            if(listData != null && listData.size() > 0) {
                final String fileName = "userList.xlsx";

                /* 엑셀 그리기 */
                final String[] colNames = {
                        "No", "성명", "나이", "거주지"
                };

                // 헤더 사이즈
                final int[] colWidths = {
                        3000, 5000, 5000, 3000
                };

                XSSFWorkbook workbook = new XSSFWorkbook();
                XSSFSheet sheet = null;
                XSSFCell cell = null;
                XSSFRow row = null;

                //Font
                Font fontHeader = workbook.createFont();
                fontHeader.setFontName("맑은 고딕");	//글씨체
                fontHeader.setFontHeight((short)(9 * 20));	//사이즈

                Font font9 = workbook.createFont();
                font9.setFontName("맑은 고딕");	//글씨체
                font9.setFontHeight((short)(9 * 20));	//사이즈
                // 엑셀 헤더 셋팅
                CellStyle headerStyle = workbook.createCellStyle();
                headerStyle.setFont(fontHeader);
                // 엑셀 바디 셋팅
                CellStyle bodyStyle = workbook.createCellStyle();
                bodyStyle.setFont(font9);

                // 엑셀 왼쪽 설정
                CellStyle leftStyle = workbook.createCellStyle();
                leftStyle.setFont(font9);

                //rows
                int rowCnt = 0;
                int cellCnt = 0;
                int listCount = listData.size();

                // 엑셀 시트명 설정
                sheet = workbook.createSheet("사용자현황");
                row = sheet.createRow(rowCnt++);
                //헤더 정보 구성
                for (int i = 0; i < colNames.length; i++) {
                    cell = row.createCell(i);
                    cell.setCellStyle(headerStyle);
                    cell.setCellValue(colNames[i]);
                    sheet.setColumnWidth(i, colWidths[i]);	//column width 지정
                }
                //데이터 부분 생성
                for(TestVo vo : listData) {
                    cellCnt = 0;
                    row = sheet.createRow(rowCnt++);
                    // 넘버링
                    cell = row.createCell(cellCnt++);
                    cell.setCellStyle(bodyStyle);
                    cell.setCellValue(listCount--);
                    // 성명
                    cell = row.createCell(cellCnt++);
                    cell.setCellStyle(bodyStyle);
                    cell.setCellValue(vo.getUserName());
                    // 나이
                    cell = row.createCell(cellCnt++);
                    cell.setCellStyle(bodyStyle);
                    cell.setCellValue(vo.getUserAge());

                    // 주소
                    cell = row.createCell(cellCnt++);
                    cell.setCellStyle(bodyStyle);
                    cell.setCellValue(vo.getAddress());
                }
                res.setContentType("application/vnd.ms-excel");
                // 엑셀 파일명 설정
                res.setHeader("Content-Disposition", "attachment;filename=" + fileName);
                try {
                    workbook.write(res.getOutputStream());
                } catch(IOException e) {
                    e.printStackTrace();
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
        catch (Exception e){
            logger.error("ERROR", e);
        }
    }

}
