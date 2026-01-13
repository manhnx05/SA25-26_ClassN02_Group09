package com.bkap.controller.admin;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.bkap.dto.ProductDTO;
import com.bkap.entity.Category;
import com.bkap.entity.Product;
import com.bkap.services.CategoryService;
import com.bkap.services.FileValidationService;
import com.bkap.services.ProductService;
import com.bkap.services.StorageService;

@Controller
public class ProductController {
	@Autowired
	private StorageService storageService;

	@Autowired
	private ProductService productService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private FileValidationService fileValidationService;

	@GetMapping("admin/product")
	public String index(Model model, @Param("keyword") String keyword,
			@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) {

		Page<Product> listProduct;

		if (keyword != null && !keyword.isEmpty()) {
			listProduct = this.productService.searchByNameOrCategory(keyword, pageNo);
			model.addAttribute("keyword", keyword);
		} else {
			listProduct = this.productService.getAll(pageNo);
		}

		model.addAttribute("totalPage", listProduct.getTotalPages());
		model.addAttribute("currentPage", pageNo);
		model.addAttribute("listProduct", listProduct);
		return "admin/product/index";
	}

	@GetMapping("admin/add-product")
	public String add(Model model) {

		Product product = new Product();
		model.addAttribute("product", product);

		List<Category> listCate = this.categoryService.getAll();
		model.addAttribute("listCate", listCate);
		return "admin/product/add";
	}

	@PostMapping("admin/add-product")
	public String save(@ModelAttribute("product") Product product, @RequestParam("imageFile") MultipartFile file,
			RedirectAttributes redirect) {
		
		// Validate file upload
		if (!fileValidationService.isValidImageFile(file)) {
			String errorMessage = fileValidationService.getValidationMessage(file);
			redirect.addFlashAttribute("error", errorMessage);
			return "redirect:/admin/add-product";
		}
		
		// upload file
		String fileName = this.storageService.store(file);
		product.setImage(fileName);

		if (productService.create(product)) {
			redirect.addFlashAttribute("success", "Thêm sản phẩm thành công");
			return "redirect:/admin/product";
		} else {
			redirect.addFlashAttribute("error", "Thêm thất bại");
			return "redirect:/admin/add-product";
		}
	}

	@GetMapping("admin/edit-product/{id}")
	public String edit(Model model, @PathVariable("id") Long id) {
		Product product = this.productService.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));

		List<Category> listCate = this.categoryService.getAll();

		if (product.getCategory() == null) {
			product.setCategory(new Category());
		}

		model.addAttribute("product", product);
		model.addAttribute("listCate", listCate);
		return "admin/product/edit";
	}

	@PostMapping("admin/edit-product")
	public String update(@ModelAttribute("product") Product product, @RequestParam("imageFile") MultipartFile file,
			RedirectAttributes redirect) {

		Product oldProduct = productService.findById(product.getId())
				.orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

		// Validate file upload (cho phép file rỗng khi update)
		if (!fileValidationService.isValidImageFileOptional(file)) {
			String errorMessage = fileValidationService.getValidationMessage(file);
			redirect.addFlashAttribute("error", errorMessage);
			return "redirect:/admin/edit-product/" + product.getId();
		}

		// Nếu có file mới → lưu file
		if (!file.isEmpty()) {
			String filename = storageService.store(file);
			product.setImage(filename);
		} else {
			// Không upload ảnh mới → giữ lại ảnh cũ
			product.setImage(oldProduct.getImage());
		}

		if (productService.update(product)) {
			redirect.addFlashAttribute("success", "Cập nhật sản phẩm thành công");
			return "redirect:/admin/product";
		} else {
			redirect.addFlashAttribute("error", "Cập nhật thất bại");
			return "redirect:/admin/edit-product/" + product.getId();
		}
	}

	@GetMapping("admin/delete-product/{id}")
	public String delete(@PathVariable("id") Long id, RedirectAttributes redirect) {
		if (productService.delete(id)) {
			redirect.addFlashAttribute("success", "Xóa sản phẩm thành công");
		} else {
			redirect.addFlashAttribute("error", "Xóa sản phẩm thất bại");
		}
		return "redirect:/admin/product";
	}

	@GetMapping("/api/admin/product/{id}")
	@ResponseBody
	public ResponseEntity<?> getProductById(@PathVariable("id") Long id) {
		Optional<Product> productOpt = productService.findById(id);
		if (productOpt.isPresent()) {
			Product p = productOpt.get();
			return ResponseEntity.ok(new ProductDTO(p));
		} else {
			return ResponseEntity.status(404).body("{\"message\": \"Không tìm thấy sản phẩm\"}");
		}
	}
}
