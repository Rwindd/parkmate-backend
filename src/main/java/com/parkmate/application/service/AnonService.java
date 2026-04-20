package com.parkmate.application.service;

import com.parkmate.adapter.inbound.rest.dto.response.AnonPostResponse;
import com.parkmate.application.mapper.AnonMapper;
import com.parkmate.domain.exception.EventNotFoundException;
import com.parkmate.domain.port.AnonPostRepositoryPort;
import com.parkmate.infrastructure.persistence.entity.AnonPostEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class AnonService {

    private final AnonPostRepositoryPort anonRepo;
    private final AnonMapper anonMapper;

    @Transactional(readOnly = true)
    public List<AnonPostResponse> getAll() {
        return anonRepo.findAllOrderedByDateDesc().stream()
            .map(anonMapper::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public AnonPostResponse create(String text) {
        AnonPostEntity entity = AnonPostEntity.builder().text(text).build();
        return anonMapper.toResponse(anonRepo.save(entity));
    }

    @Transactional
    public AnonPostResponse relate(Long postId) {
        AnonPostEntity post = anonRepo.findById(postId)
            .orElseThrow(() -> new EventNotFoundException(postId));
        post.setRelateCount(post.getRelateCount() + 1);
        return anonMapper.toResponse(anonRepo.save(post));
    }
}
