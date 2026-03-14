package com.kkaebiz.api_server.auth.service;

import com.kkaebiz.api_server.auth.domain.User;
import com.kkaebiz.api_server.auth.dto.Provider;
import com.kkaebiz.api_server.auth.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SocialLoginService {

    private final UserRepository userRepository;

    public SocialLoginService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public long findOrCreateUserId(Provider provider, String providerUserId) {
        return userRepository.findByProviderAndProviderUserId(provider, providerUserId)
                .map(User::getId)
                .orElseGet(() -> createSafely(provider, providerUserId));
    }

    /**
     * 동시 로그인(경합)으로 같은 provider/providerUserId가 동시에 insert될 수 있어서
     * 유니크 제약 + 예외 처리로 안전하게 한 번 더 조회한다.
     */
    private long createSafely(Provider provider, String providerUserId) {
        try {
            User saved = userRepository.saveAndFlush(User.of(provider, providerUserId));
            return saved.getId();
        } catch (DataIntegrityViolationException e) {
            // 누군가 먼저 생성한 경우일 수 있으니 재조회
            return userRepository.findByProviderAndProviderUserId(provider, providerUserId)
                    .map(User::getId)
                    .orElseThrow(() -> e);
        }
    }
}