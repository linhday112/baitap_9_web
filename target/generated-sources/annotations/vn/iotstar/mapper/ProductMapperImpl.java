package vn.iotstar.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import vn.iotstar.dto.ProductDTO;
import vn.iotstar.entity.Product;
import vn.iotstar.entity.User;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-09-22T09:58:53+0700",
    comments = "version: 1.6.3, compiler: javac, environment: Java 22 (Oracle Corporation)"
)
@Component
public class ProductMapperImpl implements ProductMapper {

    @Override
    public ProductDTO toDto(Product entity) {
        if ( entity == null ) {
            return null;
        }

        ProductDTO.ProductDTOBuilder productDTO = ProductDTO.builder();

        productDTO.userId( entityUserId( entity ) );
        productDTO.userFullName( entityUserFullName( entity ) );
        productDTO.id( entity.getId() );
        productDTO.name( entity.getName() );
        productDTO.brand( entity.getBrand() );
        productDTO.madein( entity.getMadein() );
        productDTO.price( entity.getPrice() );
        productDTO.images( entity.getImages() );
        productDTO.createdAt( entity.getCreatedAt() );

        return productDTO.build();
    }

    @Override
    public Product toEntity(ProductDTO dto) {
        if ( dto == null ) {
            return null;
        }

        Product.ProductBuilder product = Product.builder();

        product.id( dto.getId() );
        product.name( dto.getName() );
        product.brand( dto.getBrand() );
        product.madein( dto.getMadein() );
        product.price( dto.getPrice() );
        product.images( dto.getImages() );
        product.createdAt( dto.getCreatedAt() );

        return product.build();
    }

    private Long entityUserId(Product product) {
        User user = product.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getId();
    }

    private String entityUserFullName(Product product) {
        User user = product.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getFullName();
    }
}
