package guru.springframework.spring6reactive.controllers;

import guru.springframework.spring6reactive.domain.Beer;
import guru.springframework.spring6reactive.model.BeerDTO;
import guru.springframework.spring6reactive.repository.BeerRepositoryTest;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.mockOAuth2Login;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest
@AutoConfigureWebTestClient
class BeerControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    @Order(1)
    void testGetById() {
        webTestClient
                .mutateWith(mockOAuth2Login())
                .get().uri(BeerController.BEER_PATH_ID, 1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-type", "application/json")
                .expectBody(BeerDTO.class);
    }

    @Test
    @Order(2)
    void testListBeers() {
        webTestClient
                .mutateWith(mockOAuth2Login())
                .get().uri(BeerController.BEER_PATH)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-type", "application/json")
                .expectBody().jsonPath("$.size()").isEqualTo(3);
    }

    @Test
    @Order(3)
    void testGetByIdNotFound() {
        webTestClient
                .mutateWith(mockOAuth2Login())
                .get().uri(BeerController.BEER_PATH_ID, 666)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Order(4)
    void testCreateBeer() {
        webTestClient
                .mutateWith(mockOAuth2Login())
                .post().uri(BeerController.BEER_PATH)
                .body(Mono.just(BeerRepositoryTest.getTestBeer()), BeerDTO.class)
                .header("Content-type", "application/json")
                .exchange()
                .expectStatus().isCreated()
                .expectHeader().location("/api/v2/beer/4");
    }

    @Test
    @Order(5)
    void testCreateBeerBadData() {
        Beer testBeer = BeerRepositoryTest.getTestBeer();
        testBeer.setBeerName("");

        webTestClient
                .mutateWith(mockOAuth2Login())
                .post().uri(BeerController.BEER_PATH)
                .body(Mono.just(testBeer), BeerDTO.class)
                .header("Content-type", "application/json")
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    @Order(6)
    void testUpdateBeer() {
        webTestClient
                .mutateWith(mockOAuth2Login())
                .put().uri(BeerController.BEER_PATH_ID, 1)
                .body(Mono.just(BeerRepositoryTest.getTestBeer()), BeerDTO.class)
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    @Order(7)
    void testUpdateBeerBadRequest() {
        Beer testBeer = BeerRepositoryTest.getTestBeer();
        testBeer.setBeerStyle("");

        webTestClient
                .mutateWith(mockOAuth2Login())
                .put().uri(BeerController.BEER_PATH_ID, 1)
                .body(Mono.just(testBeer), BeerDTO.class)
                .exchange()
                .expectStatus()
                .isBadRequest();
    }

    @Test
    @Order(8)
    void testPatchBeerNotFound() {
        webTestClient
                .mutateWith(mockOAuth2Login())
                .patch().uri(BeerController.BEER_PATH_ID, 666)
                .body(Mono.just(BeerRepositoryTest.getTestBeer()), BeerDTO.class)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    @Order(9)
    void testDeleteBeerNotFound() {
        webTestClient
                .mutateWith(mockOAuth2Login())
                .delete()
                .uri(BeerController.BEER_PATH_ID, 666)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    @Order(10)
    void testUpdateBeerNotFound() {
        webTestClient
                .mutateWith(mockOAuth2Login())
                .put().uri(BeerController.BEER_PATH_ID, 666)
                .body(Mono.just(BeerRepositoryTest.getTestBeer()), BeerDTO.class)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    @Order(999)
    void testDeleteBeer() {
        webTestClient
                .mutateWith(mockOAuth2Login())
                .delete()
                .uri(BeerController.BEER_PATH_ID, 1)
                .exchange()
                .expectStatus()
                .isNoContent();
    }
}