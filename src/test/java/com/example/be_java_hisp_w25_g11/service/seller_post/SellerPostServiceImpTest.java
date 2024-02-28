package com.example.be_java_hisp_w25_g11.service.seller_post;

import com.example.be_java_hisp_w25_g11.dto.SellerPostDTO;
import com.example.be_java_hisp_w25_g11.dto.request.ProductDTO;
import com.example.be_java_hisp_w25_g11.dto.response.SellerPostsListDTO;
import com.example.be_java_hisp_w25_g11.entity.Buyer;
import com.example.be_java_hisp_w25_g11.entity.Product;
import com.example.be_java_hisp_w25_g11.entity.Seller;
import com.example.be_java_hisp_w25_g11.entity.SellerPost;
import com.example.be_java_hisp_w25_g11.exception.NotFoundException;
import com.example.be_java_hisp_w25_g11.repository.buyer.BuyerRepositoryImp;
import com.example.be_java_hisp_w25_g11.repository.seller.SellerRepositoryImp;
import com.example.be_java_hisp_w25_g11.service.user.UserServiceImp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SellerPostServiceImpTest {

    @Mock
    private SellerRepositoryImp sellerRepository;
    @Mock
    private BuyerRepositoryImp buyerRepository;
    @Mock
    private ModelMapper modelMapper;
    @InjectMocks
    private SellerPostServiceImp sellerPostService;

    @Test
    void createPost() {
    }

    // T-0008: Verificar que la consulta de publicaciones realizadas en las últimas dos
    // semanas de un determinado vendedor sean efectivamente de las últimas dos semanas.
    // Resultado: Permite continuar con normalidad y devuelve la lista de posts de las últimas dos semanas.
    @Test
    void testFollowedSellersLatestPostsOK() {
        Integer buyerId = 1;
        Integer sellerId1 = 2, sellerId2 = 3;
        Integer postId1 = 1, postId2 = 2, postId3 = 3, postId4 = 4;
        Set<Integer> followedSellers = new HashSet<>(List.of(sellerId1, sellerId2));

        SellerPost post1 = new SellerPost(
                sellerId1,
                postId1,
                LocalDate.now().minusWeeks(1),
                new Product(),
                1,
                10.0,
                null
        ), post2 = new SellerPost(
                sellerId1,
                postId2,
                LocalDate.now().minusWeeks(1),
                new Product(),
                1,
                10.0,
                null
        ), post3 = new SellerPost(
                sellerId2,
                postId3,
                LocalDate.now().minusWeeks(1),
                new Product(),
                1,
                10.0,
                null
        ), post4 = new SellerPost(
                sellerId2,
                postId4,
                LocalDate.now().minusWeeks(1),
                new Product(),
                1,
                10.0,
                null
        );

        SellerPostDTO postDto1 = new SellerPostDTO(
                sellerId1,
                postId1,
                "2024-02-25",
                new ProductDTO(),
                1,
                10.0
        ), postDto2 = new SellerPostDTO(
                sellerId1,
                postId1,
                "2024-02-25",
                new ProductDTO(),
                1,
                10.0
        ), postDto3 = new SellerPostDTO(
                sellerId2,
                postId2,
                "2024-02-25",
                new ProductDTO(),
                1,
                10.0
        ), postDto4 = new SellerPostDTO(
                sellerId2,
                postId2,
                "2024-02-25",
                new ProductDTO(),
                1,
                10.0
        );


        Buyer buyer = new Buyer(1, "Juan", followedSellers);
        Seller seller1 = new Seller(
                sellerId1,
                "Jaime",
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(List.of(post1, post2))
        ), seller2 = new Seller(
                sellerId2,
                "Batman",
                new HashSet<>(),
                new HashSet<>(),
                new HashSet<>(List.of(post3, post4))
        );

        when(buyerRepository.get(buyerId)).thenReturn(Optional.of(buyer));
        when(sellerRepository.get(buyerId)).thenReturn(Optional.empty());
        when(sellerRepository.get(sellerId1)).thenReturn(Optional.of(seller1));
        when(sellerRepository.get(sellerId2)).thenReturn(Optional.of(seller2));

        when(modelMapper.map(post1, SellerPostDTO.class)).thenReturn(postDto1);
        when(modelMapper.map(post2, SellerPostDTO.class)).thenReturn(postDto2);
        when(modelMapper.map(post3, SellerPostDTO.class)).thenReturn(postDto3);
        when(modelMapper.map(post4, SellerPostDTO.class)).thenReturn(postDto4);

        SellerPostsListDTO followedSellersPosts = sellerPostService
                .getFollowedSellersLatestPosts(buyerId, null);

        assertFalse(followedSellersPosts.getPosts()
                .stream()
                .anyMatch(v -> LocalDate.parse(
                        v.getDate(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd")
                ).isBefore(LocalDate.now().minusWeeks(2))));
    }

    // T-0008: Verificar que la consulta de publicaciones realizadas en las últimas dos
    // semanas de un determinado vendedor sean efectivamente de las últimas dos semanas.
    // Resultado: Lanza una excepción al no encontrar un usuario con ese ID.
    @Test
    void testFollowedSellersLatestPostsThrowsNotFound() {
        Integer buyerId = 1;

        when(sellerRepository.get(buyerId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> sellerPostService.getFollowedSellersLatestPosts(buyerId, null));
    }
}