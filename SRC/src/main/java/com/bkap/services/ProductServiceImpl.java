package com.bkap.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.bkap.entity.Category;
import com.bkap.entity.Product;
import com.bkap.repository.ProductRepository;

@Service
public class ProductServiceImpl implements ProductService {
	@Autowired
	private ProductRepository productRepository;

	@Override
	public List<Product> getAll() {
		// TODO Auto-generated method stub
		return this.productRepository.findAll();
	}

	@Override
	public Boolean create(Product product) {
		try {
			this.productRepository.save(product);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Optional<Product> findById(long id) {
		return productRepository.findById(id);
	}

	@Override
	public Boolean update(Product product) {
		try {
			this.productRepository.save(product);
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public Boolean delete(Long id) {
		try {
			Product product = findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
			this.productRepository.delete(product);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public List<Product> searchProduct(String keyword) {
		// TODO Auto-generated method stub
		return this.productRepository.searchProduct(keyword);
	}

	@Override
	public Page<Product> getAll(Integer pageNo) {
		// TODO Auto-generated method stub
		Pageable pageable = PageRequest.of(pageNo - 1, 2);
		return this.productRepository.findAll(pageable);
	}

	@Override
	public Page<Product> searchCategory(String keyword, Integer pageNo) {
		List list = this.searchProduct(keyword);

		Pageable pageable = PageRequest.of(pageNo - 1, 2);

		Integer start = (int) pageable.getOffset();
		Integer end = (int) ((pageable.getOffset() + pageable.getPageSize()) > list.size() ? list.size()
				: pageable.getOffset() + pageable.getPageSize());

		list = list.subList(start, end);
		return new PageImpl<Product>(list, pageable, this.searchProduct(keyword).size());
	}

	@Override
	public List<Product> getByCategoryName(String categoryName) {
		// TODO Auto-generated method stub
		return productRepository.findByCategory_NameIgnoreCase(categoryName);
	}

	@Override
	public Page<Product> searchByNameOrCategory(String keyword, Integer pageNo) {
		Pageable pageable = PageRequest.of(pageNo - 1, 2);
		return this.productRepository.searchByNameOrCategory(keyword, pageable);
	}

	@Override
	public Page<Product> findLaptopsByBrands(List<String> brands, int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize); // KHÔNG trừ -1 nữa
		return productRepository.findByCategory_NameIgnoreCaseAndBrandIn("laptops", brands, pageable);
	}

	@Override
	public Page<Product> getLaptopsByPage(int pageNo, int pageSize) {
		// pageNo đã là pageIndex (tức là đã -1 từ controller)
		Pageable pageable = PageRequest.of(pageNo, pageSize); // KHÔNG trừ -1 nữa
		return productRepository.findByCategory_NameIgnoreCase("laptops", pageable);
	}

	@Override
	public List<Product> findTop3Latest() {
		Pageable pageable = PageRequest.of(0, 3);
		return productRepository.findTop3LatestLaptops(pageable);
	}

	@Override
	public Page<Product> findLaptopsByBrandsWithPageable(List<String> brands, Pageable pageable) {
		return productRepository.findByCategory_NameIgnoreCaseAndBrandIn("laptops", brands, pageable);
	}

	@Override
	public Page<Product> findLaptopsWithPageable(Pageable pageable) {
		return productRepository.findByCategory_NameIgnoreCase("laptops", pageable);
	}

	@Override
	public Page<Product> findByCategoryIdWithPageable(int categoryId, Pageable pageable) {
		return productRepository.findByCategoryId(categoryId, pageable);
	}

	@Override
	public Page<Product> findSmartphonesWithPageable(Pageable pageable) {
		return productRepository.findByCategory_NameIgnoreCase("smartphones", pageable);
	}

	@Override
	public Page<Product> findSmartphonesByBrandsWithPageable(List<String> brands, Pageable pageable) {
		return productRepository.findByCategory_NameIgnoreCaseAndBrandIn("smartphones", brands, pageable);
	}

	@Override
	public List<Product> findTop3LatestByCategory(String categoryName) {
		// TODO Auto-generated method stub
		return productRepository.findTop3ByCategory_NameIgnoreCaseOrderByIdDesc(categoryName);
	}

	@Override
	public Page<Product> findSmartphonesByBrands(List<String> brands, int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize); // KHÔNG trừ -1 nữa
		return productRepository.findByCategory_NameIgnoreCaseAndBrandIn("smartphones", brands, pageable);
	}

	@Override
	public List<Product> findByCategoryAndBrands(String category, List<String> brands) {
		// TODO Auto-generated method stub
		return productRepository.findByCategory_NameIgnoreCaseAndBrandIn(category, brands);
	}

	// Cameras
	@Override
	public Page<Product> findCamerasWithPageable(Pageable pageable) {
		// TODO Auto-generated method stub
		return productRepository.findByCategory_NameIgnoreCase("cameras", pageable);
	}

	@Override
	public Page<Product> findCamerasByBrandsWithPageable(List<String> brands, Pageable pageable) {
		// TODO Auto-generated method stub
		return productRepository.findByCategory_NameIgnoreCaseAndBrandIn("cameras", brands, pageable);
	}

	@Override
	public List<Product> findTop3LatestCamerasByCategory(String categoryName) {
		// TODO Auto-generated method stub
		return productRepository.findTop3ByCategory_NameIgnoreCaseOrderByIdDesc(categoryName);
	}

	@Override
	public Page<Product> findCamerasByBrands(List<String> brands, int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize); // KHÔNG trừ -1 nữa
		return productRepository.findByCategory_NameIgnoreCaseAndBrandIn("cameras", brands, pageable);
	}

	@Override
	public List<Product> findCamerasByBrands(String category, List<String> brands) {
		// TODO Auto-generated method stub
		return productRepository.findByCategory_NameIgnoreCaseAndBrandIn(category, brands);
	}

	// Accessories
	@Override
	public Page<Product> findAccessoriesWithPageable(Pageable pageable) {
		// TODO Auto-generated method stub
		return productRepository.findByCategory_NameIgnoreCase("accessories", pageable);
	}

	@Override
	public Page<Product> findAccessoriesByBrandsWithPageable(List<String> brands, Pageable pageable) {
		// TODO Auto-generated method stub
		return productRepository.findByCategory_NameIgnoreCaseAndBrandIn("accessories", brands, pageable);
	}

	@Override
	public List<Product> findTop3LatestAccessoriesByCategory(String categoryName) {
		// TODO Auto-generated method stub
		return productRepository.findTop3ByCategory_NameIgnoreCaseOrderByIdDesc(categoryName);
	}

	@Override
	public Page<Product> findAccessoriesByBrands(List<String> brands, int pageNo, int pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize); // KHÔNG trừ -1 nữa
		return productRepository.findByCategory_NameIgnoreCaseAndBrandIn("accessories", brands, pageable);
	}

	@Override
	public List<Product> findAccessoriesByBrands(String category, List<String> brands) {
		// TODO Auto-generated method stub
		return productRepository.findByCategory_NameIgnoreCaseAndBrandIn(category, brands);
	}

	// Dashboard
	@Override
	public long countAll() {
		// TODO Auto-generated method stub
		return productRepository.count();
	}

	// Hot Deal
	@Override
	public List<Product> findAll() {
		// TODO Auto-generated method stub
		return productRepository.findAll();
	}

	@Override
	public List<Product> findRelatedProducts(Long categoryId, Long excludeId) {
		// TODO Auto-generated method stub
		return productRepository.findTop4ByCategoryIdAndIdNot(categoryId, excludeId);
	}
}
