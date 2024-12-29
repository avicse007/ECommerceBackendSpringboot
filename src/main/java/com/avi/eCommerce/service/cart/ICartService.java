package com.avi.eCommerce.service.cart;

import com.avi.eCommerce.dto.CartDto;
import com.avi.eCommerce.dto.UserDto;
import com.avi.eCommerce.model.Cart;
import com.avi.eCommerce.model.User;

import java.math.BigDecimal;

public interface ICartService {
    Cart getCartById(Long id);
    void clearCartById(Long id);
    BigDecimal getTotalPrice(Long id);

    Cart initilizeNewCart(User user);

    CartDto convertToDto(Cart cart);

    Cart getCartByUserId(Long userId);
}
