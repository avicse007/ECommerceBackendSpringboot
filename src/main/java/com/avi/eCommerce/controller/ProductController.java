package com.avi.eCommerce.controller;

import com.avi.eCommerce.dto.ProductDto;
import com.avi.eCommerce.model.Product;
import com.avi.eCommerce.request.AddProductRequest;
import com.avi.eCommerce.request.ProductUpdateRequest;
import com.avi.eCommerce.response.ApiResponse;
import com.avi.eCommerce.service.product.IProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("${api.prefix}/product")
public class ProductController {

    private final IProductService productService;

    public ProductController(IProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllProduct(){
        List<Product> products = productService.getAllProducts();
        List<ProductDto> productDtos = productService.getConvertedProducts(products);
        return ResponseEntity.ok().body(new ApiResponse("Products fetched successfully", productDtos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse> getProductById(@PathVariable Long id){
        try {
            Product product = productService.getProductById(id);
            ProductDto productDto = productService.convertToProductDto(product);
            return ResponseEntity.ok().body(new ApiResponse("Product fetched successfully", productDto));
        } catch (Exception e) {
            return ResponseEntity.status(NOT_FOUND).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add")
    public ResponseEntity<ApiResponse> addProduct(@RequestBody AddProductRequest product) {
        try {
            Product newProduct = productService.addProduct(product);
            return ResponseEntity.ok().body(new ApiResponse("Product added successfully", newProduct));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateProduct(@RequestBody ProductUpdateRequest product, @PathVariable Long id) {
        try {
            Product updatedProduct = productService.updateProduct(product, id);
            ProductDto productDtos = productService.convertToProductDto(updatedProduct);
            return ResponseEntity.ok().body(new ApiResponse("Product updated successfully", productDtos));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProductById(id);
            return ResponseEntity.ok().body(new ApiResponse("Product deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }
    @GetMapping("category-name/{categoryName}")
    public ResponseEntity<ApiResponse> getProductByCategoryName(@PathVariable String categoryName) {
        try {
            List<Product> products = productService.getProductByCategoryName(categoryName);
            List<ProductDto> productDtos = productService.getConvertedProducts(products);
            return ResponseEntity.ok().body(new ApiResponse("Product fetched successfully", productDtos));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }
    @GetMapping("brand-name/{brandName}")
    public ResponseEntity<ApiResponse> getProductByBrand(@PathVariable String brandName) {
        try {
            List<Product> products = productService.getProductByBrand(brandName);
            List<ProductDto> productDtos = productService.getConvertedProducts(products);
            return ResponseEntity.ok().body(new ApiResponse("Product fetched successfully", productDtos));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }
    @GetMapping("category-name-brand/{categoryName}/{brandName}")
    public ResponseEntity<ApiResponse> getProductByCategoryNameAndBrand(@PathVariable String categoryName,@PathVariable String brandName) {
        try {
            List<Product> products = productService.getProductByCategoryNameAndBrand(categoryName,brandName);
            List<ProductDto> productDtos = productService.getConvertedProducts(products);
            return ResponseEntity.ok().body(new ApiResponse("Product fetched successfully", productDtos));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }
    @GetMapping("name/{name}")
    public ResponseEntity<ApiResponse> getProductByName(@PathVariable String name) {
        try {
            List<Product> products = productService.getProductByName(name);
            List<ProductDto> productDtos = productService.getConvertedProducts(products);
            return ResponseEntity.ok().body(new ApiResponse("Product fetched successfully", productDtos));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }
    @GetMapping("brand-name/{brandName}/name/{name}")
    public ResponseEntity<ApiResponse> getProductByBrandAndName(@PathVariable String brandName,@PathVariable String name) {
        try {
            List<Product> products = productService.getProductByBrandAndName(brandName,name);
            List<ProductDto> productDtos = productService.getConvertedProducts(products);
            return ResponseEntity.ok().body(new ApiResponse("Product fetched successfully", productDtos));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }
    @GetMapping("brand-name/{brandName}/name/{name}/count")
    public ResponseEntity<ApiResponse> countProductsByBrandAndName(@PathVariable String brandName,@PathVariable String name) {
        try {
            Long count = productService.countProductsByBrandAndName(brandName,name);
            return ResponseEntity.ok().body(new ApiResponse("Product count fetched successfully", count));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse(e.getMessage(), null));
        }
    }


}
