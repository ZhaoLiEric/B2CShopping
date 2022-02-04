package com.leyou.cart.dto;

import lombok.Data;

@Data
public class CartDTO {
    private String title;
    private String image;
    private Long price;
    private Integer num;
    private Long skuId;
    private String ownSpec;
}
