package com.Ecommerce.ProductService.DTOs.Response;

import lombok.Data;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;

import java.util.List;

@Data
public class PageResponse<T> {

  private List<T> content;
  private int currentPage;
  private int totalPages;
  private long totalElements;
  private boolean isFirst;
  private boolean isLast;
  private int pageSize;
  private int numberOfElements;

  public static <T> @NonNull PageResponse<T> from(@NonNull Page<T> page) {
    PageResponse<T> response = new PageResponse<>();
    response.content = page.getContent();
    response.currentPage = page.getNumber();
    response.totalPages = page.getTotalPages();
    response.totalElements = page.getTotalElements();
    response.isFirst = page.isFirst();
    response.isLast = page.isLast();
    response.pageSize = page.getSize(); // Not important, If no use found, remove
    response.numberOfElements = page.getNumberOfElements();
    return response;
  }
}
