package com.signal.application.user;

public final class ChangePasswordCommand {
    private final Long userId;
    private final String currentPassword;
    private final String newPassword;

    public ChangePasswordCommand(Long userId, String currentPassword, String newPassword) {
        this.userId = userId;
        this.currentPassword = currentPassword;
        this.newPassword = newPassword;
    }

    public Long getUserId() {
        return userId;
    }

    public String getCurrentPassword() {
        return currentPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }
}
