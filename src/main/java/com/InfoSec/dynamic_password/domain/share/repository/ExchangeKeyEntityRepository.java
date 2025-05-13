package com.InfoSec.dynamic_password.domain.share.repository;

import com.InfoSec.dynamic_password.domain.share.entity.ExchangeKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.OptionalLong;
import java.util.Set;

public interface ExchangeKeyEntityRepository extends JpaRepository<ExchangeKeyEntity, Long> {
    Boolean existsByMember_MemberIdAndStore_StoreId(Long memberId, Long storeId);
    Set<ExchangeKeyEntity> findByStore_StoreId(Long storeId);
    Optional<ExchangeKeyEntity> findByMember_MemberIdAndStore_StoreId(Long memberId, Long storeId);
}
