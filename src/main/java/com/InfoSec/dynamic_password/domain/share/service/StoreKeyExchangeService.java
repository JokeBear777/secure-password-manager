package com.InfoSec.dynamic_password.domain.share.service;

import com.InfoSec.dynamic_password.domain.encrypt.entity.StoreRsaPublicKeyEntity;
import com.InfoSec.dynamic_password.domain.member.entity.Member;
import com.InfoSec.dynamic_password.domain.member.repository.MemberRepository;
import com.InfoSec.dynamic_password.domain.member.service.MemberService;
import com.InfoSec.dynamic_password.domain.share.Status.JoinStatus;
import com.InfoSec.dynamic_password.domain.share.dto.GetStoreKeyExchangeRequestDto;
import com.InfoSec.dynamic_password.domain.share.entity.ExchangeKeyEntity;
import com.InfoSec.dynamic_password.domain.share.entity.Store;
import com.InfoSec.dynamic_password.domain.share.entity.StoreJoin;
import com.InfoSec.dynamic_password.domain.share.repository.ExchangeKeyEntityRepository;
import com.InfoSec.dynamic_password.domain.share.repository.StoreJoinRepository;
import com.InfoSec.dynamic_password.domain.share.repository.StoreRepository;
import com.InfoSec.dynamic_password.global.security.auth.jwt.dto.SecurityUserDto;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class StoreKeyExchangeService {

    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;
    private final ExchangeKeyEntityRepository exchangeKeyEntityRepository;
    private final StoreJoinRepository storeJoinRepository;

    @Transactional
    public void saveExchangeKeyRequest(SecurityUserDto securityUserDto, Long storeId, String userPublicKey) {
        Member savedMember = memberRepository.findByMemberId(securityUserDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found."));

        Store savedStore = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found."));

        StoreJoin storeJoin = storeJoinRepository.findByStoreAndMember(savedStore,savedMember)
                .orElseThrow(() -> new IllegalArgumentException("You are not a member of this store."));

        if(!storeJoin.getJoinStatus().equals(JoinStatus.APPROVED)) {
            throw new IllegalArgumentException("This request is only allowed for approved members.");
        }

        if (userPublicKey == null || userPublicKey.isEmpty()) {
            throw new IllegalArgumentException("User's public key cannot be empty.");
        }

        if (exchangeKeyEntityRepository.existsByMember_MemberIdAndStore_StoreId(
                savedMember.getMemberId(), savedStore.getStoreId())) {
            throw new IllegalArgumentException("Request already exists.");
        };

        ExchangeKeyEntity exchangeKeyEntity = ExchangeKeyEntity.builder()
                .store(savedStore)
                .member(savedMember)
                .memberRSAPublicKey(userPublicKey)
                .masterEncryptedSecretKey(null)
                .build();

        exchangeKeyEntityRepository.save(exchangeKeyEntity);
    }

    public Set<GetStoreKeyExchangeRequestDto> getStoreKeyExchangeRequests(
            SecurityUserDto securityUserDto,
            Long storeId)
    {
        Member savedMember = memberRepository.findByMemberId(securityUserDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found."));

        Store savedStore = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found."));

        if (!savedMember.getMemberId().equals(savedStore.getMasterMemberId())) {
            throw new IllegalArgumentException("This request is only permitted for the store master");
        }

        return exchangeKeyEntityRepository.findByStore_StoreId(storeId)
                .stream()
                .map(exchangeKeyEntity -> GetStoreKeyExchangeRequestDto.builder()
                        .exchangedId(exchangeKeyEntity.getExchangeKeyId())
                        .memberId(exchangeKeyEntity.getMember().getMemberId())
                        .memberRsaPublicKey(exchangeKeyEntity.getMemberRSAPublicKey())
                        .build()
                )
                .collect(Collectors.toSet());
    }

    @Transactional
    public String getStoreKeyExchangeStatus(SecurityUserDto securityUserDto, Long storeId) {
        Member savedMember = memberRepository.findByMemberId(securityUserDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found."));

        Store savedStore = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found."));

        StoreJoin storeJoin = storeJoinRepository.findByStoreAndMember(savedStore,savedMember)
                .orElseThrow(() -> new IllegalArgumentException("You are not a member of this store."));

        if(!storeJoin.getJoinStatus().equals(JoinStatus.APPROVED)) {
            throw new IllegalArgumentException("This request is only allowed for approved members.");
        }

        ExchangeKeyEntity exchangeKeyEntity = exchangeKeyEntityRepository.findByMember_MemberIdAndStore_StoreId(
                securityUserDto.getUserId(),
                storeId
        ).orElseThrow(() -> new IllegalArgumentException("No key request has been made."));

        if(exchangeKeyEntity.getMasterEncryptedSecretKey() == null) {
            throw new IllegalArgumentException("Store master has not responded yet");
        }

        return exchangeKeyEntity.getMasterEncryptedSecretKey();
    }


}
