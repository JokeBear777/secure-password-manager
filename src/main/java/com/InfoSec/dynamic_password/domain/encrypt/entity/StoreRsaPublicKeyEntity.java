package com.InfoSec.dynamic_password.domain.encrypt.entity;

import com.InfoSec.dynamic_password.domain.member.entity.Member;
import com.InfoSec.dynamic_password.domain.share.entity.Store;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Table(name = "store_rsa_public_key")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class StoreRsaPublicKeyEntity {
    @Id
    @Column(name = "store_rsa_public_key_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeRsaPublicKeyId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false, updatable = false)
    private Store store;

    @Column(name = "public_key", nullable = true, length = 2048)
    private String publicKey;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true)
    private Date updatedAt;

}
