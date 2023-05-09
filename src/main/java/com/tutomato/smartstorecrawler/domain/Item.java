package com.tutomato.smartstorecrawler.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 크롤링 결과 Entity
 * */
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Item {

    private String name;

    private String productNumber1;

    private String productNumber2;

    private Integer orgPrice;

    private Integer salePrice;

    private String lowerPrice;

    private String shopUrl;

}
