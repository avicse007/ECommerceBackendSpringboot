package com.avi.eCommerce.service.product;

import com.avi.eCommerce.dto.ImageDto;
import com.avi.eCommerce.dto.ProductDto;
import com.avi.eCommerce.exceptions.ProductNotFoundException;
import com.avi.eCommerce.model.Category;
import com.avi.eCommerce.model.Image;
import com.avi.eCommerce.model.Product;
import com.avi.eCommerce.repository.CategoryRepository;
import com.avi.eCommerce.repository.ImageRepository;
import com.avi.eCommerce.repository.ProductRepository;
import com.avi.eCommerce.request.AddProductRequest;
import com.avi.eCommerce.request.ProductUpdateRequest;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService implements IProductService{
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final ImageRepository imageRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, ModelMapper modelMapper, ImageRepository imageRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
        this.imageRepository = imageRepository;
    }



    private Product createProduct(AddProductRequest req, Category category){
        return new Product(
                req.getName(),
                req.getBrand(),
                req.getProductDescription(),
                req.getPrice(),
                req.getInventory(),
                category
        );
    }

    @Override
    public Product addProduct(AddProductRequest product) {
        Category category = Optional.ofNullable(categoryRepository.findByName(product.getCategory().getName()))
                                    .orElseGet(()->{
                                        Category newCategory =  new Category(product.getCategory().getName());
                                        return categoryRepository.save(newCategory);
                                    });
        product.setCategory(category);
        return productRepository.save(createProduct(product,category));
    }

    @Override
    public Product getProductById(Long id) throws ProductNotFoundException {
        return productRepository.findById(id)
                       .orElseThrow(() -> new ProductNotFoundException("Product not found"));
    }

    @Override
    public void deleteProductById(Long id) {
        productRepository.findById(id)
                .ifPresentOrElse(productRepository::delete,
                        ()-> {
                            try {
                                throw new ProductNotFoundException("Product not found");
                            } catch (ProductNotFoundException e) {
                                throw new RuntimeException(e);
                            }
                        });
    }

    private Product updateExistingProduct(Product existingProduct, ProductUpdateRequest request){
        existingProduct.setName(request.getName());
        existingProduct.setBrand(request.getBrand());
        existingProduct.setProductDescription(request.getProductDescription());
        existingProduct.setInventory(request.getInventory());
        existingProduct.setPrice(request.getPrice());
        Category category =  categoryRepository.findByName(request.getCategory().getName());
        existingProduct.setCategory(category);
        return  existingProduct;
    }

    @Override
    public Product updateProduct(ProductUpdateRequest product, Long productId) throws ProductNotFoundException {

        return productRepository.findById(productId)
        .map(existingProduct ->updateExistingProduct(existingProduct,product))
                       .map(productRepository::save)
                       .orElseThrow(()-> new ProductNotFoundException("Product Not found "));
    }

    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getProductByCategoryName(String categoryName) {
        return productRepository.findByCategoryName(categoryName);
    }

    @Override
    public List<Product> getProductByBrand(String brand) {
        return productRepository.findByBrand(brand);
    }

    @Override
    public List<Product> getProductByCategoryNameAndBrand(String categoryName, String brand) {
        return productRepository.findByCategoryNameAndBrand(categoryName,brand);
    }

    @Override
    public List<Product> getProductByName(String name) {
        return productRepository.findByName(name);
    }

    @Override
    public List<Product> getProductByBrandAndName(String brand, String name) {
        return productRepository.findByBrandAndName(brand,name);
    }

    @Override
    public Long countProductsByBrandAndName(String brand, String name) {
        return productRepository.countByBrandAndName(brand,name);
    }

    @Override
    public ProductDto convertToProductDto(Product product){
        ProductDto productDto =  modelMapper.map(product,ProductDto.class);
        List<Image> images = imageRepository.findByProductId(productDto.getId());
        List<ImageDto> imageDtos = images.stream().map(image -> modelMapper.map(image,ImageDto.class)).toList();
        productDto.setImages(imageDtos);
        return productDto;
    }

    @Override
    public List<ProductDto> getConvertedProducts(List<Product> products){
        return products.stream().map(this::convertToProductDto).toList();
    }
}
