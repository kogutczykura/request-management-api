package com.application;

public class RequestMapper {
    public static Request fromDto(RequestDto requestDto, Request request) {
        request.setId(requestDto.getId());
        request.setName(requestDto.getName());
        request.setValue(requestDto.getValue());
        if (request.getState() != null) {
            request.setState(requestDto.getState());
        }
        return request;
    }

    public static RequestDto toDto(Request request) {
        RequestDto requestDto = new RequestDto();
        requestDto.setId(request.getId());
        requestDto.setName(request.getName());
        requestDto.setValue(request.getValue());
        requestDto.setState(request.getState());
        return requestDto;
    }
}
