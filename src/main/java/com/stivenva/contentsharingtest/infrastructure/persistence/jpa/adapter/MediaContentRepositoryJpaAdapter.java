package com.stivenva.contentsharingtest.infrastructure.persistence.jpa.adapter;

import com.stivenva.contentsharingtest.domain.model.Category;
import com.stivenva.contentsharingtest.domain.model.MediaContent;
import com.stivenva.contentsharingtest.domain.model.PageResult;
import com.stivenva.contentsharingtest.infrastructure.mapper.MediaContentMapper;
import com.stivenva.contentsharingtest.infrastructure.persistence.jpa.model.MediaContentEntity;
import com.stivenva.contentsharingtest.infrastructure.persistence.jpa.repository.MediaContentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MediaContentRepositoryJpaAdapter implements com.stivenva.contentsharingtest.domain.port.repository.MediaContentRepository {

    private final MediaContentRepository jpaRepository;

    @Override
    public Optional<MediaContent> findById(long id) {
        return jpaRepository.findById(id).map(MediaContentMapper::toDomain);
    }

    @Override
    public MediaContent save(MediaContent mediaContent) {
        MediaContentEntity entity = MediaContentMapper.toEntity(mediaContent);
        MediaContentEntity saved = jpaRepository.save(entity);
        return MediaContentMapper.toDomain(saved);
    }

    @Override
    public void delete(MediaContent mediaContent) {
        jpaRepository.deleteById(mediaContent.id());
    }

    @Override
    public void deleteById(long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public List<MediaContent> findAll() {
        return jpaRepository.findAll().stream().map(MediaContentMapper::toDomain).toList();
    }

    @Override
    public List<MediaContent> findByUserId(long userId) {
        return jpaRepository.findByUserId(userId).stream().map(MediaContentMapper::toDomain).toList();
    }

    @Override
    public PageResult<MediaContent> filter(String title, String description, Category category, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
        Specification<MediaContentEntity> spec = Specification.allOf();

        if (title != null && !title.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
        }
        if (description != null && !description.isBlank()) {
            spec = spec.and((root, q, cb) -> cb.like(cb.lower(root.get("description")), "%" + description.toLowerCase() + "%"));
        }
        if (category != null) {
            spec = spec.and((root, q, cb) -> cb.equal(root.get("category"), category));
        }

        Page<MediaContentEntity> result = jpaRepository.findAll(spec, pageable);
        List<MediaContent> items = result.getContent().stream().map(MediaContentMapper::toDomain).toList();
        return new PageResult<>(items, result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }


}
