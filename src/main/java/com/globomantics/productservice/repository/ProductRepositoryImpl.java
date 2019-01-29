package com.globomantics.productservice.repository;

import com.globomantics.productservice.model.Product;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private static final Logger logger = LogManager.getLogger(ProductRepositoryImpl.class);

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcInsert simpleJdbcInsert;

    public ProductRepositoryImpl(JdbcTemplate jdbcTemplate, DataSource dataSource) {
        this.jdbcTemplate = jdbcTemplate;

        // Build a SimpleJdbcInsert object from the specified data source
        this.simpleJdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("products")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Optional<Product> findById(Integer id) {
        try {
            Product product = jdbcTemplate.queryForObject("SELECT * FROM products WHERE id = ?",
                    new Object[]{id},
                    (rs, rowNum) -> {
                        Product p = new Product();
                        p.setId(rs.getInt("id"));
                        p.setName(rs.getString("name"));
                        p.setQuantity(rs.getInt("quantity"));
                        p.setVersion(rs.getInt("version"));
                        return p;
                    });
            return Optional.of(product);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Product> findAll() {
        return jdbcTemplate.query("SELECT * FROM products",
                (rs, rowNumber) -> {
                    Product product = new Product();
                    product.setId(rs.getInt("id"));
                    product.setName(rs.getString("name"));
                    product.setQuantity(rs.getInt("quantity"));
                    product.setVersion(rs.getInt("version"));
                    return product;
                });
    }

    @Override
    public boolean update(Product product) {
        return jdbcTemplate.update("UPDATE products SET name = ?, quantity = ?, version = ? WHERE id = ?",
                product.getName(),
                product.getQuantity(),
                product.getVersion(),
                product.getId()) == 1;
    }

    @Override
    public Product save(Product product) {
        // Build the product parameters we want to save
        Map<String, Object> parameters = new HashMap<>(1);
        parameters.put("name", product.getName());
        parameters.put("quantity", product.getQuantity());
        parameters.put("version", product.getVersion());

        // Execute the query and get the generated key
        Number newId = simpleJdbcInsert.executeAndReturnKey(parameters);

        logger.info("Inserting product into database, generated key is: {}", newId);

        // Update the product's ID with the new key
        product.setId((Integer)newId);

        // Return the complete product
        return product;
    }

    @Override
    public boolean delete(Integer id) {
        return jdbcTemplate.update("DELETE FROM products WHERE id = ?", id) == 1;
    }
}
