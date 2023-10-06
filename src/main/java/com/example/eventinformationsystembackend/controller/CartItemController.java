package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.CartItemDto;
import com.example.eventinformationsystembackend.dto.CartItemDtoResponse;
import com.example.eventinformationsystembackend.model.CartItem;
import com.example.eventinformationsystembackend.service.CartItemService;
import com.example.eventinformationsystembackend.service.OrderService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "/cart")
public class CartItemController {

    private final CartItemService cartItemService;
    private final OrderService orderService;

    @Autowired
    public CartItemController(CartItemService cartItemService, OrderService orderService) {
        this.cartItemService = cartItemService;
        this.orderService = orderService;
    }

    @GetMapping(path = "/coupon/{username}")
    public Double applyCoupon(@PathVariable("username") String username,
                              @RequestParam(value = "couponCode") String couponCode) {
        return orderService.getCartItemsTotalPriceWithCoupon(username, couponCode);
    }

    @GetMapping("/number/{username}")
    public Integer getAllCartItemsNumberForUser(
            @PathVariable("username") String username) {
        return cartItemService.getAllCartItemsNumberForUser(username);
    }

    @GetMapping("/{username}")
    public List<CartItemDtoResponse> getAllCartItemsForUser(
            @PathVariable("username") String username) {
        return cartItemService.getAllCartItemsForUser(username);
    }

    @PostMapping
    public CartItemDtoResponse addCartItem(@RequestBody @Valid CartItemDto cartItemDto) {
        return cartItemService.addCartItem(cartItemDto);
    }

    @PostMapping(path = "/decrease")
    public ResponseEntity<Void> decreaseCartItemTicketQuantity(@RequestBody @Valid CartItemDto cartItemDto) {
        cartItemService.decreaseCartItemTicketQuantity(cartItemDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> removeCartItem(@PathVariable("id") Long id) {
        cartItemService.removeCartItem(id);
        return ResponseEntity.noContent().build();
    }
}