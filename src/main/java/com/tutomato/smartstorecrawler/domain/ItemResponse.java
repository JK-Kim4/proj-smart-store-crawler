package com.tutomato.smartstorecrawler.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class ItemResponse {

    private String lastBuildDate;
    private String total;
    private String start;
    private String display;
    private List<ResponseItems> items = new ArrayList<>();

}
