package com.kkaebiz.api_server.auth.domain;
import com.kkaebiz.api_server.auth.dto.Provider;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_users_provider_user", columnNames = {"provider", "provider_user_id"})
        },
        indexes = {
                @Index(name = "idx_users_provider_user", columnList = "provider, provider_user_id")
        }
)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 우리 JWT subject (long)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Provider provider; // KAKAO / APPLE

    @Column(name = "provider_user_id", nullable = false, length = 128)
    private String providerUserId; // kakaoId(String) or apple sub

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(length = 10, name = "nick_name")
    private String nickName;

    protected User() {}

    private User(Provider provider, String providerUserId) {
        this.provider = provider;
        this.providerUserId = providerUserId;
        this.createdAt = LocalDateTime.now();
    }

    public static User of(Provider provider, String providerUserId) {
        return new User(provider, providerUserId);
    }

    public Long getId() { return id; }
    public Provider getProvider() { return provider; }
    public String getProviderUserId() { return providerUserId; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
}
