package com.gabchak.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class Product {

    private String name;
    private String brand;
    private String url;

    private Set<ProductDetails> productDetails;
}
