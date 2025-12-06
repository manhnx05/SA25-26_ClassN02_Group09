package com.bkap.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bkap.entity.Customer;
import com.bkap.entity.Orders;
import com.bkap.entity.Review;

@Service
public interface OrderService {
	List<Orders> findAll();

	Orders findById(Long id);

	Orders save(Orders order);

	void deleteById(Long id);

	// Dashboard
	long countTodayOrders();

	double getTodayRevenue();

	// Tìm kiếm & lọc
	List<Orders> searchOrders(String keyword, Integer status);

	List<Orders> getOrderByUser(Customer customer);

	List<Orders> getOrdersByUserAndStatus(Customer customer, Integer status);

	// Order
	void updateStatus(Long id, int status);

	long countByStatus(int status);

	// ➕ Thêm mới
	Page<Orders> findByFilter(Integer status, String keyword, Pageable pageable);
	
	//Xem đơn hàng
	List<Orders> findByCustomerId(Long customerId);
	
}
