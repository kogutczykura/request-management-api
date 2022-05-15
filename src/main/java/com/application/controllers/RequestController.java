package com.application.controllers;

import com.application.RequestDto;
import com.application.RequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/request")
public class RequestController {

    @Autowired
    RequestService requestService;

    @GetMapping
    List<RequestDto> getAll() {
        return requestService.findAll();
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
