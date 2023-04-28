package com.tutomato.smartstorecrawler.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 크롤링 결과 Entity
 * */
@Getter
@Setter
@NoArgsConstructor
public class Item {

    private String name;

    private Integer productNumber;

    private Integer orgPrice;

    private Integer salePrice;

    private Integer lowerPrice;

    private String shopUrl;

}
