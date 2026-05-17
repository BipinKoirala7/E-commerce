package com.Ecommerce.ProductService.Mapper;

import com.Ecommerce.ProductService.DTOs.Request.ProductCreateDTO;
import com.Ecommerce.ProductService.DTOs.Request.ProductUpdateDTO;
import com.Ecommerce.ProductService.DTOs.Response.ProductSummary;
import com.Ecommerce.ProductService.Model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface ProductMapper {

  Product toProductEntity(ProductCreateDTO productCreateDTO);
  List<ProductSummary> toProductSummaryList(List<Product> products);
  ProductSummary toProductSummary(Product product);
  void fromUpdateDtoToProductEntity(ProductUpdateDTO productUpdateDTO, @MappingTarget Product product);
}
