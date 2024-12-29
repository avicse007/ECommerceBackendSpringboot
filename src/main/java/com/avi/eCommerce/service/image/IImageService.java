package com.avi.eCommerce.service.image;

import com.avi.eCommerce.dto.ImageDto;
import com.avi.eCommerce.model.Image;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IImageService {
    Image getImageById(Long id);
    void deleteImageById(Long id);
    List<ImageDto> saveImages(List<MultipartFile> files, Long productId);
    Image updateImage(MultipartFile file, Long imageId);

}
