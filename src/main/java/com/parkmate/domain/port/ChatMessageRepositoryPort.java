package com.parkmate.domain.port;

import com.parkmate.infrastructure.persistence.entity.ChatMessageEntity;
import java.util.List;

public interface ChatMessageRepositoryPort {
    ChatMessageEntity save(ChatMessageEntity message);
    List<ChatMessageEntity> findLast50();
}
