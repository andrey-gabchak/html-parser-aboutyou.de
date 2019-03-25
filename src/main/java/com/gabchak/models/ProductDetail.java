package com.gabchak.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.nodes.Document;

import java.util.Set;

@Getter
@Setter
@EqualsAndHashCode(of = "url")
public class ProductDetail {

    private String url;
    private Document htmlDocument;
    private String color;
    private String price;
    private String articleId;
    private Set<String> sizeSet;
}
