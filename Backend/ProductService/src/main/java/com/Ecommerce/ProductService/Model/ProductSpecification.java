package com.Ecommerce.ProductService.Model;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProductSpecification {

  public static Specification<Product> searchWithFilters(String query, String category, int minPrice, int maxPrice) {
    return (root, criteriaQuery, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      if(Objects.nonNull(query) && !query.isBlank()) {
        String pattern = "%" + query.toLowerCase() + "%";
        predicates.add(criteriaBuilder.like(
            criteriaBuilder.lower(criteriaBuilder.concat(criteriaBuilder.concat(root.get("brand"), " "), root.get("name"))),
            pattern
        ));
      }
      if (category != null && !category.isBlank()) {
        predicates.add(criteriaBuilder.equal(root.get("category"), Category.fromString(category)));
      }

      if (minPrice > 0) {
        predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("price"), minPrice));
      }

      if (maxPrice > 0) {
        predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("price"), maxPrice));
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }
}
