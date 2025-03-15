package com.InfoSec.dynamic_password.domain.share.repository;

import com.InfoSec.dynamic_password.domain.share.entity.InviteToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InviteTokenRepository extends JpaRepository<InviteToken, Long> {
}
