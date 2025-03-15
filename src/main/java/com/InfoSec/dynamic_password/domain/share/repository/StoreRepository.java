package com.InfoSec.dynamic_password.domain.share.repository;

import com.InfoSec.dynamic_password.domain.share.entity.Store;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreRepository extends JpaRepository<Store, Long> {
}
