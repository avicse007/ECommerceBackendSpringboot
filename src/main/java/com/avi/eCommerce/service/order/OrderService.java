package com.avi.eCommerce.service.order;

import com.avi.eCommerce.dto.OrderDto;
import com.avi.eCommerce.enums.OrderStatus;
import com.avi.eCommerce.exceptions.ResourceNotFoundException;
import com.avi.eCommerce.model.Cart;
import com.avi.eCommerce.model.Order;
import com.avi.eCommerce.model.OrderItem;
import com.avi.eCommerce.model.Product;
import com.avi.eCommerce.repository.OrderRepository;
import com.avi.eCommerce.repository.ProductRepository;
import com.avi.eCommerce.service.cart.CartService;
import com.avi.eCommerce.service.cart.ICartService;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class OrderService implements IOrderService{

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final ICartService cartService;
    private final ModelMapper modelMapper;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository, CartService cartService, ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
        this.modelMapper = modelMapper;
    }


    @Override
    public Order placeOrder(Long userId) {
        Cart cart = cartService.getCartByUserId(userId);
        Order order = createOrder(cart);
        List<OrderItem> orderItems = createOrderItems(order, cart);
        order.setOrderItems(Set.copyOf(orderItems));
        order.setTotalAmount(calculateTotalAmount(orderItems));
        Order savedOrder =  orderRepository.save(order);
        cartService.clearCartById(cart.getId());
        return savedOrder;
    }

    @Override
    public OrderDto getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                                .map(this::convertToDto)
                                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + orderId));
    }

    @Override
    public List<OrderDto> getOrdersByUserId(Long userId){
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                     .map(this::convertToDto)
                     .toList();
    }

    private Order createOrder(Cart cart){
        Order order = new Order();
        order.setUser(cart.getUser());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setOrderDate(LocalDate.now());
        return order;
    }

    private List<OrderItem> createOrderItems(Order order, Cart cart){
        return cart.getCartItems()
                   .stream()
                   .map(cartItem -> {
                       Product product = cartItem.getProduct();
                       product.setInventory(product.getInventory() - cartItem.getQuantity());
                       productRepository.save(product);

                       OrderItem orderItem = new OrderItem();
                       orderItem.setProduct(product);
                       orderItem.setQuantity(cartItem.getQuantity());
                       orderItem.setPrice(cartItem.getUnitPrice());
                       orderItem.setOrder(order);
                       return orderItem;
                   })
                   .toList();

    }
    private BigDecimal calculateTotalAmount(List<OrderItem> orderItems){
        return orderItems
                    .stream()
                    .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public OrderDto convertToDto(Order order){
        return modelMapper.map(order, OrderDto.class);
    }

}
