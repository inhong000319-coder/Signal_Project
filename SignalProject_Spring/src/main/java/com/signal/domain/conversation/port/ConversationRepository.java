package com.signal.domain.conversation.port;

import java.util.Optional;

import com.signal.domain.conversation.Conversation;
import com.signal.domain.conversation.ConversationType;

public interface ConversationRepository {
    Conversation save(Conversation conversation);
    Optional<Conversation> findDirectBetween(Long userIdA, Long userIdB);
    Optional<Conversation> findById(Long conversationId);
}
