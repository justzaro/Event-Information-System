package com.example.eventinformationsystembackend.service;

import com.example.eventinformationsystembackend.dto.CartItemDto;
import com.example.eventinformationsystembackend.dto.CartItemDtoResponse;
import com.example.eventinformationsystembackend.dto.CartItemDtoResponseProjection;
import com.example.eventinformationsystembackend.exception.CartItemTicketsExceedEventCapacity;
import com.example.eventinformationsystembackend.exception.NotEnoughSeatsException;
import com.example.eventinformationsystembackend.exception.ResourceNotFoundException;
import com.example.eventinformationsystembackend.model.CartItem;
import com.example.eventinformationsystembackend.model.Event;
import com.example.eventinformationsystembackend.model.User;
import com.example.eventinformationsystembackend.repository.CartItemRepository;
import com.example.eventinformationsystembackend.repository.EventRepository;
import com.example.eventinformationsystembackend.repository.UserRepository;
import jakarta.validation.constraints.NotNull;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.example.eventinformationsystembackend.common.ExceptionMessages.*;

@Service
public class CartItemService {
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final EventService eventService;
    private final ModelMapper modelMapper;

    @Autowired
    public CartItemService(CartItemRepository cartItemRepository,
                           UserRepository userRepository,
                           EventRepository eventRepository,
                           EventService eventService) {
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
        this.eventService = eventService;
        this.modelMapper = new ModelMapper();
    }

    public Integer getAllCartItemsNumberForUser(String username) {
        return getAllCartItemsForUser(username).size();
    }

    public List<CartItem> getAllCartItemsForUser(User user) {
        if (userRepository.findUserByUsername(user.getUsername()).isEmpty()) {
            throw new ResourceNotFoundException(USER_DOES_NOT_EXIST);
        }

        return cartItemRepository.findAllByUser(user);
    }

    public List<CartItemDtoResponse> getAllCartItemsForUser(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));

        List<CartItem> cartItems = cartItemRepository.findAllByUser(user);

        return cartItems
                .stream()
                .map(cartItem -> {
                    CartItemDtoResponse cartItemDtoResponse = new CartItemDtoResponse();
                    cartItemDtoResponse.setEventPicturePath(cartItem.getEvent().getEventPicturePath());
                    cartItemDtoResponse.setEventName(cartItem.getEvent().getName());
                    cartItemDtoResponse.setEventLocation(cartItem.getEvent().getLocation());
                    cartItemDtoResponse.setTicketQuantity(cartItem.getTicketQuantity());
                    cartItemDtoResponse.setTicketPrice(cartItem.getEvent().getTicketPrice());
                    cartItemDtoResponse.setTotalPrice(cartItem.getTotalPrice());
                    cartItemDtoResponse.setId(cartItem.getId());
                    cartItemDtoResponse.setEventId(cartItem.getEvent().getId());
                    return cartItemDtoResponse;
                }).
                collect(Collectors.toList());
    }

/*    public List<CartItemDtoResponse> getAllCartItemsForUser(String username) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));

        List<CartItem> cartItems = cartItemRepository.findAllByUser(user);

        return cartItems
               .stream()
               .map(cartItem -> {
                    CartItemDtoResponse cartItemDtoResponse = new CartItemDtoResponse();
                    cartItemDtoResponse.setEventPicturePath(cartItem.getEvent().getEventPicturePath());
                    cartItemDtoResponse.setEventName(cartItem.getEvent().getName());
                    cartItemDtoResponse.setEventLocation(cartItem.getEvent().getLocation());
                    cartItemDtoResponse.setTicketQuantity(cartItem.getTicketQuantity());
                    cartItemDtoResponse.setTicketPrice(cartItem.getEvent().getTicketPrice());
                    cartItemDtoResponse.setTotalPrice(cartItem.getTotalPrice());
                    return cartItemDtoResponse;
               }).
               collect(Collectors.toList());*/
    //}

    public void decreaseCartItemTicketQuantity(CartItemDto cartItemDto) {
        User user = userRepository.findUserByUsername(cartItemDto.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));

        Event event = eventRepository.findEventByName(cartItemDto.getEventName())
                .orElseThrow(() -> new ResourceNotFoundException(EVENT_DOES_NOT_EXIST));

        List<CartItem> cartItems = cartItemRepository.findAllByUser(user);

        for (CartItem cartItem : cartItems) {
            if (cartItem.getEvent().getName().equals(event.getName())) {
                int cartItemTicketQuantity = cartItem.getTicketQuantity();
                cartItem.setTicketQuantity(cartItemTicketQuantity - 1);

                cartItemRepository.save(cartItem);
            }
        }
    }

    public CartItemDtoResponse addCartItem(CartItemDto cartItemDto) {
        User user = userRepository.findUserByUsername(cartItemDto.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException(USER_DOES_NOT_EXIST));

        Event event = eventRepository.findEventByName(cartItemDto.getEventName())
                .orElseThrow(() -> new ResourceNotFoundException(EVENT_DOES_NOT_EXIST));

        List<CartItem> cartItems = cartItemRepository.findAllByUser(user);

        if (!eventService.checkIfEventHasEnoughSeats(event, cartItemDto.getTicketQuantity())) {
            throw new NotEnoughSeatsException(NOT_ENOUGH_SEATS);
        }

        for (CartItem cartItem : cartItems) {
            if (cartItem.getEvent().getName().equals(event.getName())) {

                if (!checkIfCartItemTicketQuantityIsLessThanEventCapacity(
                        cartItem, event, cartItemDto.getTicketQuantity())) {
                    throw new CartItemTicketsExceedEventCapacity(CART_ITEM_TICKETS_EXCEED_EVENT_CAPACITY);
                }

                int matchingCartItemTicketQuantity = cartItem.getTicketQuantity();
                double matchingCartItemTotalPrice = cartItem.getTotalPrice();

                double cartItemToAddTotalPrice = event.getTicketPrice() * cartItemDto.getTicketQuantity();

                CartItem cartItemToUpdate = modelMapper.map(cartItem, CartItem.class);

                cartItemToUpdate.setTicketQuantity(matchingCartItemTicketQuantity + cartItemDto.getTicketQuantity());
                cartItemToUpdate.setTotalPrice(matchingCartItemTotalPrice + cartItemToAddTotalPrice);

                cartItemRepository.save(cartItemToUpdate);

                return modelMapper.map(cartItemToUpdate, CartItemDtoResponse.class);
            }
        }

        CartItem cartItem = new CartItem();

        cartItem.setEvent(event);
        cartItem.setUser(user);
        cartItem.setTicketQuantity(cartItemDto.getTicketQuantity());
        cartItem.setTotalPrice(calculateCartItemTotalPrice(event, cartItemDto.getTicketQuantity()));

        cartItemRepository.save(cartItem);

        CartItemDtoResponse cartItemDtoResponse = modelMapper.map(cartItem, CartItemDtoResponse.class);

        cartItemDtoResponse.setEventName(event.getName());
        cartItemDtoResponse.setEventPicturePath(event.getEventPicturePath());
        cartItemDtoResponse.setTicketPrice(event.getTicketPrice());

        return cartItemDtoResponse;
    }

    public boolean checkIfCartItemTicketQuantityIsLessThanEventCapacity(CartItem cartItem,
                                                                        Event event, int ticketQuantity) {
        int eventCapacity = event.getCapacity();
        int cartItemTicketQuantity = cartItem.getTicketQuantity();

        return (cartItemTicketQuantity + ticketQuantity) <= eventCapacity;
    }

    public void removeCartItem(Long id) {
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(CART_ITEM_DOES_NOT_EXIST));

        cartItemRepository.delete(cartItem);
    }

    private Double calculateCartItemTotalPrice(Event event, int ticketQuantity) {
        return ticketQuantity * event.getTicketPrice();
    }

    public void removeAllCartItemsForUser(User user) {
        cartItemRepository.removeAllByUser(user);
    }
}
