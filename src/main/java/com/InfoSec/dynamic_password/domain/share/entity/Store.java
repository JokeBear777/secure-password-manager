package com.InfoSec.dynamic_password.domain.share.entity;

import com.InfoSec.dynamic_password.domain.member.entity.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Table(name = "store")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Store {

    @Id
    @Column(name = "store_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long storeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id", nullable = false, updatable = false)
    private Member member;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", nullable = true)
    private String description;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = true)
    private Date updatedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

}
