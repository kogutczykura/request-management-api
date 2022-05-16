package com.application.service;

import com.application.data.Request;
import com.application.data.RequestDto;

public class RequestMapper {
    public static Request fromDto(RequestDto requestDto, Request request) {
        request.setId(requestDto.getId());
        request.setName(requestDto.getName());
        if (!(request.getValue() != null && requestDto.getValue() == null)) {
            request.setValue(requestDto.getValue());
        }
        request.setReasonRejection(requestDto.getReasonRejection());
        if (!(request.getState() != null && requestDto.getState() == null)) {
            request.setState(requestDto.getState());
        }
        return request;
    }

    public static RequestDto toDto(Request request) {
        RequestDto requestDto = new RequestDto();
        requestDto.setId(request.getId());
        requestDto.setName(request.getName());
        requestDto.setReasonRejection(request.getReasonRejection());
        requestDto.setValue(request.getValue());
        requestDto.setState(request.getState());
        return requestDto;
    }
}
