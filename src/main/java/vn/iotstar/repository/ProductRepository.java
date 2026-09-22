package vn.iotstar.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.iotstar.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByNameContainingIgnoreCaseOrBrandContainingIgnoreCaseOrMadeinContainingIgnoreCase(
            String name, String brand, String madein, Pageable pageable);

    long countByUserId(Long userId);

    Page<Product> findByUserId(Long userId, Pageable pageable);
}
