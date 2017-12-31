package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.reactivestreams.Publisher;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

public class VendorControllerTest {

    WebTestClient webTestClient;
    VendorRepository vendorRepository;
    VendorController controller;

    @Before
    public void setUp() throws Exception {
        vendorRepository = Mockito.mock(VendorRepository.class);
        controller = new VendorController(vendorRepository);
        webTestClient = WebTestClient.bindToController(controller).build();
    }

    @Test
    public void list() {

        given(vendorRepository.findAll())
                .willReturn(Flux.just(Vendor.builder().firstName("Fred").lastName("Flintstone").build(),
                        Vendor.builder().firstName("Barney").lastName("Rubble").build()));

        webTestClient.get()
                .uri("/api/v1/vendors")
                .exchange()
                .expectBodyList(Vendor.class)
                .hasSize(2);
    }

    @Test
    public void getById() {
        given(vendorRepository.findById("someid"))
                .willReturn(Mono.just(Vendor.builder().firstName("Jimmy").lastName("Johns").build()));

        webTestClient.get()
                .uri("/api/v1/vendors/someid")
                .exchange()
                .expectBody(Vendor.class);
    }

    @Test
    public void testCreateVendor() {
        given(vendorRepository.saveAll(any(Publisher.class)))
                .willReturn(Flux.just(Vendor.builder().build()));

        Mono<Vendor> vendorToSaveMono = Mono.just(Vendor.builder().firstName("First Name")
                                                .lastName("Last Name").build());

        webTestClient.post()
                .uri("/api/v1/vendors")
                .body(vendorToSaveMono, Vendor.class)
                .exchange()
                .expectStatus()
                .isCreated();
    }

    @Test
    public void testUpdateVendor() {

        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(Vendor.builder().build()));

        Mono<Vendor> vendorMonoToUpdate = Mono.just(Vendor.builder().build());

        webTestClient.put()
                .uri("/api/v1/vendors/someid")
                .body(vendorMonoToUpdate, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();

    }

    @Test
    public void testPatchVendorWithChanges() {

        given(vendorRepository.findById(anyString()))
                .willReturn(Mono.just(Vendor.builder().firstName("Jimmy").build()));

        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(Vendor.builder().build()));

        Mono<Vendor> vendorMonoToUpdate = Mono.just(Vendor.builder().firstName("Jim").build());

        webTestClient.patch()
                .uri("/api/v1/vendors/someid")
                .body(vendorMonoToUpdate, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();

        verify(vendorRepository).save(any());
    }

    @Test
    public void testPatchVendorWithoutChanges() {

        given(vendorRepository.findById(anyString()))
                .willReturn(Mono.just(Vendor.builder().firstName("Jimmy").build()));

        given(vendorRepository.save(any(Vendor.class)))
                .willReturn(Mono.just(Vendor.builder().build()));

        Mono<Vendor> vendorMonoToUpdate = Mono.just(Vendor.builder().firstName("Jimmy").build());

        webTestClient.patch()
                .uri("/api/v1/vendors/someid")
                .body(vendorMonoToUpdate, Vendor.class)
                .exchange()
                .expectStatus()
                .isOk();

        verify(vendorRepository, never()).save(any());
    }
}