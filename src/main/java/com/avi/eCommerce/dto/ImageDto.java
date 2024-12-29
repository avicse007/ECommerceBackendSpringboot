package com.avi.eCommerce.dto;

public class ImageDto {
    private Long imageId;
    private String imageName;
    private String downloadUrl;

    public Long getImageId() {
        return imageId;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public ImageDto() {
    }

    public ImageDto(Long imageId, String imageName, String downloadUrl) {
        this.imageId = imageId;
        this.imageName = imageName;
        this.downloadUrl = downloadUrl;
    }
}
