package com.Ecommerce.ProductService.Repository;

import com.Ecommerce.ProductService.Model.Category;
import com.Ecommerce.ProductService.Model.Product;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {

  Page<Product> findByCategory(Category category, Pageable pageable);
  Page<Product> findByBrand(String brand, Pageable pageable);

  List<Product> findAllById(@NonNull Iterable<UUID> ids);

  @Query(value = "SELECT * FROM products p WHERE CONCAT(p.brand, ' ', p.name) ILIKE CONCAT('%', :query, '%') AND category= :category AND price BETWEEN :minPrice AND :maxPrice ORDER BY p.created_at DESC",
      countQuery = "SELECT COUNT(*) FROM products p WHERE CONCAT(p.brand, ' ', p.name) ILIKE CONCAT('%', :query, '%') AND category ILIKE :category AND price BETWEEN :minPrice AND :maxPrice",
      nativeQuery = true)
  Page<Product> searchProductByQuery(@Param("query") String query, @Param("category") String category, @Param("minPrice") int minPrice, @Param("maxPrice") int maxPrice, Pageable pageable);
}
