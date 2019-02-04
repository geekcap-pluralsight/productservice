package com.globomantics.productservice.service;

import com.globomantics.productservice.model.Product;
import com.globomantics.productservice.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }


    public Optional<Product> findById(Integer id) {
        return productRepository.findById(id);
    }


    public List<Product> findAll() {
        return productRepository.findAll();
    }


    public boolean update(Product product) {
        return productRepository.update(product);
    }


    public Product save(Product product) {
        product.setVersion(1);
        return productRepository.save(product);
    }

    public boolean delete(Integer id) {
        return productRepository.delete(id);
    }
}
