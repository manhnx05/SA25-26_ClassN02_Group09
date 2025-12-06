package com.bkap.controller.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bkap.dto.OrderViewModel;
import com.bkap.entity.Orders;
import com.bkap.services.OrderService;

@Controller
@RequestMapping("/admin/order")
public class OrderController {

	@Autowired
	private OrderService orderService;

	// Xem chi tiết đơn hàng
	@GetMapping("/{id}")
	public String viewOrder(@PathVariable Long id, Model model) {
		Orders order = orderService.findById(id);
		if (order == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng");
		}

		OrderViewModel wrapper = new OrderViewModel(order);

		model.addAttribute("order", order);
		model.addAttribute("wrapper", wrapper);
		return "admin/order/detail";
	}

	// Duyệt đơn hàng (từ trạng thái 1 → 2)
	@GetMapping("/confirm/{id}")
	public String confirm(@PathVariable Long id, RedirectAttributes ra) {
		orderService.updateStatus(id, 2);
		ra.addFlashAttribute("message", "Đơn hàng đã được xác nhận.");
		return "redirect:/admin/order";
	}

	// Hủy đơn hàng (từ trạng thái 1 hoặc 7 → 5)
	@GetMapping("/cancel/{id}")
	public String cancel(@PathVariable Long id, RedirectAttributes ra) {
		orderService.updateStatus(id, 5);
		ra.addFlashAttribute("message", "Đơn hàng đã bị hủy.");
		return "redirect:/admin/order";
	}

	// Cập nhật trạng thái tùy ý (3: Đang chuyển, 4: Đang giao, 6: Thành công,...)
	@GetMapping("/updateStatus/{id}/{status}")
	public String updateStatus(@PathVariable Long id, @PathVariable int status, RedirectAttributes ra) {
		if (status < 1 || status > 7) {
			ra.addFlashAttribute("error", "Trạng thái không hợp lệ.");
			return "redirect:/admin/order";
		}

		orderService.updateStatus(id, status);
		ra.addFlashAttribute("message", "Cập nhật trạng thái thành công.");
		return "redirect:/admin/order";
	}
}
