package com.soulrebel.auth.repository;

import com.soulrebel.auth.domain.User;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query(value = """
            select u.* from user u inner join token t on u.id = t.user
            where u.id = :id and t.refresh_token = :refreshToken and t.expired_at >= :expiredAt
            """)
    Optional<User> findByIdAndTokensRefreshTokenAndTokenExpiredAtGreaterThan
            (Long id, String refreshToken, LocalDateTime expiredAt);

    @Query(value = """
            select u.*from user u inner join password_recovery pr on u.id = pr.user
            where pr.token = :token
            """)
    Optional<User> findByPasswordRecoveriesToken(String token);
}
