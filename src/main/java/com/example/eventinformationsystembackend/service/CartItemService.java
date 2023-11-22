package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.dto.CartItemDto;
import com.example.eventinformationsystembackend.dto.CartItemDtoResponse;
import com.example.eventinformationsystembackend.model.CartItem;
import com.example.eventinformationsystembackend.model.User;

import java.util.List;

public interface CartItemService {
    void removeAllCartItemsForUser(User user);

    void removeCartItem(Long id);

    void decreaseCartItemTicketQuantity(CartItemDto cartItemDto);

    Integer getAllCartItemsNumberForUser(String username);

    CartItemDtoResponse addCartItem(CartItemDto cartItemDto);

    List<CartItem> getAllCartItemsForUser(User user);

    List<CartItemDtoResponse> getAllCartItemsForUser(String username);
}
