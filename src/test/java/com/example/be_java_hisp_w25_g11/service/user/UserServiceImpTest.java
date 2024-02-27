package com.example.be_java_hisp_w25_g11.service.user;

import com.example.be_java_hisp_w25_g11.dto.UserDTO;
import com.example.be_java_hisp_w25_g11.dto.response.FollowedDTO;
import com.example.be_java_hisp_w25_g11.dto.response.FollowerDTO;
import com.example.be_java_hisp_w25_g11.entity.Buyer;
import com.example.be_java_hisp_w25_g11.entity.Seller;
import com.example.be_java_hisp_w25_g11.exception.BadRequestException;
import com.example.be_java_hisp_w25_g11.repository.buyer.BuyerRepositoryImp;
import com.example.be_java_hisp_w25_g11.repository.seller.SellerRepositoryImp;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImpTest {
    @Mock
    private SellerRepositoryImp sellerRepository;
    @Mock
    private BuyerRepositoryImp buyerRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private UserServiceImp userService;

    @Test
    void follow() {
    }

    @Test
    void followersSellersCount() {
    }

    @Test
    void userFollowSellers() {
    }

    @Test
    void unfollow() {
    }

    @Test
    void sortFollowers() {
    }

    @Test
    void testSortFollowersValidOrder() {
        // Arrange
        Integer sellerId = 1;
        Seller seller = new Seller(sellerId, "seller");
        String orderAsc = "name_asc";
        String orderDesc = "name_desc";
        String noOrder = null;
        String otherOrder = "other";
        when(sellerRepository.get(sellerId)).thenReturn(Optional.of(seller));
        when(buyerRepository.get(sellerId)).thenReturn(Optional.empty());
        // Act & Assert
        Assertions.assertDoesNotThrow(() -> userService.sortFollowers(sellerId, orderAsc));
        Assertions.assertDoesNotThrow(() -> userService.sortFollowers(sellerId, orderDesc));
        Assertions.assertDoesNotThrow(() -> userService.sortFollowers(sellerId, noOrder));
    }

    @Test
    void testSortFollowersInvalidOrder() {
        // Arrange
        Integer sellerId = 1;
        Seller seller = new Seller(sellerId, "seller");
        String failOrderAsc = "asc";
        String failOrderDesc = "desc";
        String otherOrder = "empanada";
        when(sellerRepository.get(sellerId)).thenReturn(Optional.of(seller));
        when(buyerRepository.get(sellerId)).thenReturn(Optional.empty());
        // Act & Assert
        Assertions.assertThrows(BadRequestException.class, () -> userService.sortFollowers(sellerId, failOrderAsc));
        Assertions.assertThrows(BadRequestException.class, () -> userService.sortFollowers(sellerId, failOrderDesc));
        Assertions.assertThrows(BadRequestException.class, () -> userService.sortFollowers(sellerId, otherOrder));
    }

    @Test
    void sortFollowed() {
    }

    @Test
    void sortFollowedTestOrderExists() {
    }

    @Test
    void sortFollowedTestOrderNoExists() {
    }

    @Test
    void isSeller() {
    }
}