package com.avi.eCommerce.service.product;

import com.avi.eCommerce.dto.ProductDto;
import com.avi.eCommerce.exceptions.ProductNotFoundException;
import com.avi.eCommerce.model.Category;
import com.avi.eCommerce.model.Product;
import com.avi.eCommerce.request.AddProductRequest;
import com.avi.eCommerce.request.ProductUpdateRequest;

import java.util.List;

public interface IProductService {
    Product addProduct(AddProductRequest product);
    Product getProductById(Long id) throws ProductNotFoundException;
    void deleteProductById(Long id);
    Product updateProduct(ProductUpdateRequest product, Long productId) throws ProductNotFoundException;
    List<Product> getAllProducts();
    List<Product> getProductByCategoryName(String categoryName);
    List<Product> getProductByBrand(String brand);
    List<Product> getProductByCategoryNameAndBrand(String categoryName,String brand);
    List<Product> getProductByName(String name);
    List<Product> getProductByBrandAndName(String brand,String name);
    Long countProductsByBrandAndName(String brand,String name);

    ProductDto convertToProductDto(Product product);

    List<ProductDto> getConvertedProducts(List<Product> products);
}
