package com.avi.eCommerce.controller;

import com.avi.eCommerce.dto.CartDto;
import com.avi.eCommerce.exceptions.ResourceNotFoundException;
import com.avi.eCommerce.model.Cart;
import com.avi.eCommerce.response.ApiResponse;
import com.avi.eCommerce.service.cart.ICartService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("${api.prefix}/carts")
public class CartController {
    private final ICartService cartService;


    public CartController(ICartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<ApiResponse> getCart(@PathVariable Long cartId){
        try {
            Cart cart = cartService.getCartById(cartId);
            CartDto cartDto = cartService.convertToDto(cart);
        return ResponseEntity.ok().body(new ApiResponse("Cart fetched successfully", cartDto));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                           .body(new ApiResponse("Failed to fetch cart", e.getMessage()));
        }
    }

    @DeleteMapping("/{cartId}/clear")
    public ResponseEntity<ApiResponse> clearCart(@PathVariable Long cartId){
        try {
            try{
            cartService.clearCartById(cartId);
            }catch (Exception e){
                System.out.println("=========>>>>Exception in deletion of cartBy Id");
                e.printStackTrace();
            }
            return ResponseEntity.ok().body(new ApiResponse("Cart cleared successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                           .body(new ApiResponse("Failed to clear cart", e.getMessage()));
        }
    }

    @GetMapping("/{cartId}/cart/total")
    public ResponseEntity<ApiResponse> getTotalAmount(@PathVariable Long cartId){
        try {
            BigDecimal totalAmount = cartService.getTotalPrice(cartId);
            return ResponseEntity.ok().body(new ApiResponse("Total amount fetched successfully", totalAmount));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND)
                           .body(new ApiResponse("Failed to fetch total amount", e.getMessage()));
        }
    }



}
