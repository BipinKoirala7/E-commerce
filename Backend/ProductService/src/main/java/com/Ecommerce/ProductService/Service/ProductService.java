package com.Ecommerce.ProductService.Service;

import com.Ecommerce.ProductService.DTOs.Request.ProductCreateDTO;
import com.Ecommerce.ProductService.DTOs.Request.ProductUpdateDTO;
import com.Ecommerce.ProductService.DTOs.Response.PageResponse;
import com.Ecommerce.ProductService.DTOs.Response.ProductSummary;
import com.Ecommerce.ProductService.Exception.ProductNotFoundException;
import com.Ecommerce.ProductService.Mapper.ProductMapper;
import com.Ecommerce.ProductService.Model.Category;
import com.Ecommerce.ProductService.Model.Product;
import com.Ecommerce.ProductService.Model.ProductSpecification;
import com.Ecommerce.ProductService.Repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Core Service that handles product creation, deletion, update
 * and fetching of the product.
 *
 * @see ProductRepository
 * @see ProductMapper
 * @see ProductCreateDTO
 * @see ProductUpdateDTO
 *
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

  private final ProductRepository productRepository;
  private final ProductMapper productMapper;

  public PageResponse<Product> getProducts(
      String query, String category, int minPrice, int maxPrice,
      int page, String size, String sort, String direction
  ) {
    log.warn("Fetching products...");

    Specification<Product> specification = ProductSpecification.searchWithFilters(query, category, minPrice, maxPrice);
    PageRequest pageRequest = PageRequest.of(page, Integer.parseInt(size), Sort.by(Sort.Direction.valueOf(direction), sort));
    Page<Product> products = productRepository.findAll(specification, pageRequest);
    return PageResponse.from(products);
  }

  public PageResponse<Product> getProductsByCategory(Category category, Pageable pageable) {
    log.warn("Fetching all the products by category...");
    return PageResponse.from(productRepository.findByCategory(category, pageable));
  }

  public PageResponse<Product> getProductsByBrand(String brand, Pageable pageable) {
    log.warn("Fetching all the products by brand...");
    return PageResponse.from(productRepository.findByBrand(brand, pageable));
  }

  public ProductSummary getProductSummary(UUID productId) {
    log.info("Fetching product Summary...");
    if (Objects.isNull(productId)) {
      log.warn("Fetching product Summary Failed - Product ID is null");
      throw new IllegalArgumentException("Product ID cannot be null");
    }

    Product product = productRepository
        .findById(productId)
        .orElseThrow(() -> {
          log.warn("Fetching Product Summary Failed - Product with given ID not found");
          return new ProductNotFoundException("Product Not Found");
        });

    ProductSummary summary = productMapper.toProductSummary(product);
    return summary;
  }

  public Product getProduct(UUID productId) {
    log.info("Fetching product info...");

    if (Objects.isNull(productId)) {
      log.warn("Fetching Product Info - Product ID is null");
      throw new IllegalArgumentException("Product ID cannot be null");
    }

    Product product = productRepository
        .findById(productId)
        .orElseThrow(() -> {
          log.warn("Fetching Product Info - Product with given ID not found");
          return new ProductNotFoundException("Product Not Found");
        });

    log.info("Fetching Product Success");
    return product;
  }

  public List<ProductSummary> getProductBatch(Set<UUID> ids) {
    if (ids.isEmpty()) {
      log.warn("Set Of Ids are empty");
      throw new IllegalArgumentException("Ids are empty");
    }
    return productMapper.toProductSummaryList(productRepository.findAllById(ids));
  }

  // It is for another service to verify the existence of a product by its ID before
  // performing any operation related to that product.
  public Boolean verifyProductExists(UUID productId) {
    log.info("Product Verification...");

    if (Objects.isNull(productId)) {
      log.warn("Product ID cannot be null");
      throw new IllegalArgumentException("Product ID cannot be null");
    }

    return getProduct(productId).getId().equals(productId);
  }
}
