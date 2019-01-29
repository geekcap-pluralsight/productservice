package com.globomantics.productservice.web;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.globomantics.productservice.model.Product;
import com.globomantics.productservice.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Optional;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerTest {
    @MockBean
    private ProductService service;

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("GET /product/1 - Found")
    void testGetProductByIdFound() throws Exception {
        // Setup our mocked service
        Product mockProduct = new Product(1, "Product Name", 10, 1);
        doReturn(Optional.of(mockProduct)).when(service).findById(1);

        // Execute the GET request
        mockMvc.perform(get("/product/{id}", 1))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.ETAG, "\"1\""))
                .andExpect(header().string(HttpHeaders.LOCATION, "/product/1"))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Product Name")))
                .andExpect(jsonPath("$.quantity", is(10)))
                .andExpect(jsonPath("$.version", is(1)));
    }

    @Test
    @DisplayName("GET /product/1 - Not Found")
    void testGetProductByIdNotFound() throws Exception {
        // Setup our mocked service
        doReturn(Optional.empty()).when(service).findById(1);

        // Execute the GET request
        mockMvc.perform(get("/product/{id}", 1))

                // Validate that we get a 404 Not Found response
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /product - Success")
    void testCreateProduct() throws Exception {
        // Setup mocked service
        Product postProduct = new Product("Product Name", 10);
        Product mockProduct = new Product(1, "Product Name", 10, 1);
        doReturn(mockProduct).when(service).save(any());

        mockMvc.perform(post("/product")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(postProduct)))

                // Validate the response code and content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.ETAG, "\"1\""))
                .andExpect(header().string(HttpHeaders.LOCATION, "/product/1"))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Product Name")))
                .andExpect(jsonPath("$.quantity", is(10)))
                .andExpect(jsonPath("$.version", is(1)));
    }

    @Test
    @DisplayName("PUT /product/1 - Success")
    void testProductPutSuccess() throws Exception {
        // Setup mocked service
        Product putProduct = new Product("Product Name", 10);
        Product mockProduct = new Product(1, "Product Name", 10, 1);
        doReturn(Optional.of(mockProduct)).when(service).findById(1);
        doReturn(true).when(service).update(any());

        mockMvc.perform(put("/product/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.IF_MATCH, 1)
                .content(asJsonString(putProduct)))

                // Validate the response code and content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8_VALUE))

                // Validate the headers
                .andExpect(header().string(HttpHeaders.ETAG, "\"2\""))
                .andExpect(header().string(HttpHeaders.LOCATION, "/product/1"))

                // Validate the returned fields
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("Product Name")))
                .andExpect(jsonPath("$.quantity", is(10)))
                .andExpect(jsonPath("$.version", is(2)));
    }

    @Test
    @DisplayName("PUT /product/1 - Version Mismatch")
    void testProductPutVersionMismatch() throws Exception {
        // Setup mocked service
        Product putProduct = new Product("Product Name", 10);
        Product mockProduct = new Product(1, "Product Name", 10, 2);
        doReturn(Optional.of(mockProduct)).when(service).findById(1);
        doReturn(true).when(service).update(any());

        mockMvc.perform(put("/product/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.IF_MATCH, 1)
                .content(asJsonString(putProduct)))

                // Validate the response code and content type
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PUT /product/1 - Not Found")
    void testProductPutNotFound() throws Exception {
        // Setup mocked service
        Product putProduct = new Product("Product Name", 10);
        doReturn(Optional.empty()).when(service).findById(1);

        mockMvc.perform(put("/product/{id}", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.IF_MATCH, 1)
                .content(asJsonString(putProduct)))

                // Validate the response code and content type
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /product/1 - Success")
    void testProductDeleteSuccess() throws Exception {
        // Setup mocked product
        Product mockProduct = new Product(1, "Product Name", 10, 1);

        // Setup the mocked service
        doReturn(Optional.of(mockProduct)).when(service).findById(1);
        doReturn(true).when(service).delete(1);

        // Execute our DELETE request
        mockMvc.perform(delete("/product/{id}", 1))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /product/1 - Not Found")
    void testProductDeleteNotFound() throws Exception {
        // Setup the mocked service
        doReturn(Optional.empty()).when(service).findById(1);

        // Execute our DELETE request
        mockMvc.perform(delete("/product/{id}", 1))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /product/1 - Failure")
    void testProductDeleteFailure() throws Exception {
        // Setup mocked product
        Product mockProduct = new Product(1, "Product Name", 10, 1);

        // Setup the mocked service
        doReturn(Optional.of(mockProduct)).when(service).findById(1);
        doReturn(false).when(service).delete(1);

        // Execute our DELETE request
        mockMvc.perform(delete("/product/{id}", 1))
                .andExpect(status().isInternalServerError());
    }

    static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
