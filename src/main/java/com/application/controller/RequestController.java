package com.application.controller;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.application.data.PageAndDtoList;
import com.application.data.PageableResourceDto;
import com.application.data.RequestDto;
import com.application.data.RequestState;
import com.application.service.RequestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/request")
public class RequestController {

    private final RequestService requestService;

    @GetMapping
    ResponseEntity<PageableResourceDto<RequestDto>> getAll(@RequestParam(required = false) String name,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        try {
            PageAndDtoList pageAndDto = requestService.findAll(name, page, size);
            return getMapResponseEntity(pageAndDto);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/status")
    ResponseEntity<PageableResourceDto<RequestDto>> getAllRequests(@RequestParam(required = false) RequestState requestState,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size) {
        try {
            PageAndDtoList pageAndDto = requestService.findAllWithState(requestState, page, size);
            return getMapResponseEntity(pageAndDto);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private ResponseEntity<PageableResourceDto<RequestDto>> getMapResponseEntity(PageAndDtoList pageAndDto) {
        PageableResourceDto<RequestDto> pages = PageableResourceDto.<RequestDto>builder()
                .items(pageAndDto.getRequestDtos())
                .currentPage(pageAndDto.getRequestPage().getNumber())
                .totalItems(pageAndDto.getRequestPage().getTotalElements())
                .totalPages(pageAndDto.getRequestPage().getTotalPages())
                .build();
        return new ResponseEntity<>(pages, HttpStatus.OK);
    }

    @GetMapping("{id}")
    RequestDto getOne(@PathVariable Long id) {
        return requestService.getRequest(id);
    }

    @PostMapping
    RequestDto createNewRequest(@Valid @RequestBody RequestDto requestDto, BindingResult bindingResult) {
        return requestService.create(requestDto, bindingResult);
    }

    @PutMapping("{id}")
    RequestDto update(@Valid @RequestBody RequestDto requestDto, BindingResult bindingResult) {
        return requestService.update(requestDto, bindingResult);
    }
}
