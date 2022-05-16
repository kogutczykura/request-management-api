package com.application.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.application.data.Request;
import com.application.data.RequestState;


public interface RequestRepository extends JpaRepository<Request, Long> {
    Page<Request> findAllByName(String name, Pageable pageable);

    Page<Request> findAllByState(RequestState requestState, Pageable pageable);

    Page<Request> findAllByStateAndName(RequestState requestState, String name, Pageable pageable);
}
