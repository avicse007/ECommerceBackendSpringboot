package com.avi.eCommerce.controller;

import com.avi.eCommerce.dto.UserDto;
import com.avi.eCommerce.exceptions.ResourceNotFoundException;
import com.avi.eCommerce.model.Cart;
import com.avi.eCommerce.model.User;
import com.avi.eCommerce.response.ApiResponse;
import com.avi.eCommerce.service.cart.CartService;
import com.avi.eCommerce.service.cart.ICartItemService;
import com.avi.eCommerce.service.cart.ICartService;
import com.avi.eCommerce.service.user.IUserService;
import io.jsonwebtoken.JwtException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("${api.prefix}/cart-items")
public class CartItemController  {
    private final ICartItemService cartItemService;;
    private final ICartService cartService;
    private final IUserService userService;

    public CartItemController(ICartItemService cartItemService, CartService cartService, IUserService userService) {
        this.cartItemService = cartItemService;
        this.cartService = cartService;
        this.userService = userService;
    }

    @PostMapping("/item/add")
    public ResponseEntity<ApiResponse> addItemToCart(
            @RequestParam Long productId,
            @RequestParam int quantity) {
        try {
            User user = userService.getAuthenticatedUser();
            Cart cart = cartService.initilizeNewCart(user);

            cartItemService.addItemToCart(cart.getId(), productId, quantity);
            return ResponseEntity.ok().body(new ApiResponse("Item added to cart successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Failed to add item to cart", e.getMessage()));
        }catch(JwtException e){
            return ResponseEntity.status(UNAUTHORIZED).body(new ApiResponse("Failed to add item to cart", e.getMessage()));
        }
    }

    @DeleteMapping("/item/remove/{cartId}/{itemId}")
    public ResponseEntity<ApiResponse> removeItemFromCart(
            @PathVariable Long cartId,
            @PathVariable Long itemId) {
        try {
            cartItemService.removeItemFromCart(cartId, itemId);
            return ResponseEntity.ok().body(new ApiResponse("Item removed from cart successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Failed to remove item from cart", e.getMessage()));
        }
    }

    @PutMapping("/item/update/{cartId}/{itemId}")
    public ResponseEntity<ApiResponse> updateItemQuantity(
            @PathVariable Long cartId,
            @PathVariable Long itemId,
            @RequestParam int quantity) {
        try {
            cartItemService.updateItemQuantity(cartId, itemId, quantity);
            return ResponseEntity.ok().body(new ApiResponse("Item quantity updated successfully", null));
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse("Failed to update item quantity", e.getMessage()));
        }
    }

}
