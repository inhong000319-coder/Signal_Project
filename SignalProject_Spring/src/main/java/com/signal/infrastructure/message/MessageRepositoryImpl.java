package com.signal.infrastructure.message;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.signal.domain.message.Message;
import com.signal.domain.message.port.MessageRepository;

@Repository
public class MessageRepositoryImpl implements MessageRepository {
    private final MessageJpaRepository messageJpaRepository;

    public MessageRepositoryImpl(MessageJpaRepository messageJpaRepository) {
        this.messageJpaRepository = messageJpaRepository;
    }

    @Override
    public Message save(Message message) {
        MessageEntity saved = messageJpaRepository.save(
            new MessageEntity(
                message.getConversationId(),
                message.getSenderUserId(),
                message.getContent(),
                message.getClientMessageKey(),
                message.getCreatedAt()
            )
        );
        return Message.restore(
            saved.getMessageId(),
            saved.getConversationId(),
            saved.getSenderUserId(),
            saved.getContent(),
            saved.getClientMessageKey(),
            saved.getCreatedAt()
        );
    }

    @Override
    public Optional<Message> findBySenderAndClientKey(Long senderUserId, String clientMessageKey) {
        return messageJpaRepository.findBySenderUserIdAndClientMessageKey(senderUserId, clientMessageKey)
            .map(m -> Message.restore(
                m.getMessageId(),
                m.getConversationId(),
                m.getSenderUserId(),
                m.getContent(),
                m.getClientMessageKey(),
                m.getCreatedAt()
            ));
    }

    @Override
    public List<Message> findPage(Long conversationId, Long beforeMessageId, int limit) {
        PageRequest page = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "messageId"));
        List<MessageEntity> entities;
        if (beforeMessageId == null) {
            entities = messageJpaRepository.findByConversationId(conversationId, page);
        } else {
            entities = messageJpaRepository.findByConversationIdAndMessageIdLessThan(conversationId, beforeMessageId, page);
        }
        return entities.stream()
            .map(m -> Message.restore(
                m.getMessageId(),
                m.getConversationId(),
                m.getSenderUserId(),
                m.getContent(),
                m.getClientMessageKey(),
                m.getCreatedAt()
            ))
            .toList();
    }

    @Override
    public List<Message> findAfter(Long conversationId, Long afterMessageId, int limit) {
        PageRequest page = PageRequest.of(0, limit, Sort.by(Sort.Direction.ASC, "messageId"));
        List<MessageEntity> entities;
        if (afterMessageId == null) {
            entities = messageJpaRepository.findByConversationId(conversationId, page);
        } else {
            entities = messageJpaRepository.findByConversationIdAndMessageIdGreaterThan(conversationId, afterMessageId, page);
        }
        return entities.stream()
            .map(m -> Message.restore(
                m.getMessageId(),
                m.getConversationId(),
                m.getSenderUserId(),
                m.getContent(),
                m.getClientMessageKey(),
                m.getCreatedAt()
            ))
            .toList();
    }

    @Override
    public Optional<Message> findById(Long messageId) {
        return messageJpaRepository.findById(messageId)
            .map(m -> Message.restore(
                m.getMessageId(),
                m.getConversationId(),
                m.getSenderUserId(),
                m.getContent(),
                m.getClientMessageKey(),
                m.getCreatedAt()
            ));
    }

    @Override
    public Optional<Message> findLatestByConversationId(Long conversationId) {
        return messageJpaRepository.findTopByConversationIdOrderByMessageIdDesc(conversationId)
            .map(m -> Message.restore(
                m.getMessageId(),
                m.getConversationId(),
                m.getSenderUserId(),
                m.getContent(),
                m.getClientMessageKey(),
                m.getCreatedAt()
            ));
    }

    @Override
    public long countUnread(Long conversationId, Long userId, Long lastReadMessageId) {
        if (lastReadMessageId == null) {
            return messageJpaRepository.countByConversationIdAndSenderUserIdNot(conversationId, userId);
        }
        return messageJpaRepository.countByConversationIdAndMessageIdGreaterThanAndSenderUserIdNot(
            conversationId, lastReadMessageId, userId
        );
    }

    @Override
    public List<Message> findByIds(List<Long> messageIds) {
        return messageJpaRepository.findByMessageIdIn(messageIds).stream()
            .map(m -> Message.restore(
                m.getMessageId(),
                m.getConversationId(),
                m.getSenderUserId(),
                m.getContent(),
                m.getClientMessageKey(),
                m.getCreatedAt()
            ))
            .toList();
    }
}
