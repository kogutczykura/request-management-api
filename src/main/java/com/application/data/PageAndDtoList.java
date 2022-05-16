package com.application.data;

import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PageAndDtoList {
    private List<RequestDto> requestDtos;
    private Page<Request> requestPage;
}
