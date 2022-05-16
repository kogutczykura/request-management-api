package com.application.data;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageableResourceDto<T> {
    private List<T> items;
    private long currentPage;
    private long totalItems;
    private long totalPages;
}
