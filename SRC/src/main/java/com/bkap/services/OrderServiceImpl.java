package com.bkap.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.bkap.entity.Customer;
import com.bkap.entity.Orders;
import com.bkap.entity.Review;
import com.bkap.repository.OrderRepository;

@Service
public class OrderServiceImpl implements OrderService {

	@Autowired
	private OrderRepository orderRepository;

	@Override
	public List<Orders> findAll() {
		return orderRepository.findAll();
	}

	@Override
	public Orders findById(Long id) {
		return orderRepository.findById(id).orElse(null);
	}

	@Override
	public Orders save(Orders order) {
		return orderRepository.save(order);
	}

	@Override
	public void deleteById(Long id) {
		orderRepository.deleteById(id);
	}

	@Override
	public List<Orders> searchOrders(String keyword, Integer status) {
		return orderRepository.searchOrders(keyword, status);
	}

	@Override
	public List<Orders> getOrderByUser(Customer customer) {
		// TODO Auto-generated method stub
		return orderRepository.findByCustomer(customer);
	}

	@Override
	public List<Orders> getOrdersByUserAndStatus(Customer customer, Integer status) {
		// TODO Auto-generated method stub
		return orderRepository.findByCustomerAndStatus(customer, status);
	}

	@Override
	public long countTodayOrders() {
		// TODO Auto-generated method stub
		return orderRepository.countTodayOrders();
	}

	@Override
	public double getTodayRevenue() {
		Double revenue = orderRepository.getTodayRevenue();
		return revenue != null ? revenue : 0;
	}

	@Override
	public void updateStatus(Long id, int status) {
		Orders order = orderRepository.findById(id).orElse(null);
		if (order != null) {
			order.setStatus(status);
			orderRepository.save(order);
		} else {
			throw new RuntimeException("Không tìm thấy đơn hàng với ID: " + id);
		}
	}

	@Override
	public long countByStatus(int status) {
		// TODO Auto-generated method stub
		return orderRepository.countByStatus(status);
	}

	@Override
	public Page<Orders> findByFilter(Integer status, String keyword, Pageable pageable) {
		if (status != null && keyword != null && !keyword.trim().isEmpty()) {
			return orderRepository.findByStatusAndKeyword(status, keyword.trim(), pageable);
		} else if (status != null) {
			return orderRepository.findByStatus(status, pageable);
		} else if (keyword != null && !keyword.trim().isEmpty()) {
			return orderRepository.findByKeyword(keyword.trim(), pageable);
		} else {
			return orderRepository.findAll(pageable);
		}
	}

	@Override
	public List<Orders> findByCustomerId(Long customerId) {
		// TODO Auto-generated method stub
		return orderRepository.findByCustomerId(customerId);
	}

}
