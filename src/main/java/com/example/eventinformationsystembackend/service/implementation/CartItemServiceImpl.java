package com.example.eventinformationsystembackend.service.implementation;

import com.example.eventinformationsystembackend.dto.CartItemDto;
import com.example.eventinformationsystembackend.dto.CartItemDtoResponse;
import com.example.eventinformationsystembackend.exception.CartItemTicketsExceedEventCapacity;
import com.example.eventinformationsystembackend.exception.NotEnoughSeatsException;
import com.example.eventinformationsystembackend.exception.ResourceNotFoundException;
import com.example.eventinformationsystembackend.model.CartItem;
import com.example.eventinformationsystembackend.model.Event;
import com.example.eventinformationsystembackend.model.User;
import com.example.eventinformationsystembackend.repository.CartItemRepository;
import com.example.eventinformationsystembackend.repository.UserRepository;
import com.example.eventinformationsystembackend.service.CartItemService;
import com.example.eventinformationsystembackend.service.DataValidationService;
import com.example.eventinformationsystembackend.service.EventService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static com.example.eventinformationsystembackend.common.ExceptionMessages.*;

@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {
    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final EventService eventService;
    private final DataValidationService dataValidationService;
    private final ModelMapper modelMapper = new ModelMapper();

    @Override
    public Integer getAllCartItemsNumberForUser(String username) {
        return getAllCartItemsForUser(username).size();
    }

    @Override
    public List<CartItem> getAllCartItemsForUser(User user) {
        if (userRepository.findUserByUsername(user.getUsername()).isEmpty()) {
            throw new ResourceNotFoundException(USER_DOES_NOT_EXIST);
        }

        return cartItemRepository.findAllByUser(user);
    }

    @Override
    public List<CartItemDtoResponse> getAllCartItemsForUser(String username) {
        User user = dataValidationService.getUserByUsername(username);

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

    @Override
    public void decreaseCartItemTicketQuantity(CartItemDto cartItemDto) {
        User user = dataValidationService.getUserByUsername(cartItemDto.getUsername());
        Event event = dataValidationService.getEventByName(cartItemDto.getEventName());

        List<CartItem> cartItems = cartItemRepository.findAllByUser(user);

        for (CartItem cartItem : cartItems) {
            if (cartItem.getEvent().getName().equals(event.getName())) {
                int cartItemTicketQuantity = cartItem.getTicketQuantity();
                cartItem.setTicketQuantity(cartItemTicketQuantity - 1);

                cartItemRepository.save(cartItem);
            }
        }
    }

    @Override
    public CartItemDtoResponse addCartItem(CartItemDto cartItemDto) {
        User user = dataValidationService.getUserByUsername(cartItemDto.getUsername());
        Event event = dataValidationService.getEventByName(cartItemDto.getEventName());

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

    private boolean checkIfCartItemTicketQuantityIsLessThanEventCapacity(CartItem cartItem,
                                                                        Event event, int ticketQuantity) {
        int eventCapacity = event.getCapacity();
        int cartItemTicketQuantity = cartItem.getTicketQuantity();

        return (cartItemTicketQuantity + ticketQuantity) <= eventCapacity;
    }

    @Override
    public void removeCartItem(Long id) {
        CartItem cartItem = dataValidationService.
                getResourceByIdOrThrowException(id, CartItem.class, CART_ITEM_DOES_NOT_EXIST);

        cartItemRepository.delete(cartItem);
    }

    private Double calculateCartItemTotalPrice(Event event, int ticketQuantity) {
        return ticketQuantity * event.getTicketPrice();
    }

    @Override
    public void removeAllCartItemsForUser(User user) {
        cartItemRepository.removeAllByUser(user);
    }
}
