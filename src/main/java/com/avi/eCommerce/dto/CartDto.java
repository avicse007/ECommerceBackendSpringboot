package com.avi.eCommerce.dto;

import com.avi.eCommerce.model.Cart;

import java.math.BigDecimal;
import java.util.List;

public class CartDto {
    private Long id;
    private BigDecimal totalAmount;
    private List<CartItemDto> cartItems;

    public CartDto() {
    }

    public CartDto(Long id, BigDecimal totalAmount, List<CartItemDto> cartItems) {
        this.id = id;
        this.totalAmount = totalAmount;
        this.cartItems = cartItems;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<CartItemDto> getCartItems() {
        return cartItems;
    }

    public void setCartItems(List<CartItemDto> cartItems) {
        this.cartItems = cartItems;
    }
}
