package com.InfoSec.dynamic_password.domain.share.repository;

import com.InfoSec.dynamic_password.domain.member.entity.Member;
import com.InfoSec.dynamic_password.domain.share.entity.Store;
import com.InfoSec.dynamic_password.domain.share.entity.StoreJoin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StoreJoinRepository extends JpaRepository<StoreJoin, Long> {
    @Query("SELECT sj FROM StoreJoin sj WHERE sj.store.storeId = :storeId")
    List<StoreJoin> findByStoreId(@Param("storeId") Long storeId);

    boolean existsByStoreAndMember(Store store, Member member);

    boolean existsByMember_MemberId(Long memberId);
}
