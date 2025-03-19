package com.InfoSec.dynamic_password.domain.share.entity;

import com.InfoSec.dynamic_password.domain.member.entity.Member;
import com.InfoSec.dynamic_password.domain.share.Status.JoinStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

import static com.InfoSec.dynamic_password.domain.share.Status.JoinStatus.PENDING;

@Entity
@Table(name = "store_join")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class StoreJoin {

    @Id
    @Column(name = "store_join_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeJoinId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false, updatable = false)
    private Store store;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false, updatable = false)
    private Member member;

    @Column(name = "join_status")
    private JoinStatus joinStatus;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Date createdAt;


}
