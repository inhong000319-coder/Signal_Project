package com.signal.infrastructure.conversation;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.signal.domain.conversation.Conversation;
import com.signal.domain.conversation.ConversationType;
import com.signal.domain.conversation.port.ConversationRepository;

@Repository
public class ConversationRepositoryImpl implements ConversationRepository {
    private final ConversationJpaRepository conversationJpaRepository;

    public ConversationRepositoryImpl(ConversationJpaRepository conversationJpaRepository) {
        this.conversationJpaRepository = conversationJpaRepository;
    }

    @Override
    public Conversation save(Conversation conversation) {
        ConversationEntity saved = conversationJpaRepository.save(
            new ConversationEntity(conversation.getType(), conversation.isActive(), conversation.getCreatedAt())
        );
        return Conversation.restore(saved.getConversationId(), saved.getType(), saved.isActive(), saved.getCreatedAt());
    }

    @Override
    public Optional<Conversation> findDirectBetween(Long userIdA, Long userIdB) {
        return conversationJpaRepository.findDirectBetween(ConversationType.DIRECT, userIdA, userIdB)
            .map(c -> Conversation.restore(c.getConversationId(), c.getType(), c.isActive(), c.getCreatedAt()));
    }

    @Override
    public Optional<Conversation> findById(Long conversationId) {
        return conversationJpaRepository.findById(conversationId)
            .map(c -> Conversation.restore(c.getConversationId(), c.getType(), c.isActive(), c.getCreatedAt()));
    }
}
