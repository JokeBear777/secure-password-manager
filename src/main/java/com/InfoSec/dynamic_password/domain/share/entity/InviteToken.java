package com.InfoSec.dynamic_password.domain.share.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@Table(name = "invite_token")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class InviteToken {

    @Id
    @Column(name = "invite_token_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inviteTokenId;

    @Column(name = "store_id", nullable = false)
    private Long storeId;

    @Column(name = "token", nullable = false)
    private String token;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private Date createdAt;

}
