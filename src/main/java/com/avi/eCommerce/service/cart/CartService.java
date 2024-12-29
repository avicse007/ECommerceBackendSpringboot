package com.avi.eCommerce.service.cart;

import com.avi.eCommerce.dto.*;
import com.avi.eCommerce.exceptions.ResourceNotFoundException;
import com.avi.eCommerce.model.Cart;
import com.avi.eCommerce.model.User;
import com.avi.eCommerce.repository.CartItemRepository;
import com.avi.eCommerce.repository.CartRepository;
import com.avi.eCommerce.repository.UserRepository;
import com.avi.eCommerce.service.image.ImageService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class CartService implements ICartService{
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final AtomicLong cartIdGenerator = new AtomicLong(0);
    private final ModelMapper modelMapper;
    private final EntityManager entityManager;
    private final UserRepository userRepository;
    public CartService(CartRepository cartRepository, CartItemRepository cartItemRepository, ImageService imageService, ModelMapper modelMapper, EntityManager entityManager, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.modelMapper = modelMapper;
        this.entityManager = entityManager;
        this.userRepository = userRepository;
    }

    @Override
    public Cart getCartById(Long id) {
        Cart cart = cartRepository.findById(id)
                       .orElseThrow( ()-> new ResourceNotFoundException("Cart not found"+id));
        cart.setTotalAmount(cart.getTotalAmount());
        return cartRepository.save(cart);
    }

    @Transactional
    @Override
    public void clearCartById(Long id) {
        Cart cart = cartRepository.findById(id)
                       .orElseThrow( ()-> new ResourceNotFoundException("Cart not found"+id));
        // Delete the cart
        try{
            User user = cart.getUser();
            if (user != null) {
                System.out.println("User before nullifying cart: " + user);
                user.setCart(null); // Nullify the cart reference in the user
                entityManager.flush();
                System.out.println("User after nullifying cart: " + user);
                userRepository.save(user);
                entityManager.flush();
            }
        cartRepository.delete(cart);
        cartRepository.flush();
        }catch (Exception e) {
            System.out.println("=========>>>>Exception in deletion of cartBy Id");
            e.printStackTrace();
        }
        //cartRepository.delete(cart);
        //cartItemRepository.deleteAllByCartId(id);
        //cart.getCartItems().clear();
        //cartRepository.flush();
    }

    @Override
    public BigDecimal getTotalPrice(Long id) {
        Cart cart = getCartById(id);
        return cart.getTotalAmount();
    }

    @Override
    public Cart initilizeNewCart(User user){
        Cart cart = null;
        try{
            cart = getCartByUserId(user.getId());
            return cart;
        } catch (Exception e) {
            System.out.println("Ni cart is associated with user id: "+user.getId());
        }
        cart = new Cart();
        cart.setUser(user);
        //cart.setId(cartIdGenerator.get());
        return cartRepository.save(cart);
    }

    @Override
    public CartDto convertToDto(Cart cart) {
        CartDto cartDto = new CartDto();
        cartDto.setId(cart.getId());
        cartDto.setTotalAmount(cart.getTotalAmount());

        List<CartItemDto> cartItems = cart.getCartItems().stream()
                                              .map(item -> {
                                                  CartItemDto itemDto = new CartItemDto();
                                                  itemDto.setId(item.getId());

                                                  ProductDto productDto = new ProductDto();
                                                  productDto.setId(item.getProduct().getId());
                                                  productDto.setName(item.getProduct().getName());
                                                  List<ImageDto> imageDtos = item.getProduct().getImages().stream().map(image -> modelMapper.map(image,ImageDto.class)).toList();
                                                  productDto.setImages(imageDtos);
                                                  itemDto.setProduct(productDto);
                                                  itemDto.setQuantity(item.getQuantity());
                                                  return itemDto;
                                              })
                                              .collect(Collectors.toList());

        cartDto.setCartItems(cartItems);
        return cartDto;
    }

    @Override
    public Cart getCartByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                       .orElseThrow(()-> new ResourceNotFoundException("Cart not found for user id: "+userId));
    }
}
