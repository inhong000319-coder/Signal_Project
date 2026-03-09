package com.signal.infrastructure.sync;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "sync_cursors")
public class SyncCursorEntity {
    @EmbeddedId
    private SyncCursorId id;

    @Column(name = "last_delivered_message_id")
    private Long lastDeliveredMessageId;

    @Column(name = "last_read_message_id")
    private Long lastReadMessageId;

    @Version
    @Column(name = "version", nullable = false)
    private long version;

    protected SyncCursorEntity() {
    }

    public SyncCursorEntity(SyncCursorId id, Long lastDeliveredMessageId, Long lastReadMessageId) {
        this.id = id;
        this.lastDeliveredMessageId = lastDeliveredMessageId;
        this.lastReadMessageId = lastReadMessageId;
    }

    public SyncCursorId getId() {
        return id;
    }

    public Long getLastDeliveredMessageId() {
        return lastDeliveredMessageId;
    }

    public Long getLastReadMessageId() {
        return lastReadMessageId;
    }

    public long getVersion() {
        return version;
    }

    public void updateLastDelivered(Long lastDeliveredMessageId) {
        this.lastDeliveredMessageId = lastDeliveredMessageId;
    }

    public void updateLastRead(Long lastReadMessageId) {
        this.lastReadMessageId = lastReadMessageId;
    }
}
