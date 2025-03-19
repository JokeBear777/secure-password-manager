package com.InfoSec.dynamic_password.domain.share.service;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
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

    @Transactional
    public void createStore(
            CreateStoreDto createStoreDto,
            SecurityUserDto securityUserDto
    ) {
        if (createStoreDto.getStoreName() == null || createStoreDto.getStoreName().isEmpty()) {
            throw new IllegalArgumentException("Store name cannot be empty or null.");
        }

        Member member = memberRepository.findByEmail(securityUserDto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Member not found."));

        Store store = Store.builder()
                .member(member)
                .name(createStoreDto.getStoreName())
                .description(createStoreDto.getDescription())
                .build();
        storeRepository.save(store);

        StoreJoin storeJoin = StoreJoin.builder()
                .store(store)
                .member(member)
                .joinStatus(JoinStatus.PENDING)
                .build();

        storeJoinRepository.save(storeJoin);
    }

    public String generateInviteLink(
            SecurityUserDto securityUserDto,
            Long storeId
    ) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found."));

        if (store.getMember() != null
                && store.getMember().getEmail() != null
                && store.getMember().getEmail().equals(securityUserDto.getEmail())) {
            throw new IllegalArgumentException("You do not have permission to modify the store.");
        }

        String token = generateInviteToken();
        InviteToken inviteToken = InviteToken.builder()
                .storeId(storeId)
                .token(token)
                .build();

        inviteTokenRepository.save(inviteToken);

        return String.format("%s/%d/members/%s", domainConfig.getName(), storeId, token);
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

       if (!savedInviteToken.getStoreId().equals(storeId)) {
           throw new IllegalArgumentException("Invite token does not match store id.");
       }

       inviteTokenRepository.delete(savedInviteToken);

       StoreJoin storeJoin = StoreJoin.builder()
               .store(savedStore)
               .member(savedMember)
               .joinStatus(JoinStatus.PENDING)
               .build();

       storeJoinRepository.save(storeJoin);
    }

    @Transactional(readOnly = true)
    public List<RequestJoinMembersDto> getRequestJoinMembers(SecurityUserDto securityUserDto,Long storeId) {
        Member savedMember = memberRepository.findByMemberId(securityUserDto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Member not found."));

        Store savedStore = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found."));

        if (!savedMember.getMemberId().equals(savedStore.getMember().getMemberId())) {
            throw new IllegalArgumentException("Member does not match store id.");
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

        if (!Objects.equals(securityUserDto.getUserId(), savedStore.getMember().getMemberId())) {
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

        Store savedStore = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found."));

        if (!Objects.equals(securityUserDto.getUserId(), savedStore.getMember().getMemberId())) {
            throw new IllegalArgumentException("Member does not have permission to approve store join requests.");
        }

        storeJoinRepository.deleteAll(storeJoins);
    }


}
