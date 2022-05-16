package com.application.data;

import lombok.*;
import org.hibernate.validator.constraints.UniqueElements;

import javax.persistence.*;
import java.util.List;

@Getter
@Setter
@Entity
@Builder
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String value;
    @Enumerated(EnumType.STRING)
    private RequestState state;
    private String reasonRejection;
    @OneToOne(cascade = CascadeType.ALL)
    private PublishedRequest publishedNumber;
    @OneToMany(mappedBy = "request", cascade = CascadeType.ALL)
    private List<RequestStateHistory> requestStateHistories;
}
