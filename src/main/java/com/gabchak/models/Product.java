package com.gabchak.models;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class Product {

    private String name;
    private String brand;

    private Set<ProductDetail> productDetails;

    public void addProductDetails(ProductDetail productDetail) {
        if (this.productDetails == null) {
            this.productDetails = new HashSet<>();
        }
        this.productDetails.add(productDetail);
    }
}
