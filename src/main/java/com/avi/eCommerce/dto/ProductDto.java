package com.avi.eCommerce.dto;

import com.avi.eCommerce.model.Category;

import java.math.BigDecimal;
import java.util.List;

public class ProductDto {
    private Long id;
    private String name;
    private String brand;
    private BigDecimal price;
    private int inventory;
    private String productDescription;
    private Category category;

    public ProductDto(String name, String brand, BigDecimal price, int inventory, String productDescription, Category category, List<ImageDto> images) {
        this.name = name;
        this.brand = brand;
        this.price = price;
        this.inventory = inventory;
        this.productDescription = productDescription;
        this.category = category;
        this.images = images;
    }

    public ProductDto() {
    }

    public List<ImageDto> getImages() {
        return images;
    }

    public void setImages(List<ImageDto> images) {
        this.images = images;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public int getInventory() {
        return inventory;
    }

    public void setInventory(int inventory) {
        this.inventory = inventory;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    private List<ImageDto> images;
}
