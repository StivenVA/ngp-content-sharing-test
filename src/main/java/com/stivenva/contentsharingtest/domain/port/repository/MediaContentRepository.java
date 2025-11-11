package com.stivenva.contentsharingtest.domain.port.repository;

import com.stivenva.contentsharingtest.domain.model.Category;
import com.stivenva.contentsharingtest.domain.model.MediaContent;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface MediaContentRepository {

    Optional<MediaContent> findById(long id);
    MediaContent save(MediaContent mediaContent);
    void delete(MediaContent mediaContent);
    void deleteById(long id);
    List<MediaContent> findAll();
    List<MediaContent> findByUserId(long userId);

    Page<MediaContent> filter(String title, String description, Category category, int page, int size);
}
