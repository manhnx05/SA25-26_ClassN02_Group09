package com.bkap.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bkap.entity.OrderDetail;



public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
    List<OrderDetail> findByOrderId(Long orderId);
    
}
