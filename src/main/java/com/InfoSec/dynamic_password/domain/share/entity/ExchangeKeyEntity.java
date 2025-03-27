package com.InfoSec.dynamic_password.domain.share.entity;

import com.InfoSec.dynamic_password.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "exchange_key_entity")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class ExchangeKeyEntity {

    @Id
    @Column(name = "exchange_key_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long exchangeKeyId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "store_id", nullable = false, updatable = false)
    private Store store;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id", nullable = false, updatable = false)
    private Member member;

    @Column(name = "member_RSA_public_key", nullable = false)
    private String memberRSAPublicKey;

    @Column(name = "master_encrypted_secret_key", nullable = true)
    private String masterEncryptedSecretKey;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

}
