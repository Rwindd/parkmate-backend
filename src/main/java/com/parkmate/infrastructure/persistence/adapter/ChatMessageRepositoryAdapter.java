package com.parkmate.infrastructure.persistence.adapter;

import com.parkmate.domain.port.ChatMessageRepositoryPort;
import com.parkmate.infrastructure.persistence.entity.ChatMessageEntity;
import com.parkmate.infrastructure.persistence.repository.JpaChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ChatMessageRepositoryAdapter implements ChatMessageRepositoryPort {
    private final JpaChatMessageRepository jpa;

    @Override public ChatMessageEntity save(ChatMessageEntity m) { return jpa.save(m); }

    @Override
    public List<ChatMessageEntity> findLast50() {
        List<ChatMessageEntity> desc = jpa.findLast50Desc();
        int size = Math.min(desc.size(), 50);
        List<ChatMessageEntity> sub = desc.subList(0, size);
        Collections.reverse(sub);
        return sub;
    }
}
