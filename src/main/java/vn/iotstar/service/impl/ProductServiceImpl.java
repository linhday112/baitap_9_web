package vn.iotstar.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.iotstar.dto.ProductDTO;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.User;
import vn.iotstar.mapper.ProductMapper;
import vn.iotstar.repository.ProductRepository;
import vn.iotstar.repository.UserRepository;
import vn.iotstar.service.FileUploadService;
import vn.iotstar.service.ProductService;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductMapper productMapper;
    private final FileUploadService fileUploadService;

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(String keyword, Pageable pageable) {
        Page<Product> products;
        if (keyword != null && !keyword.trim().isEmpty()) {
            products = productRepository.findByNameContainingIgnoreCaseOrBrandContainingIgnoreCaseOrMadeinContainingIgnoreCase(
                    keyword.trim(), keyword.trim(), keyword.trim(), pageable);
        } else {
            products = productRepository.findAll(pageable);
        }
        return products.map(productMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + id));
        return productMapper.toDto(product);
    }

    @Override
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) throws IOException {
        Product product = productMapper.toEntity(productDTO);
        product.setCreatedAt(LocalDateTime.now());

        if (productDTO.getImageFile() != null && !productDTO.getImageFile().isEmpty()) {
            String imagePath = fileUploadService.saveFile(productDTO.getImageFile());
            product.setImages(imagePath);
        }

        if (productDTO.getUserId() != null) {
            User user = userRepository.findById(productDTO.getUserId()).orElse(null);
            product.setUser(user);
        }

        Product savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) throws IOException {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy sản phẩm với ID: " + id));

        product.setName(productDTO.getName());
        product.setBrand(productDTO.getBrand());
        product.setMadein(productDTO.getMadein());
        product.setPrice(productDTO.getPrice());

        if (productDTO.getImageFile() != null && !productDTO.getImageFile().isEmpty()) {
            String imagePath = fileUploadService.saveFile(productDTO.getImageFile());
            product.setImages(imagePath);
        }

        Product updatedProduct = productRepository.save(product);
        return productMapper.toDto(updatedProduct);
    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public long countTotalProducts() {
        return productRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public long countProductsByUserId(Long userId) {
        return productRepository.countByUserId(userId);
    }
}
