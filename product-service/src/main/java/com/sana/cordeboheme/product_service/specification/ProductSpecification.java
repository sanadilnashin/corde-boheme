package com.sana.cordeboheme.product_service.specification;

import com.sana.cordeboheme.product_service.entity.Product;
import java.math.BigDecimal;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {
  // select * from table where ..
  // specification<Product>--- where
  // root-- table
  // root.get("name")---product.name
  // criteriaBuilder -- create sql condition criteriaBuilder.equal(...) means =
  // criteriaBuilder.like(...) --like
  // criteriaBuilder.conjunction()-- where 1=1 -- no filter
  // SELECT *
  // FROM product
  // WHERE LOWER(name) LIKE '%mac%'
  public static Specification<Product> hasName(String name) {
    return (root, query, cb) -> {
      if (name == null || name.isBlank()) {
        return null;
      }

      return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    };
  }

  public static Specification<Product> hasMaxPrice(BigDecimal maxPrice) {
    return (root, query, cb) -> {
      if (maxPrice == null) {
        return null;
      }

      return cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    };
  }

  public static Specification<Product> hasMinPrice(BigDecimal minPrice) {
    return (root, query, cb) -> {
      if (minPrice == null) {
        return null;
      }

      return cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    };
  }

  public static Specification<Product> isNotDeleted() {
    return (root, query, cb) -> cb.isFalse(root.get("deleted"));
  }
}
