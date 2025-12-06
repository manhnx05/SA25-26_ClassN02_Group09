package com.bkap.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bkap.entity.Product;
import com.bkap.services.ProductService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

	@Autowired
	private ProductService productService;

	@PostMapping("/add/{id}")
	public ResponseEntity<?> addToCart(@PathVariable Long id, HttpSession session) {
		Cart cart = (Cart) session.getAttribute("cart");
		if (cart == null) {
			cart = new Cart();
		}

		Optional<Product> optionalProduct = productService.findById(id);
		if (optionalProduct.isPresent()) {
			Product product = optionalProduct.get();
			cart.addItem(product);
			session.setAttribute("cart", cart);
		}

		Map<String, Object> response = new HashMap<>();
		response.put("totalItems", cart.getTotalItems());

		return ResponseEntity.ok(response);
	}
}
