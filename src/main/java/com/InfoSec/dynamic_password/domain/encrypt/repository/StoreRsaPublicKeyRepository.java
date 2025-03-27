package com.InfoSec.dynamic_password.domain.encrypt.repository;

import com.InfoSec.dynamic_password.domain.encrypt.entity.StoreRsaPublicKeyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StoreRsaPublicKeyRepository extends JpaRepository<StoreRsaPublicKeyEntity, Integer> {

    Optional<StoreRsaPublicKeyEntity> findByStore_StoreId(Long storeId);
    Boolean existsByStore_StoreId(Long storeId);
}
