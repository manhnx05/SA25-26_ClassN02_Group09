# Layered Architecture Implementation

## Tổng quan về dự án

Đây là một ứng dụng web e-commerce được xây dựng bằng Spring Boot, sử dụng kiến trúc phân lớp (Layered Architecture) để quản lý sản phẩm điện tử như laptop, smartphone, camera và phụ kiện.

## Kiến trúc sử dụng

### MVC Pattern (Model-View-Controller)
Dự án này **có sử dụng kiến trúc MVC** làm nền tảng:

- **Model**: `com.bkap.entity` (Product, Category, Orders, User...)
- **View**: Templates Thymeleaf trong `src/main/resources/templates/`
- **Controller**: `com.bkap.controller` (ProductController, UserWebController...)

### Layered Architecture (Mở rộng từ MVC)
Tuy nhiên, dự án được **mở rộng thành Layered Architecture** với 6 lớp chính để tăng tính module và maintainability:

### 1. **Presentation Layer (Lớp trình bày)**
- **Package**: `com.bkap.controller`
- **Chức năng**: Xử lý HTTP requests, tương tác với người dùng
- **Thành phần chính**:
  - `ProductController` - Quản lý sản phẩm (admin)
  - `UserWebController` - Giao diện người dùng
  - `DealsController` - Quản lý khuyến mãi
  - `CartController` (API) - Xử lý giỏ hàng

**Ví dụ**:
```java
@Controller
public class ProductController {
    @Autowired
    private ProductService productService;
    
    @GetMapping("admin/product")
    public String index(Model model, @Param("keyword") String keyword) {
        // Xử lý request và trả về view
    }
}
```

### 2. **Service Layer (Lớp dịch vụ)**
- **Package**: `com.bkap.services`
- **Chức năng**: Chứa business logic, xử lý các quy tắc nghiệp vụ
- **Pattern**: Interface + Implementation
- **Thành phần chính**:
  - `ProductService` & `ProductServiceImpl`
  - `CategoryService` & `CategoryServiceImpl`
  - `OrderService` & `OrderServiceImpl`
  - `UserService` & `UserServiceImpl`

**Ví dụ**:
```java
@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    
    @Override
    public Page<Product> searchByNameOrCategory(String keyword, Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 2);
        return productRepository.searchByNameOrCategory(keyword, pageable);
    }
}
```

### 3. **Repository Layer (Lớp truy cập dữ liệu)**
- **Package**: `com.bkap.repository`
- **Chức năng**: Truy cập và thao tác với cơ sở dữ liệu
- **Technology**: Spring Data JPA
- **Thành phần chính**:
  - `ProductRepository`
  - `CategoryRepository`
  - `OrderRepository`
  - `UserRepository`

**Ví dụ**:
```java
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategory_NameIgnoreCase(String categoryName);
    
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchProduct(@Param("keyword") String keyword);
}
```

### 4. **Entity Layer (Lớp thực thể)**
- **Package**: `com.bkap.entity`
- **Chức năng**: Định nghĩa các đối tượng domain, mapping với database
- **Technology**: JPA/Hibernate
- **Thành phần chính**:
  - `Product` - Sản phẩm
  - `Category` - Danh mục
  - `Orders` - Đơn hàng
  - `Customer` - Khách hàng
  - `User` - Người dùng

**Ví dụ**:
```java
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
}
```

### 5. **DTO Layer (Lớp truyền dữ liệu)**
- **Package**: `com.bkap.dto`
- **Chức năng**: Truyền dữ liệu giữa các lớp, API responses
- **Thành phần chính**:
  - `ProductDTO` - Dữ liệu sản phẩm cho API
  - `OrderViewModel` - Hiển thị đơn hàng
  - `ReviewViewModel` - Hiển thị đánh giá

### 6. **Configuration Layer (Lớp cấu hình)**
- **Package**: `com.bkap.config`
- **Chức năng**: Cấu hình ứng dụng, security, mail
- **Thành phần chính**:
  - `SecurityConfig` - Cấu hình bảo mật
  - `MailConfig` - Cấu hình email
  - `StaticResourceConfig` - Cấu hình tài nguyên tĩnh

## So sánh MVC vs Layered Architecture trong dự án

### MVC Pattern (Cơ bản)
```
Client → Controller → Model → Database
           ↓
         View
```

### Layered Architecture (Mở rộng)
```
Client → Controller → Service → Repository → Database
           ↓           ↓          ↓
         View ← DTO ← Entity ← Query Result
```

### Tại sao mở rộng từ MVC sang Layered?

**MVC thuần túy có hạn chế:**
- Controller có thể trở nên phức tạp với nhiều business logic
- Model trực tiếp tương tác với database
- Khó test và maintain khi ứng dụng lớn

**Layered Architecture giải quyết:**
- **Service Layer**: Tách business logic khỏi Controller
- **Repository Layer**: Tách data access logic khỏi Model
- **DTO Layer**: Tối ưu data transfer giữa các lớp

## Luồng xử lý dữ liệu

```
Client Request → Controller → Service → Repository → Database
                     ↓           ↓          ↓
                   View ← DTO ← Entity ← Query Result
```

### Ví dụ so sánh MVC vs Layered trong dự án:

#### Cách MVC thuần túy (không khuyến khích):
```java
@Controller
public class ProductController {
    @Autowired
    private ProductRepository productRepository; // Trực tiếp inject Repository
    
    @GetMapping("/products")
    public String getProducts(Model model) {
        // Business logic trực tiếp trong Controller
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        return "products"; // View
    }
}
```

#### Cách Layered Architecture (được sử dụng trong dự án):
```java
@Controller
public class ProductController {
    @Autowired
    private ProductService productService; // Inject Service, không phải Repository
    
    @GetMapping("/products")
    public String getProducts(Model model) {
        // Delegate business logic cho Service layer
        Page<Product> products = productService.getAll(1);
        model.addAttribute("products", products);
        return "products"; // View
    }
}

@Service
public class ProductServiceImpl implements ProductService {
    @Autowired
    private ProductRepository productRepository;
    
    @Override
    public Page<Product> getAll(Integer pageNo) {
        // Business logic được xử lý ở đây
        Pageable pageable = PageRequest.of(pageNo - 1, 2);
        return productRepository.findAll(pageable);
    }
}
```

### Ví dụ luồng xử lý tìm kiếm sản phẩm:

1. **Controller** nhận request tìm kiếm:
```java
@GetMapping("admin/product")
public String index(@Param("keyword") String keyword) {
    Page<Product> listProduct = productService.searchByNameOrCategory(keyword, pageNo);
}
```

2. **Service** xử lý business logic:
```java
public Page<Product> searchByNameOrCategory(String keyword, Integer pageNo) {
    Pageable pageable = PageRequest.of(pageNo - 1, 2);
    return productRepository.searchByNameOrCategory(keyword, pageable);
}
```

3. **Repository** thực hiện truy vấn:
```java
@Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
List<Product> searchProduct(@Param("keyword") String keyword);
```

## Công nghệ sử dụng

### Backend Framework
- **Spring Boot 3.5.0** - Framework chính
- **Spring Data JPA** - ORM và repository pattern
- **Spring Security** - Bảo mật và authentication
- **Spring Web MVC** - Web framework

### Database
- **Oracle Database** - Cơ sở dữ liệu chính
- **Hibernate** - ORM implementation
- **JDBC Driver** - Oracle JDBC 11

### Frontend
- **Thymeleaf** - Template engine
- **HTML/CSS/JavaScript** - Frontend technologies
- **Bootstrap** - UI framework

### Build Tool
- **Maven** - Dependency management và build tool

## Ưu điểm của Layered Architecture

### 1. **Separation of Concerns**
- Mỗi lớp có trách nhiệm riêng biệt
- Dễ dàng maintain và debug
- Code có tổ chức rõ ràng

### 2. **Reusability**
- Service layer có thể được sử dụng bởi nhiều controller
- Repository có thể được inject vào nhiều service

### 3. **Testability**
- Mỗi lớp có thể được test độc lập
- Dễ dàng mock dependencies

### 4. **Scalability**
- Dễ dàng thêm tính năng mới
- Có thể scale từng lớp riêng biệt

## Cấu trúc thư mục

```
src/main/java/com/bkap/
├── controller/          # Presentation Layer
│   ├── admin/          # Admin controllers
│   └── ...             # User controllers
├── services/           # Service Layer
├── repository/         # Repository Layer
├── entity/            # Entity Layer
├── dto/               # DTO Layer
├── config/            # Configuration Layer
├── security/          # Security components
└── model/             # Additional models
```

## Kết luận

Dự án này **sử dụng cả MVC và Layered Architecture**:

### MVC Pattern:
- **Model**: Entity classes (Product, Category, User...)
- **View**: Thymeleaf templates 
- **Controller**: HTTP request handlers

### Layered Architecture (Mở rộng từ MVC):
- **Presentation Layer**: Controllers + Views
- **Service Layer**: Business logic
- **Repository Layer**: Data access
- **Entity Layer**: Domain models
- **DTO Layer**: Data transfer objects
- **Configuration Layer**: App configuration

**Kết hợp này mang lại:**
- Cấu trúc MVC quen thuộc và dễ hiểu
- Tính module hóa cao từ Layered Architecture
- Separation of concerns tốt hơn
- Dễ test, maintain và scale
- Phù hợp cho ứng dụng enterprise

Đây là một **best practice** trong phát triển ứng dụng Spring Boot hiện đại.