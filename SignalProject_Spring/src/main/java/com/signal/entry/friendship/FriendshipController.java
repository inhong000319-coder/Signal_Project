package com.signal.entry.friendship;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import com.signal.application.friendship.FriendAcceptCommand;
import com.signal.application.friendship.FriendBlockCommand;
import com.signal.application.friendship.FriendListResult;
import com.signal.application.friendship.FriendListUseCase;
import com.signal.application.friendship.FriendRequestCommand;
import com.signal.application.friendship.FriendshipResult;
import com.signal.application.friendship.FriendshipUseCase;
import com.signal.entry.friendship.dto.FriendListResponse;
import com.signal.entry.friendship.dto.FriendRequestCreate;
import com.signal.entry.friendship.dto.FriendSummaryResponse;
import com.signal.entry.friendship.dto.FriendshipResponse;
import com.signal.infrastructure.security.UserPrincipal;

import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/friendships")
public class FriendshipController {
    private final FriendshipUseCase friendshipUseCase;
    private final FriendListUseCase friendListUseCase;

    public FriendshipController(FriendshipUseCase friendshipUseCase, FriendListUseCase friendListUseCase) {
        this.friendshipUseCase = friendshipUseCase;
        this.friendListUseCase = friendListUseCase;
    }

    @PostMapping
    public ResponseEntity<FriendshipResponse> request(
        @AuthenticationPrincipal UserPrincipal principal,
        @Valid @RequestBody FriendRequestCreate request
    ) {
        FriendshipResult result = friendshipUseCase.request(
            new FriendRequestCommand(principal == null ? null : principal.getUserId(), request.targetUserId())
        );
        return ResponseEntity.ok(toResponse(result));
    }

    @PostMapping("/{requesterUserId}/accept")
    public ResponseEntity<FriendshipResponse> accept(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable Long requesterUserId
    ) {
        FriendshipResult result = friendshipUseCase.accept(
            new FriendAcceptCommand(requesterUserId, principal == null ? null : principal.getUserId())
        );
        return ResponseEntity.ok(toResponse(result));
    }

    @PostMapping("/{targetUserId}/block")
    public ResponseEntity<FriendshipResponse> block(
        @AuthenticationPrincipal UserPrincipal principal,
        @PathVariable Long targetUserId
    ) {
        FriendshipResult result = friendshipUseCase.block(
            new FriendBlockCommand(principal == null ? null : principal.getUserId(), targetUserId)
        );
        return ResponseEntity.ok(toResponse(result));
    }

    @GetMapping
    public ResponseEntity<FriendListResponse> list(@AuthenticationPrincipal UserPrincipal principal) {
        FriendListResult result = friendListUseCase.list(principal == null ? null : principal.getUserId());
        List<FriendSummaryResponse> items = result.getFriends().stream()
            .map(f -> new FriendSummaryResponse(
                f.getFriendUserId(),
                f.getNickname(),
                f.getUserCode(),
                f.getConversationId()
            ))
            .toList();
        return ResponseEntity.ok(new FriendListResponse(items));
    }

    private FriendshipResponse toResponse(FriendshipResult result) {
        return new FriendshipResponse(
            result.getRequesterUserId(),
            result.getTargetUserId(),
            result.getStatus(),
            result.getRequestedAt(),
            result.getAcceptedAt()
        );
    }
}
