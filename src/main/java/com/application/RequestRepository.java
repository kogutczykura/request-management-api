package com.application;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RequestRepository extends JpaRepository<Request, Long> {

 //   Page<Request> findAllWithFilters();
}
