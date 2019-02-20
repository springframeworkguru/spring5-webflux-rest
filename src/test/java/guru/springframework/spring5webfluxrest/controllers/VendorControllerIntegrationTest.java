package guru.springframework.spring5webfluxrest.controllers;

import guru.springframework.spring5webfluxrest.domain.Vendor;
import guru.springframework.spring5webfluxrest.repositories.VendorRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@DataMongoTest
@RunWith(SpringRunner.class)
public class VendorControllerIntegrationTest {

    @Autowired
    VendorRepository vendorRepository;
    WebTestClient webTestClient;
    VendorController controller;

    Vendor savedVendor;

    @Before
    public void setUp() throws Exception {
        controller = new VendorController(vendorRepository);
        webTestClient = WebTestClient.bindToController(controller).build();

        savedVendor = vendorRepository.save(Vendor.builder().firstName("Salah").build()).block();
    }

    @Test
    public void testPatchVendorLogic() {
        Mono<Vendor> vendorMonoToUpdate = Mono.just(Vendor.builder().build());

        webTestClient.patch()
                .uri("/api/v1/vendors/"+savedVendor.getId())
                .body(vendorMonoToUpdate, Vendor.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                //Since the body is empty, the object should not be changed
                .jsonPath("$.firstName").isEqualTo("Salah");
    }
}