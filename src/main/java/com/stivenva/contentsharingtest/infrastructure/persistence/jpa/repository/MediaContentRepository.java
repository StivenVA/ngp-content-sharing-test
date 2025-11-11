package com.stivenva.contentsharingtest.infrastructure.persistence.jpa.repository;

import com.stivenva.contentsharingtest.infrastructure.persistence.jpa.model.MediaContentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MediaContentRepository extends JpaRepository<MediaContentEntity, Long>, JpaSpecificationExecutor<MediaContentEntity> {

    @Query("SELECT mc FROM MediaContentEntity mc WHERE mc.userId.id = ?1")
    List<MediaContentEntity> findByUserId(Long userId);
}
