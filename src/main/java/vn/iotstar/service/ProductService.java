package vn.iotstar.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.iotstar.dto.ProductDTO;

import java.io.IOException;

public interface ProductService {

    Page<ProductDTO> findAll(String keyword, Pageable pageable);

    ProductDTO findById(Long id);

    ProductDTO createProduct(ProductDTO productDTO) throws IOException;

    ProductDTO updateProduct(Long id, ProductDTO productDTO) throws IOException;

    void deleteProduct(Long id);

    long countTotalProducts();

    long countProductsByUserId(Long userId);
}
