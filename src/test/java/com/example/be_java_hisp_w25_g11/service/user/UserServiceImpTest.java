package com.example.be_java_hisp_w25_g11.service.user;

import com.example.be_java_hisp_w25_g11.dto.UserDTO;
import com.example.be_java_hisp_w25_g11.dto.response.FollowerDTO;
import com.example.be_java_hisp_w25_g11.dto.response.SuccessDTO;
import com.example.be_java_hisp_w25_g11.entity.Buyer;
import com.example.be_java_hisp_w25_g11.entity.Seller;
import com.example.be_java_hisp_w25_g11.exception.BadRequestException;
import com.example.be_java_hisp_w25_g11.exception.NotFoundException;
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
import static org.junit.jupiter.api.Assertions.*;

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
    void BuyerfollowTestOk() {
        //Arrange
        Buyer buyer = new Buyer(5,"pepitoTest");
        Seller seller = new Seller(6,"sellerTest");
        Set<Integer> expectedfollowers = new HashSet<>(Set.of(5));
        Set<Integer> expectedfollowed = new HashSet<>(Set.of(6));
        when(buyerRepository.addFollowed(buyer,seller.getId())).thenReturn(true);
        when(sellerRepository.addFollower(seller,buyer.getId())).thenReturn(true);
        when(sellerRepository.get(seller.getId())).thenReturn(Optional.of(seller));
        when(buyerRepository.get(buyer.getId())).thenReturn(Optional.of(buyer));
        when(buyerRepository.existing(buyer.getId())).thenReturn(true);
        when(sellerRepository.existing(seller.getId())).thenReturn(true);

        //Act
        SuccessDTO result = userService.follow(buyer.getId(),seller.getId());

        //Assert
        assertEquals("El usuario con id=5 ahora sigue al vendedor con id=6.",result.getMessage());
    }
    @Test
    void SellerfollowTestOk() {
        //Arrange
        Seller seller = new Seller(2,"pepitoTest");
        Seller sellerToFollow = new Seller(6,"sellerTest");
        Set<Integer> expectedfollowers = new HashSet<>(Set.of(5));
        Set<Integer> expectedfollowed = new HashSet<>(Set.of(6));
        when(sellerRepository.addFollowed(seller,sellerToFollow.getId())).thenReturn(true);
        when(sellerRepository.addFollower(sellerToFollow,seller.getId())).thenReturn(true);
        when(sellerRepository.get(seller.getId())).thenReturn(Optional.of(seller));
        when(sellerRepository.get(sellerToFollow.getId())).thenReturn(Optional.of(sellerToFollow));
        when(sellerRepository.existing(seller.getId())).thenReturn(true);
        when(sellerRepository.existing(sellerToFollow.getId())).thenReturn(true);
        //Act
        SuccessDTO result = userService.follow(seller.getId(),sellerToFollow.getId());
        //Assert
        assertEquals("El usuario con id=2 ahora sigue al vendedor con id=6.",result.getMessage());
    }
    @Test
    void followTestNotFound() {
        //Arrange
        Buyer buyer = new Buyer(5,"pepitoTest");
        Seller seller = new Seller(6,"sellerTest");

        //Act && Assert
        assertThrows(NotFoundException.class,()-> userService.follow(buyer.getId(),seller.getId()));
    }

    @Test
    void followTestBadRequest() {
        //Arrange
        Buyer buyer = new Buyer(5,"pepitoTest");
        Seller seller = new Seller(6,"sellerTest");
        when(buyerRepository.get(buyer.getId())).thenReturn(Optional.of(buyer));
        when(buyerRepository.existing(buyer.getId())).thenReturn(true);
        //Act && Assert
        assertThrows(BadRequestException.class,()-> userService.follow(buyer.getId(), buyer.getId()));
    }



    @Test
    void followersSellersCount() {}

    @Test
    void userFollowSellers() {}

    //T-0002: Verificar que el usuario a dejar de seguir, exista (de seller a seller).
    // Resultado: Permite continuar con normalidad
    @Test
    void testSellerUnfollowTrue() {
        //Arrange
        Integer userId = 1;
        Integer sellerIdToUnfollow = 6;
        Seller fakeSeller = new Seller(userId, "Carolina", Set.of(2, 3), Set.of(sellerIdToUnfollow), Set.of());
        Seller fakeSellerToUnfollow = new Seller(sellerIdToUnfollow, "Joaquín", Set.of(userId, 2, 4, 5),Set.of(4, 5),Set.of());

        when(sellerRepository.get(userId)).thenReturn(Optional.of(fakeSeller));
        when(sellerRepository.get(sellerIdToUnfollow)).thenReturn(Optional.of(fakeSellerToUnfollow));
        when(sellerRepository.existing(userId)).thenReturn(true);
        when(sellerRepository.existing(sellerIdToUnfollow)).thenReturn(true);

        //Act
        SuccessDTO result = userService.unfollow(userId, sellerIdToUnfollow);

        //Assert

        assertEquals(result.getMessage(), "El usuario con id=1 ha dejado de seguir al vendedor con id=6.");
    }

    //T-0002: Notifica la no existencia con un excepción.
    // Resultado: Permite continuar con normalidad
    @Test
    void unfollowTestNotFound() {
        //Arrange
        Buyer buyer = new Buyer(5,"pepitoTest");
        Seller seller = new Seller(6,"sellerTest");

        //Act && Assert
        assertThrows(NotFoundException.class,()-> userService.unfollow(buyer.getId(),seller.getId()));
    }

    @Test
    void unfollowTestBadRequest() {
        //Arrange
        Buyer buyer = new Buyer(5, "pepitoTest");
        Seller seller = new Seller(6, "sellerTest");

        when(buyerRepository.get(buyer.getId())).thenReturn(Optional.of(buyer));
        when(buyerRepository.existing(buyer.getId())).thenReturn(true);

        //Act && Assert
        assertThrows(BadRequestException.class, () -> userService.unfollow(buyer.getId(), buyer.getId()));
    }

    //T-0002: Verificar que el usuario a dejar de seguir, exista (de buyer a seller).
    // Resultado: Permite continuar con normalidad
    @Test
    void testBuyerUnfollowTrue() {
        //Arrange
        Integer userId = 2;
        Integer sellerIdToUnfollow = 5;
        Buyer fakeBuyer = new Buyer(userId, "Martin", Set.of(sellerIdToUnfollow));
        Seller fakeSellerToUnfollow = new Seller(sellerIdToUnfollow, "Joaquín", Set.of(userId, 4, 5),Set.of(4, 5),Set.of());

        when(buyerRepository.get(userId)).thenReturn(Optional.of(fakeBuyer));
        when(sellerRepository.get(sellerIdToUnfollow)).thenReturn(Optional.of(fakeSellerToUnfollow));
        when(buyerRepository.existing(userId)).thenReturn(true);
        when(sellerRepository.existing(sellerIdToUnfollow)).thenReturn(true);

        //Act
        SuccessDTO result = userService.unfollow(userId, sellerIdToUnfollow);

        //Assert
        assertEquals(result.getMessage(), "El usuario con id=2 ha dejado de seguir al vendedor con id=5.");

    }

    // T-0003: Verificar que el tipo de ordenamiento alfabético exista para los seguidores
    // Resultado: Permite continuar con normalidad.
    @Test
    void testSortFollowersValidOrder() {
        // Arrange
        Integer sellerId = 1;
        Seller seller = new Seller(sellerId, "seller");
        String orderAsc = "name_asc";
        String orderDesc = "name_desc";
        String noOrder = null;
        when(sellerRepository.get(sellerId)).thenReturn(Optional.of(seller));
        when(buyerRepository.get(sellerId)).thenReturn(Optional.empty());
        // Act & Assert
        Assertions.assertDoesNotThrow(() -> userService.sortFollowers(sellerId, orderAsc));
        Assertions.assertDoesNotThrow(() -> userService.sortFollowers(sellerId, orderDesc));
        Assertions.assertDoesNotThrow(() -> userService.sortFollowers(sellerId, noOrder));
    }

    // T-0003: Verificar que el tipo de ordenamiento alfabético no exista para los seguidores
    // Resultado: Notifica la no existencia mediante una excepción.
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

    // T-0003: Verificar que el tipo de ordenamiento alfabético exista para los seguidos
    // Resultado: Permite continuar con normalidad.
    @Test
    void testSortFollowedValidOrder() {
        // Arrange
        Integer buyerId = 1;
        Buyer buyer = new Buyer(buyerId, "buyer");
        String orderAsc = "name_asc";
        String orderDesc = "name_desc";
        String noOrder = null;
        when(buyerRepository.get(buyerId)).thenReturn(Optional.of(buyer));
        when(sellerRepository.get(buyerId)).thenReturn(Optional.empty());
        // Act & Assert
        Assertions.assertDoesNotThrow(() -> userService.sortFollowed(buyerId, orderAsc));
        Assertions.assertDoesNotThrow(() -> userService.sortFollowed(buyerId, orderDesc));
        Assertions.assertDoesNotThrow(() -> userService.sortFollowed(buyerId, noOrder));
    }

    // T-0003: Verificar que el tipo de ordenamiento alfabético no exista para los seguidos
    // Resultado: Notifica la no existencia mediante una excepción.
    @Test
    void testSortFollowedInvalidOrder() {
        // Arrange
        Integer buyerId = 1;
        Buyer buyer = new Buyer(buyerId, "buyer");
        String failOrderAsc = "asc";
        String failOrderDesc = "desc";
        String otherOrder = "empanada";
        when(buyerRepository.get(buyerId)).thenReturn(Optional.of(buyer));
        when(sellerRepository.get(buyerId)).thenReturn(Optional.empty());
        // Act & Assert
        Assertions.assertThrows(BadRequestException.class, () -> userService.sortFollowed(buyerId, failOrderAsc));
        Assertions.assertThrows(BadRequestException.class, () -> userService.sortFollowed(buyerId, failOrderDesc));
        Assertions.assertThrows(BadRequestException.class, () -> userService.sortFollowed(buyerId, otherOrder));
    }

    // T-0004: Verificar que el ordenamiento alfabético se realice correctamente.
    // Resultado: Retorna el DTO con la lista ordenada alfabéticamente (ascendentemente).
    @Test
    void testSortFollowersOK() {
        Integer sellerId = 1;
        Integer fakeUserId1 = 5, fakeUserId2 = 6, fakeUserId3 = 7;
        String order = "NAME_ASC";

        Buyer fakeUser1 = new Buyer(fakeUserId1, "Benito");
        Buyer fakeUser2 = new Buyer(fakeUserId2, "Armando");
        Buyer fakeUser3 = new Buyer(fakeUserId3, "Carlos");
        Seller seller = new Seller(
                1,
                "Vendedor #1",
                new HashSet<>(List.of(fakeUserId1, fakeUserId2, fakeUserId3)),
                new HashSet<>(),
                new HashSet<>()
        );

        UserDTO fakeUserDto1 = new UserDTO(fakeUser1.getId(), fakeUser1.getName());
        UserDTO fakeUserDto2 = new UserDTO(fakeUser2.getId(), fakeUser2.getName());
        UserDTO fakeUserDto3 = new UserDTO(fakeUser3.getId(), fakeUser3.getName());

        when(buyerRepository.get(sellerId)).thenReturn(Optional.empty());
        when(sellerRepository.get(sellerId)).thenReturn(Optional.of(seller));
        when(buyerRepository.get(fakeUserId1)).thenReturn(Optional.of(fakeUser1));
        when(buyerRepository.get(fakeUserId2)).thenReturn(Optional.of(fakeUser2));
        when(buyerRepository.get(fakeUserId3)).thenReturn(Optional.of(fakeUser3));

        when(modelMapper.map(fakeUser1, UserDTO.class)).thenReturn(fakeUserDto1);
        when(modelMapper.map(fakeUser2, UserDTO.class)).thenReturn(fakeUserDto2);
        when(modelMapper.map(fakeUser3, UserDTO.class)).thenReturn(fakeUserDto3);

        FollowerDTO followersInfo = userService.sortFollowers(sellerId, order);

        assertEquals("Armando", followersInfo.getFollowers().get(0).getName());
        assertEquals("Benito", followersInfo.getFollowers().get(1).getName());
        assertEquals("Carlos", followersInfo.getFollowers().get(2).getName());
    }

    // T-0004: Verificar que el ordenamiento alfabético se realice correctamente.
    // Resultado: Retorna el DTO con la lista ordenada alfabéticamente (descendentemente).
    @Test
    void testSortFollowedOK() {
        Integer sellerId = 1;
        Integer fakeUserId1 = 5, fakeUserId2 = 6, fakeUserId3 = 7;
        String order = "NAME_DESC";

        Buyer fakeUser1 = new Buyer(fakeUserId1, "Armando");
        Buyer fakeUser2 = new Buyer(fakeUserId2, "Benito");
        Buyer fakeUser3 = new Buyer(fakeUserId3, "Carlos");
        Seller seller = new Seller(
                1,
                "Vendedor #1",
                new HashSet<>(List.of(fakeUserId1, fakeUserId2, fakeUserId3)),
                new HashSet<>(),
                new HashSet<>()
        );

        UserDTO fakeUserDto1 = new UserDTO(fakeUser1.getId(), fakeUser1.getName());
        UserDTO fakeUserDto2 = new UserDTO(fakeUser2.getId(), fakeUser2.getName());
        UserDTO fakeUserDto3 = new UserDTO(fakeUser3.getId(), fakeUser3.getName());

        when(buyerRepository.get(sellerId)).thenReturn(Optional.empty());
        when(sellerRepository.get(sellerId)).thenReturn(Optional.of(seller));
        when(buyerRepository.get(fakeUserId1)).thenReturn(Optional.of(fakeUser1));
        when(buyerRepository.get(fakeUserId2)).thenReturn(Optional.of(fakeUser2));
        when(buyerRepository.get(fakeUserId3)).thenReturn(Optional.of(fakeUser3));

        when(modelMapper.map(fakeUser1, UserDTO.class)).thenReturn(fakeUserDto1);
        when(modelMapper.map(fakeUser2, UserDTO.class)).thenReturn(fakeUserDto2);
        when(modelMapper.map(fakeUser3, UserDTO.class)).thenReturn(fakeUserDto3);

        FollowerDTO followersInfo = userService.sortFollowers(sellerId, order);

        assertEquals("Carlos", followersInfo.getFollowers().get(0).getName());
        assertEquals("Benito", followersInfo.getFollowers().get(1).getName());
        assertEquals("Armando", followersInfo.getFollowers().get(2).getName());
    }

    // T-0004: Verificar que el ordenamiento alfabético se realice correctamente.
    // Resultado: Retorna una excepción dado que le pasamos un ID inexistente.
    @Test
    void testSortFollowersThrowsNotFound() {
        Integer sellerId = 1;
        String order = "NAME_ASC";

        when(sellerRepository.get(sellerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.sortFollowers(sellerId, order));
    }

    @Test
    void isSeller() {
    }


}

