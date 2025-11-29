package com.mapnaom.foodapp.dtos;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedUserResponseDto {
    private List<UserResponseDto> users;
    private int totalElements;
    private int totalPages;
    private int currentPage;
    private int pageSize;
    private boolean hasNext;
    private boolean hasPrevious;
    private boolean isFirst;
    private boolean isLast;

    // Custom builder method to automatically calculate pagination flags
    public static PagedUserResponseDto of(List<UserResponseDto> users, int totalElements, int currentPage, int pageSize) {
        int totalPages = (int) Math.ceil((double) totalElements / pageSize);

        return PagedUserResponseDto.builder()
                .users(users)
                .totalElements(totalElements)
                .totalPages(totalPages)
                .currentPage(currentPage)
                .pageSize(pageSize)
                .hasNext(currentPage < totalPages - 1)
                .hasPrevious(currentPage > 0)
                .isFirst(currentPage == 0)
                .isLast(currentPage == totalPages - 1)
                .build();
    }
}
