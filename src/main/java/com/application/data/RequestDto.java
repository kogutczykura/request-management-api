package com.application.data;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RequestDto {
    private Long id;
    private String name;
    private String value;
    private RequestState state;
    private String reasonRejection;
    private Long publishedNumber;
}
