package com.avi.eCommerce.controller;

import com.avi.eCommerce.dto.ImageDto;
import com.avi.eCommerce.model.Image;
import com.avi.eCommerce.response.ApiResponse;
import com.avi.eCommerce.service.image.IImageService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.SQLException;
import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.ResponseEntity.ok;

@RestController()
@RequestMapping("${api.prefix}/images")
public class ImageController {
    private final IImageService imageService;

    public ImageController(IImageService imageService) {
        this.imageService = imageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> saveImages(@RequestParam List<MultipartFile> files, @RequestParam Long productId)
    {
        try{
            List<ImageDto> imageDtos = imageService.saveImages(files, productId);
            return ok(new ApiResponse( "Images uploaded successfully", imageDtos));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Upload fail", e.getMessage()));
        }
    }

    @GetMapping("/image/download/{imageId}")
    public ResponseEntity<ByteArrayResource> downloadImage(@PathVariable Long imageId) throws SQLException {
            Image image = imageService.getImageById(imageId);

        byte[] imageData = null;
        if (image.getImage() != null) {
            imageData = image.getImage().getBytes(1, (int) image.getImage().length());
        }

        // Create ByteArrayResource from the byte array
        ByteArrayResource resource = new ByteArrayResource(imageData);

        //ByteArrayResource resource = new ByteArrayResource(image.getImage().getBytes(1, (int) image.getImage().length()));
            return ResponseEntity.ok().contentType(MediaType.parseMediaType(image.getFileType()))
                           .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + image.getFileName() + "\"")
                            .body(resource);
    }
    @PutMapping("/image/update/{imageId}")
    public ResponseEntity<ApiResponse> updateImage(@PathVariable Long imageId, @RequestBody MultipartFile file)
    {
        try{
            Image imageDto = imageService.updateImage(file,imageId);
            return ok(new ApiResponse("Image updated successfully", imageDto));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Image update fail", e.getMessage()));
        }
    }

    @DeleteMapping("image/delete/{imageId}")
    public ResponseEntity<ApiResponse> deleteImage(@PathVariable Long imageId)
    {
        try{
            imageService.deleteImageById(imageId);
            return ok(new ApiResponse("Image deleted successfully", null));
        } catch (Exception e) {
            return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(new ApiResponse("Image delete fail", e.getMessage()));
        }
    }
 }
