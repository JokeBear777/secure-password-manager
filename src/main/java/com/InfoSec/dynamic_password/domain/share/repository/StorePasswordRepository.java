package com.InfoSec.dynamic_password.domain.share.repository;

import com.InfoSec.dynamic_password.domain.share.entity.StorePassword;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StorePasswordRepository extends JpaRepository<StorePassword, Long> {
}
