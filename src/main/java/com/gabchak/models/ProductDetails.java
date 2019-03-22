package com.gabchak.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
public class ProductDetails {

    private String color;
    private String price;
    private Set<String> sizeSet;
}
