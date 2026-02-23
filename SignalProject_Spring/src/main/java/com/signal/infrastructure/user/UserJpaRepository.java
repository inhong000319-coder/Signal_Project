package com.signal.infrastructure.user;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserJpaRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByLoginId(String loginId);
    Optional<UserEntity> findByUserCode(String userCode);

    @Modifying
    @Query("update UserEntity u set u.passwordHash = :passwordHash where u.userId = :userId")
    int updatePasswordHash(@Param("userId") Long userId, @Param("passwordHash") String passwordHash);
}
