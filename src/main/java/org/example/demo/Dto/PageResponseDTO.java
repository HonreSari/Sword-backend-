package org.example.demo.Dto;

import java.util.List;
import org.springframework.data.domain.Page;

public record PageResponseDTO<T>(
    List<T> content,
    int pageNumber,
    int pageSize,
    long totalElements,
    int totalPages,
    boolean isFirst,
    boolean isLast) {
  // ✅ Static factory method for easy conversion from Spring Page
  public static <T> PageResponseDTO<T> from(Page<T> page) {
    return new PageResponseDTO<>(
        page.getContent(),
        page.getNumber(),
        page.getSize(),
        page.getTotalElements(),
        page.getTotalPages(),
        page.isFirst(),
        page.isLast());
  }
}
