package com.avi.eCommerce.service.cart;

import com.avi.eCommerce.model.Cart;
import com.avi.eCommerce.model.CartItem;
import com.avi.eCommerce.model.Product;
import com.avi.eCommerce.repository.CartItemRepository;
import com.avi.eCommerce.repository.CartRepository;
import com.avi.eCommerce.service.product.IProductService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CartItemService implements ICartItemService {

    private final CartItemRepository cartItemRepository;
    private final ICartService cartService;
    private final IProductService productService;
    private final CartRepository cartRepository;

    public CartItemService(CartItemRepository cartItemRepository, ICartService cartService, IProductService productService, CartRepository cartRepository) {
        this.cartItemRepository = cartItemRepository;
        this.cartService = cartService;
        this.productService = productService;
        this.cartRepository = cartRepository;
    }

    @Override
    public void addItemToCart(Long cartId, Long productId, int quantity) {
        //1. Get the cart
        Cart cart = cartService.getCartById(cartId);
        //2. Get the product
        Product product = productService.getProductById(productId);
        //3. Check if the product already int the cart
        CartItem cartItem = null;
        if(cart.getCartItems() != null){
            cartItem = cart.getCartItems()
                               .stream()
                               .filter(item -> item.getProduct().getId().equals(product.getId()))
                               .findFirst()
                               .orElse(new CartItem());
        }else {
            cartItem = new CartItem();
        }
        if(cartItem.getId()==null){
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setCart(cart);
            cartItem.setUnitPrice(product.getPrice());

        }else {
            cartItem.setQuantity(cartItem.getQuantity()+quantity);
        }
        cartItem.setTotalPrice();
        cart.addCartItem(cartItem);
        cartItemRepository.save(cartItem);
        cartRepository.save(cart);

    }

    @Override
    public void removeItemFromCart(Long cartId, Long productId) {
        Cart cart = cartService.getCartById(cartId);
        CartItem cartItem = getCartItem(cartId, productId);
        cart.removeCartItem(cartItem);
        cartRepository.save(cart);
    }

    @Override
    public void updateItemQuantity(Long cartId, Long productId, int quantity) {
        Cart cart = cartService.getCartById(cartId);
        cart.getCartItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(item -> {
                    item.setQuantity(quantity);
                    item.setUnitPrice(item.getProduct().getPrice());
                    item.setTotalPrice();
                });
        BigDecimal totalAmount = cart.getTotalAmount();
        cart.setTotalAmount(totalAmount);
        cartRepository.save(cart);
    }

    @Override
    public CartItem getCartItem(Long cartId, Long productId) {
        Cart cart = cartService.getCartById(cartId);
        return cart.getCartItems()
                .stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElseThrow(()-> new RuntimeException("Item not found in the cart"));
    }
}
