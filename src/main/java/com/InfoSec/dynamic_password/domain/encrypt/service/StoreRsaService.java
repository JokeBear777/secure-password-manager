package com.InfoSec.dynamic_password.domain.encrypt.service;

import com.InfoSec.dynamic_password.domain.encrypt.dto.SaveRsaPublicDto;
import com.InfoSec.dynamic_password.domain.encrypt.dto.SaveStoreRsaPublicDto;
import com.InfoSec.dynamic_password.domain.encrypt.entity.RsaPublicKeyEntity;
import com.InfoSec.dynamic_password.domain.encrypt.entity.StoreRsaPublicKeyEntity;
import com.InfoSec.dynamic_password.domain.encrypt.repository.StoreRsaPublicKeyRepository;
import com.InfoSec.dynamic_password.domain.member.entity.Member;
import com.InfoSec.dynamic_password.domain.member.repository.MemberRepository;
import com.InfoSec.dynamic_password.domain.share.entity.Store;
import com.InfoSec.dynamic_password.domain.share.repository.StoreRepository;
import com.InfoSec.dynamic_password.global.security.auth.jwt.dto.SecurityUserDto;
import com.InfoSec.dynamic_password.global.utils.validator.RsaKeyValidator;
import com.nimbusds.oauth2.sdk.util.StringUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

@Service
@Slf4j
@AllArgsConstructor
public class StoreRsaService {

    private final StoreRsaPublicKeyRepository storeRsaPublicKeyRepository;
    private final StoreRepository storeRepository;
    private final MemberRepository memberRepository;


    @Transactional
    public void saveStoreRsaPublic(SaveStoreRsaPublicDto saveStoreRsaPublicDto,
                                   SecurityUserDto securityUserDto,
                                   Long storeId) {
        String publicKey = saveStoreRsaPublicDto.getPublicKey();

        if (StringUtils.isBlank(publicKey)) {
            throw new IllegalArgumentException("Public key is empty");
        }

        if (!RsaKeyValidator.isValidPublicKey(publicKey)) {
            throw new IllegalArgumentException("Invalid public key format");
        }

        Member member = memberRepository.findByEmail(securityUserDto.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("Member with email "
                        + securityUserDto.getEmail() + " not found"));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found."));

        if (!member.getMemberId().equals(store.getMember().getMemberId())) {
            throw new IllegalArgumentException("Member does not match store id.");
        }

        if (storeRsaPublicKeyRepository.existsByStore_StoreId(storeId)) {
            throw new IllegalStateException("Store with ID " + storeId
                    + " already has a registered public key");
        }

        StoreRsaPublicKeyEntity StorePubKey = StoreRsaPublicKeyEntity.builder()
                .publicKey(publicKey)
                .store(store)
                .build();

        storeRsaPublicKeyRepository.save(StorePubKey);

        log.info("RSA public key saved for store ID: {}", storeId);
    }

    public String getRsaPublicKeyByStoreId(Long storeId) {
        StoreRsaPublicKeyEntity storeRsaPublicKeyEntity = storeRsaPublicKeyRepository.findByStore_StoreId(storeId)
                .orElseThrow(()-> new NotFoundException("PublicKey with " + storeId + " not found"));

        return storeRsaPublicKeyEntity.getPublicKey();
    }

}
