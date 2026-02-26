package com.signal.infrastructure.websocket;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import com.signal.infrastructure.conversation.ConversationMemberJpaRepository;
import com.signal.infrastructure.security.JwtTokenProvider;
import com.signal.infrastructure.security.UserPrincipal;

@Component
public class StompAuthChannelInterceptor implements ChannelInterceptor {
    private final JwtTokenProvider tokenProvider;
    private final ConversationMemberJpaRepository conversationMemberJpaRepository;

    public StompAuthChannelInterceptor(
        JwtTokenProvider tokenProvider,
        ConversationMemberJpaRepository conversationMemberJpaRepository
    ) {
        this.tokenProvider = tokenProvider;
        this.conversationMemberJpaRepository = conversationMemberJpaRepository;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) {
            return message;
        }

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            authenticate(accessor);
        }

        if (StompCommand.SUBSCRIBE.equals(accessor.getCommand())) {
            authorizeSubscription(accessor);
        }

        if (StompCommand.SEND.equals(accessor.getCommand())) {
            authorizeSend(accessor);
        }

        return message;
    }

    private void authenticate(StompHeaderAccessor accessor) {
        String auth = accessor.getFirstNativeHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                Long userId = tokenProvider.parseUserId(token);
                accessor.setUser(new UserPrincipal(userId));
            } catch (RuntimeException ex) {
                throw new IllegalArgumentException("invalid token");
            }
        } else {
            throw new IllegalArgumentException("missing token");
        }
    }

    private void authorizeSubscription(StompHeaderAccessor accessor) {
        if (!(accessor.getUser() instanceof UserPrincipal principal)) {
            throw new IllegalArgumentException("unauthorized");
        }

        String destination = accessor.getDestination();
        if (destination == null) {
            return;
        }

        String prefix = "/topic/conversations/";
        if (!destination.startsWith(prefix)) {
            return;
        }

        Long conversationId = parseConversationId(destination, prefix);
        if (conversationId == null) {
            throw new IllegalArgumentException("invalid destination");
        }

        ensureMember(conversationId, principal.getUserId());
    }

    private void authorizeSend(StompHeaderAccessor accessor) {
        if (!(accessor.getUser() instanceof UserPrincipal principal)) {
            throw new IllegalArgumentException("unauthorized");
        }

        String destination = accessor.getDestination();
        if (destination == null) {
            return;
        }

        String prefix = "/app/conversations/";
        if (!destination.startsWith(prefix)) {
            return;
        }

        Long conversationId = parseConversationId(destination, prefix);
        if (conversationId == null) {
            throw new IllegalArgumentException("invalid destination");
        }

        ensureMember(conversationId, principal.getUserId());
    }

    private void ensureMember(Long conversationId, Long userId) {
        boolean member = conversationMemberJpaRepository.existsByIdConversationIdAndIdUserId(
            conversationId,
            userId
        );
        if (!member) {
            throw new IllegalArgumentException("forbidden");
        }
    }

    private Long parseConversationId(String destination, String prefix) {
        String rest = destination.substring(prefix.length());
        int slash = rest.indexOf('/');
        String idPart = slash >= 0 ? rest.substring(0, slash) : rest;
        if (idPart.isEmpty()) {
            return null;
        }
        try {
            return Long.parseLong(idPart);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
