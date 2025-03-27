package com.InfoSec.dynamic_password.domain.share.service;

import com.InfoSec.dynamic_password.domain.encrypt.entity.StoreRsaPublicKeyEntity;
import com.InfoSec.dynamic_password.domain.encrypt.repository.StoreRsaPublicKeyRepository;
import com.InfoSec.dynamic_password.domain.member.entity.Member;
import com.InfoSec.dynamic_password.domain.member.repository.MemberRepository;
import com.InfoSec.dynamic_password.domain.share.Status.JoinStatus;
import com.InfoSec.dynamic_password.domain.share.dto.CreateStoreDto;
import com.InfoSec.dynamic_password.domain.share.dto.ApproveMemberRequestDto;
import com.InfoSec.dynamic_password.domain.share.dto.DisapproveMemberRequestDto;
import com.InfoSec.dynamic_password.domain.share.dto.RequestJoinMembersDto;
import com.InfoSec.dynamic_password.domain.share.entity.InviteToken;
import com.InfoSec.dynamic_password.domain.share.entity.Store;
import com.InfoSec.dynamic_password.domain.share.entity.StoreJoin;
import com.InfoSec.dynamic_password.domain.share.repository.InviteTokenRepository;
import com.InfoSec.dynamic_password.domain.share.repository.StoreJoinRepository;
import com.InfoSec.dynamic_password.domain.share.repository.StoreRepository;
import com.InfoSec.dynamic_password.global.config.configuration_properties.DomainConfig;
import com.InfoSec.dynamic_password.global.security.auth.jwt.dto.SecurityUserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class StoreService {

    private final StoreJoinRepository storeJoinRepository;
    private DomainConfig domainConfig;
    private StoreRepository storeRepository;
    private MemberRepository memberRepository;
    private InviteTokenRepository inviteTokenRepository;
    private StoreRsaPublicKeyRepository storeRsaPublicKeyRepository;

    @Transactional
    public Long createStore(
            CreateStoreDto createStoreDto,
            SecurityUserDto securityUserDto
    ) {
        if (createStoreDto.getStoreName() == null || createStoreDto.getStoreName().isEmpty()) {
            throw new IllegalArgumentException("Store name cannot be empty or null.");
        }

        Member member = memberRepository.findByEmail(securityUserDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Member not found."));

        Store store = Store.builder()
                .masterMemberId(securityUserDto.getUserId())
                .name(createStoreDto.getStoreName())
                .description(createStoreDto.getDescription())
                .build();

        storeRepository.save(store);

        StoreJoin storeJoin = StoreJoin.builder()
                .store(store)
                .member(member)
                .joinStatus(JoinStatus.MASTER)
                .build();

        storeJoinRepository.save(storeJoin);

        return store.getStoreId();
    }

    public String generateInviteLink(
            SecurityUserDto securityUserDto,
            Long storeId
    ) {
        isStoreMaster(securityUserDto, storeId);

        String token = generateInviteToken();
        InviteToken inviteToken = InviteToken.builder()
                .storeId(storeId)
                .token(token)
                .build();

        inviteTokenRepository.save(inviteToken);

        return String.format("%s/stores/%d/members/%s", domainConfig.getName(), storeId, token);
    }

    private String generateInviteToken() {
        return UUID.randomUUID().toString();
    }

    @Transactional
    public void requestJoin(Long storeId, String token, SecurityUserDto securityUserDto) {

       Member savedMember = memberRepository.findByMemberId(securityUserDto.getUserId())
               .orElseThrow(() -> new IllegalArgumentException("Member not found."));

       InviteToken savedInviteToken = inviteTokenRepository.findByToken(token)
               .orElseThrow(() -> new IllegalArgumentException("Invite token not found."));

       Store savedStore = storeRepository.findById(
         savedInviteToken.getStoreId())
               .orElseThrow(() -> new IllegalArgumentException("Store not found."));

       if(storeJoinRepository.existsByMember_MemberId(savedMember.getMemberId())) {
           throw new IllegalArgumentException("Member already exists.");
       }

       if (!savedInviteToken.getStoreId().equals(storeId)) {
           throw new IllegalArgumentException("Invite token does not match store id.");
       }

       inviteTokenRepository.delete(savedInviteToken);

       Date now = new Date();

       long diffInMillis = now.getTime() - savedInviteToken.getCreatedAt().getTime();
       long diffInDays = TimeUnit.MILLISECONDS.toDays(diffInMillis);

       if (diffInDays > 7) {
            throw new IllegalArgumentException("Invite token expired.");
       }

       StoreJoin storeJoin = StoreJoin.builder()
               .store(savedStore)
               .member(savedMember)
               .joinStatus(JoinStatus.PENDING)
               .build();

       storeJoinRepository.save(storeJoin);
       log.info("MemberId = {} Join Request In {} Success", savedMember.getMemberId(), savedStore.getStoreId());
    }

    @Transactional(readOnly = true)
    public List<RequestJoinMembersDto> getRequestJoinMembers(SecurityUserDto securityUserDto,Long storeId) {
        Member savedMember = memberRepository.findByMemberId(securityUserDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found."));

        Store savedStore = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found."));

        if (!savedMember.getMemberId().equals(savedStore.getMasterMemberId())) {
            throw new IllegalArgumentException("Member is not store master.");
        }

        List<StoreJoin> storeJoins = storeJoinRepository.findByStoreId(storeId);

        return storeJoins.stream()
                .map(storeJoin -> new RequestJoinMembersDto(
                        storeJoin.getStoreJoinId(),
                        savedMember.getMemberId(),
                        savedMember.getEmail(),
                        savedMember.getName()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public void approveRequestJoinMembers(
            SecurityUserDto securityUserDto,
            Long storeId,
            ApproveMemberRequestDto[] approveMemberRequestDtos
    ) {
        isStoreMaster(securityUserDto, storeId);

        List<Long> storeJoinIds = Arrays.stream(approveMemberRequestDtos)
                .map(ApproveMemberRequestDto::getStoreJoinId)
                .toList();

        List<StoreJoin> storeJoins = storeJoinRepository.findAllById(storeJoinIds);

        boolean hasInvalidStatus = storeJoins.stream()
                .anyMatch(join -> join.getJoinStatus() != JoinStatus.PENDING);

        if (hasInvalidStatus) {
            throw new IllegalArgumentException("The disapproval request contains a invalid request.");
        }

        if (storeJoinIds.size() != storeJoins.size()) {
            throw new IllegalArgumentException("The approval request contains a non-existent request.");
        }

        Store savedStore = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found."));

        if (!Objects.equals(securityUserDto.getUserId(), savedStore.getMasterMemberId())) {
            throw new IllegalArgumentException("Member does not have permission to approve store join requests.");
        }

        storeJoins.forEach(join -> join.setJoinStatus(JoinStatus.APPROVED));
        storeJoinRepository.saveAll(storeJoins);

    }

    @Transactional
    public void disApproveRequestJoinMembers(
            SecurityUserDto securityUserDto,
            Long storeId,
            DisapproveMemberRequestDto[] disapproveMemberRequestDtos
    ) {
        isStoreMaster(securityUserDto, storeId);

        Store savedStore = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found."));

        List<Long> storeJoinIds = Arrays.stream(disapproveMemberRequestDtos)
                .map(DisapproveMemberRequestDto::getStoreJoinId)
                .toList();

        List<StoreJoin> storeJoins = storeJoinRepository.findAllById(storeJoinIds);

        boolean hasInvalidStatus = storeJoins.stream()
                .anyMatch(join -> join.getJoinStatus() != JoinStatus.PENDING);

        if (hasInvalidStatus) {
            throw new IllegalArgumentException("The disapproval request contains a invalid request.");
        }

        if (storeJoinIds.size() != storeJoins.size()) {
            throw new IllegalArgumentException("The disapproval request contains a non-existent request.");
        }

        if (!Objects.equals(securityUserDto.getUserId(), savedStore.getMasterMemberId())) {
            throw new IllegalArgumentException("Member does not have permission to approve store join requests.");
        }

        storeJoinRepository.deleteAll(storeJoins);
    }

    private void isStoreMaster(
            SecurityUserDto securityUserDto,
            Long storeId
    ) {
        Store savedStore = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found."));

        if (!savedStore.getMasterMemberId().equals(securityUserDto.getUserId())) {
            throw new IllegalArgumentException("This request is only permitted for the store master");
        }
    }

}
