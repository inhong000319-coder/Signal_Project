package com.signal.infrastructure.conversation;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import com.signal.domain.conversation.ConversationMember;
import com.signal.domain.conversation.port.ConversationMemberRepository;

@Repository
public class ConversationMemberRepositoryImpl implements ConversationMemberRepository {
    private final ConversationMemberJpaRepository conversationMemberJpaRepository;

    public ConversationMemberRepositoryImpl(ConversationMemberJpaRepository conversationMemberJpaRepository) {
        this.conversationMemberJpaRepository = conversationMemberJpaRepository;
    }

    @Override
    public void saveAll(List<ConversationMember> members) {
        conversationMemberJpaRepository.saveAll(
            members.stream()
                .map(m -> new ConversationMemberEntity(
                    new ConversationMemberId(m.getConversationId(), m.getUserId()),
                    m.getRole()
                ))
                .collect(Collectors.toList())
        );
    }

    @Override
    public boolean exists(Long conversationId, Long userId) {
        return conversationMemberJpaRepository.existsByIdConversationIdAndIdUserId(conversationId, userId);
    }

    @Override
    public List<Long> findConversationIdsByUserId(Long userId, int limit) {
        return conversationMemberJpaRepository.findConversationIdsOrderByLastMessage(
            userId,
            PageRequest.of(0, limit)
        );
    }
}
