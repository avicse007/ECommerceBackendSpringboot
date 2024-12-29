package com.avi.eCommerce.dto;

import java.util.List;

public class UserDto {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private List<OrderDto> orders;
    private CartDto cartDto;

    public UserDto() {
    }

    public UserDto(Long id, String firstName, String lastName, String email, List<OrderDto> order, CartDto cartDto) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.orders = order;
        this.cartDto = cartDto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<OrderDto> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderDto> orders) {
        this.orders = orders;
    }

    public CartDto getCartDto() {
        return cartDto;
    }

    public void setCartDto(CartDto cartDto) {
        this.cartDto = cartDto;
    }
}
