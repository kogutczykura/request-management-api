package com.application.data;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
public class RequestStateHistory {

    @Id
    @GeneratedValue
    private Long id;
    @ManyToOne
    private Request request;
    @Enumerated(EnumType.STRING)
    private RequestState requestState;
}
