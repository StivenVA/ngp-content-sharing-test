package com.stivenva.contentsharingtest.domain.port;

public interface StorageService {

    String upload(byte[] bytes, String fileName, String contentType);
    byte[] download(String fileName);
    void delete(String fileName);
}
