package com.Ecommerce.ProductService.Service;

import com.Ecommerce.ProductService.DTOs.Request.ProductCreateDTO;
import com.Ecommerce.ProductService.DTOs.Request.ProductUpdateDTO;
import com.Ecommerce.ProductService.DTOs.Response.PageResponse;
import com.Ecommerce.ProductService.DTOs.Response.ProductSummary;
import com.Ecommerce.ProductService.Exception.ProductNotFoundException;
import com.Ecommerce.ProductService.Exception.ProductWithNoImageException;
import com.Ecommerce.ProductService.Mapper.ProductMapper;
import com.Ecommerce.ProductService.Model.Category;
import com.Ecommerce.ProductService.Model.Product;
import com.Ecommerce.ProductService.Model.ProductSpecification;
import com.Ecommerce.ProductService.Repository.ProductRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Core Service that handles product creation, deletion, update
 * and fetching of the product.
 *
 * @see ProductRepository
 * @see ProductMapper
 * @see ProductCreateDTO
 * @see ProductUpdateDTO
 * */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

  private final ProductRepository productRepository;
  private final ProductMapper productMapper;

  @Transactional
  public Product createNewProduct(ProductCreateDTO newProductDTO) {
    log.info("Creating Product...");

    if (Objects.isNull(newProductDTO)) {
      log.warn("New Product is null");
      throw new IllegalArgumentException("Product cannot be null");
    }
    log.debug("Product Creation Info - New Product is present");

    Product newProduct = productMapper.toProductEntity(newProductDTO);
    log.debug("Product Creation Info - New Product Entity is created");

    if(Objects.isNull(newProduct.getImageUrl())) {
      log.warn("Product Creation Failed - Product cannot be created without images");
      throw new ProductWithNoImageException("Product with no images");
    }

    Product product = productRepository.save(newProduct);
    log.debug("Product Creation Success");
    return product;
  }

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
    log.debug("Fetching Product Summary Info - Product ID is present");

    Product product = productRepository
        .findById(productId)
        .orElseThrow(() -> {
          log.warn("Fetching Product Summary Info - Product with given ID not found");
          return new ProductNotFoundException("Product Not Found");
        });

    log.info("Fetching Product Summary Success");
    ProductSummary summary = productMapper.toProductSummary(product);
    log.debug("Fetching product summary for : {}", summary.getName());
    return summary;
  }

  public Product getProduct(UUID productId) {
    log.info("Fetching product info...");

    if (Objects.isNull(productId)) {
      log.warn("Fetching Product Info - Product ID is null");
      throw new IllegalArgumentException("Product ID cannot be null");
    }
    log.debug("Fetching Product Info - Product ID is present");

    Product product = productRepository
        .findById(productId)
        .orElseThrow(() -> {
          log.warn("Fetching Product Info - Product with given ID not found");
          return new ProductNotFoundException("Product Not Found");
        });

    log.info("Fetching Product Success");
    return product;
  }

  @Transactional
  public void updateProduct(UUID productId, ProductUpdateDTO productUpdateDTO) {
    log.info("Updating a Product...");

    if (Objects.isNull(productId)) {
      log.warn("Product Update Info - Product ID is null");
      throw new IllegalArgumentException("Product ID cannot be null");
    }
    log.debug("Product Update Info - Product ID is present");

    if (Objects.isNull(productUpdateDTO)) {
      log.warn("Updated Product is null so updating not possible");
      throw new IllegalArgumentException("Updated Product cannot be null");
    }
    log.debug("Product Update Info - Updated Product is present");

    Product product = getProduct(productId);
    log.debug("Product Update Info - Existing Product is fetched");

    if(Objects.isNull(product.getImageUrl())) {
      log.warn("Product Update Failed - Product cannot be updated without image");
      throw new ProductWithNoImageException("Product with no image");
    }

    productMapper.fromUpdateDtoToProductEntity(productUpdateDTO, product);
    this.productRepository.save(product);
    log.info("Updated Product saved");
  }

  @Transactional
  public void deleteProduct(UUID productId) {
    log.info("Product Deletion...");

    if (Objects.isNull(productId)) {
      log.warn("Product Deletion Failed - Product ID is null");
      throw new IllegalArgumentException("Product ID cannot be null");
    }
    log.debug("Product Deletion Info - Product ID is present");

    if (!productRepository.existsById(productId)) {
      log.warn("Product Deletion Failed - Product with given ID Not Found");
      throw new ProductNotFoundException("Product with given ID Not Found");
    }
    log.debug("Product Deletion Info - Product with given ID is present");

    this.productRepository.deleteById(productId);
    log.info("Successfully deleted a product");
  }

  public List<ProductSummary> getProductBatch(Set<UUID> ids){
    if(ids.isEmpty()){
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
    log.debug("Product Verification Info - Product ID is present");

    return getProduct(productId).getId().equals(productId);
  }
}
