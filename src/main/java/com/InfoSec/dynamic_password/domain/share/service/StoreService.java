package com.InfoSec.dynamic_password.domain.share.service;

import com.InfoSec.dynamic_password.domain.member.entity.Member;
import com.InfoSec.dynamic_password.domain.member.repository.MemberRepository;
import com.InfoSec.dynamic_password.domain.share.dto.CreateStoreDto;
import com.InfoSec.dynamic_password.domain.share.entity.InviteToken;
import com.InfoSec.dynamic_password.domain.share.entity.Store;
import com.InfoSec.dynamic_password.domain.share.repository.InviteTokenRepository;
import com.InfoSec.dynamic_password.domain.share.repository.StoreRepository;
import com.InfoSec.dynamic_password.global.config.configuration_properties.DomainConfig;
import com.InfoSec.dynamic_password.global.security.auth.jwt.dto.SecurityUserDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
@Slf4j
public class StoreService {

    private DomainConfig domainConfig;
    private StoreRepository storeRepository;
    private MemberRepository memberRepository;
    private InviteTokenRepository inviteTokenRepository;

    public void createStore(
            CreateStoreDto createStoreDto,
            SecurityUserDto securityUserDto
    ) {
        if (createStoreDto.getStoreName() == null || createStoreDto.getStoreName().isEmpty()) {
            throw new IllegalArgumentException("Store name cannot be empty or null.");
        }

        Member member = memberRepository.findByEmail(securityUserDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Member not found."));

        Store store = Store.builder()
                .member(member)
                .name(createStoreDto.getStoreName())
                .description(createStoreDto.getDescription())
                .build();

        storeRepository.save(store);
    }

    public String generateInviteLink(
            SecurityUserDto securityUserDto,
            Long storeId
    ) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found."));

        if (store.getMember() != null
                && store.getMember().getEmail() != null
                && store.getMember().getEmail().equals(securityUserDto.getEmail())) {
            throw new IllegalArgumentException("You do not have permission to modify the store.");
        }

        String token = generateInviteToken();
        InviteToken inviteToken = InviteToken.builder()
                .storeId(storeId)
                .token(token)
                .build();

        inviteTokenRepository.save(inviteToken);

        return String.format("%s/%d/members/%s", domainConfig.getName(), storeId, token);
    }

    private String generateInviteToken() {
        return UUID.randomUUID().toString();
    }

}
