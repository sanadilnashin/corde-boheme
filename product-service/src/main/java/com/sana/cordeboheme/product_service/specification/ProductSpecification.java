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
    return ((root, query, criteriaBuilder) -> {
      if (name == null || name.isBlank()) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.like(
          criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    });
  }

  public static Specification<Product> hasMaxPrice(BigDecimal price) {
    return ((root, query, criteriaBuilder) ->
        criteriaBuilder.lessThanOrEqualTo(root.get("price"), price));
  }

  public static Specification<Product> hasMinPrice(BigDecimal price) {
    return ((root, query, criteriaBuilder) ->
        criteriaBuilder.greaterThanOrEqualTo(root.get("price"), price));
  }
}
