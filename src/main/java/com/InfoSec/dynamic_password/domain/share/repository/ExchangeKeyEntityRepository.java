package com.InfoSec.dynamic_password.domain.share.repository;

import com.InfoSec.dynamic_password.domain.share.entity.ExchangeKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Set;

public interface ExchangeKeyEntityRepository extends JpaRepository<ExchangeKeyEntity, Long> {
    Boolean existsByMember_MemberId(Long memberId);
    Set<ExchangeKeyEntity> findByStore_StoreId(Long storeId);
}
