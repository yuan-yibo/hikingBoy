package com.hiking.application.team.service;

import com.hiking.application.team.dto.*;
import com.hiking.domain.team.entity.Team;
import com.hiking.domain.team.entity.TeamMember;
import com.hiking.domain.team.repository.TeamMemberRepository;
import com.hiking.domain.team.repository.TeamRepository;
import com.hiking.domain.team.valueobject.MemberStatus;
import com.hiking.domain.user.entity.User;
import com.hiking.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 团队应用服务
 */
@Service
@RequiredArgsConstructor
public class TeamApplicationService {

    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final UserRepository userRepository;

    /**
     * 根据 openId 获取或创建用户，返回用户ID
     */
    private Long getOrCreateUserId(String openId) {
        return userRepository.findByOpenId(openId)
                .map(User::getId)
                .orElseGet(() -> {
                    User newUser = User.create(openId, "徒步爱好者", null);
                    userRepository.save(newUser);
                    return newUser.getId();
                });
    }

    /**
     * 创建团队
     */
    @Transactional
    public TeamDTO createTeam(String visitorId, CreateTeamRequest request) {
        Long userId = getOrCreateUserId(visitorId);

        // 创建团队
        Team team = Team.create(request.getName(), request.getDescription(), userId);
        teamRepository.save(team);

        // 创建者自动成为团队所有者
        TeamMember ownerMember = TeamMember.createOwner(team.getId(), userId);
        teamMemberRepository.save(ownerMember);

        return toTeamDTO(team);
    }

    /**
     * 获取我的团队列表（包括我创建的和我加入的）
     */
    public List<TeamDTO> getMyTeams(String visitorId) {
        Long userId = getOrCreateUserId(visitorId);
        // 获取用户加入的所有团队
        List<TeamMember> members = teamMemberRepository.findApprovedByUserId(userId);
        
        return members.stream()
                .map(member -> teamRepository.findById(member.getTeamId()).orElse(null))
                .filter(team -> team != null)
                .map(this::toTeamDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取团队详情
     */
    public TeamDTO getTeam(Long teamId, String visitorId) {
        Long userId = getOrCreateUserId(visitorId);
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("团队不存在"));

        // 验证用户是否为团队成员
        validateTeamMember(teamId, userId);

        return toTeamDTO(team);
    }

    /**
     * 更新团队信息
     */
    @Transactional
    public TeamDTO updateTeam(Long teamId, String visitorId, UpdateTeamRequest request) {
        Long userId = getOrCreateUserId(visitorId);
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("团队不存在"));

        // 只有所有者可以更新
        if (!team.isOwner(userId)) {
            throw new IllegalStateException("只有团队创建者可以修改团队信息");
        }

        team.update(request.getName(), request.getDescription());
        teamRepository.save(team);

        return toTeamDTO(team);
    }

    /**
     * 解散团队
     */
    @Transactional
    public void deleteTeam(Long teamId, String visitorId) {
        Long userId = getOrCreateUserId(visitorId);
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("团队不存在"));

        // 只有所有者可以解散
        if (!team.isOwner(userId)) {
            throw new IllegalStateException("只有团队创建者可以解散团队");
        }

        // 删除所有成员
        teamMemberRepository.deleteByTeamId(teamId);
        // 删除团队
        teamRepository.deleteById(teamId);
    }

    /**
     * 通过邀请码加入团队
     */
    @Transactional
    public TeamDTO joinByInviteCode(String visitorId, JoinTeamRequest request) {
        Long userId = getOrCreateUserId(visitorId);
        // 查找团队
        Team team = teamRepository.findByInviteCode(request.getInviteCode())
                .orElseThrow(() -> new IllegalArgumentException("无效的邀请码"));

        // 检查是否已经是成员
        if (teamMemberRepository.existsByTeamIdAndUserId(team.getId(), userId)) {
            throw new IllegalStateException("您已经是该团队成员");
        }

        // 直接加入（通过邀请码无需审批）
        TeamMember member = TeamMember.createByInviteCode(team.getId(), userId);
        teamMemberRepository.save(member);

        return toTeamDTO(team);
    }

    /**
     * 申请加入团队
     */
    @Transactional
    public void applyToJoin(Long teamId, String visitorId) {
        Long userId = getOrCreateUserId(visitorId);
        // 验证团队存在
        teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("团队不存在"));

        // 检查是否已经是成员或已申请
        if (teamMemberRepository.existsByTeamIdAndUserId(teamId, userId)) {
            throw new IllegalStateException("您已经申请或加入该团队");
        }

        // 创建申请记录
        TeamMember member = TeamMember.createApplicant(teamId, userId);
        teamMemberRepository.save(member);
    }

    /**
     * 获取团队成员列表
     */
    public List<TeamMemberDTO> getTeamMembers(Long teamId, String visitorId) {
        Long userId = getOrCreateUserId(visitorId);
        // 验证用户是否为团队成员
        validateTeamMember(teamId, userId);

        List<TeamMember> members = teamMemberRepository.findByTeamId(teamId);
        
        return members.stream()
                .map(this::toMemberDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取待审批的申请列表
     */
    public List<TeamMemberDTO> getPendingApplications(Long teamId, String visitorId) {
        Long userId = getOrCreateUserId(visitorId);
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("团队不存在"));

        // 只有所有者可以查看申请
        if (!team.isOwner(userId)) {
            throw new IllegalStateException("只有团队创建者可以查看申请列表");
        }

        List<TeamMember> pendingMembers = teamMemberRepository.findByTeamIdAndStatus(teamId, MemberStatus.PENDING);
        
        return pendingMembers.stream()
                .map(this::toMemberDTO)
                .collect(Collectors.toList());
    }

    /**
     * 审批加入申请
     */
    @Transactional
    public void approveApplication(Long teamId, Long memberId, String visitorId, boolean approve) {
        Long userId = getOrCreateUserId(visitorId);
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("团队不存在"));

        // 只有所有者可以审批
        if (!team.isOwner(userId)) {
            throw new IllegalStateException("只有团队创建者可以审批申请");
        }

        TeamMember member = teamMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("申请记录不存在"));

        if (!member.getTeamId().equals(teamId)) {
            throw new IllegalArgumentException("申请记录不属于该团队");
        }

        if (approve) {
            member.approve();
        } else {
            member.reject();
        }
        teamMemberRepository.save(member);
    }

    /**
     * 移除成员
     */
    @Transactional
    public void removeMember(Long teamId, Long memberId, String visitorId) {
        Long userId = getOrCreateUserId(visitorId);
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("团队不存在"));

        TeamMember member = teamMemberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("成员不存在"));

        if (!member.getTeamId().equals(teamId)) {
            throw new IllegalArgumentException("成员不属于该团队");
        }

        // 不能移除所有者
        if (member.isOwner()) {
            throw new IllegalStateException("不能移除团队创建者");
        }

        // 只有所有者可以移除他人，或者成员自己退出
        if (!team.isOwner(userId) && !member.getUserId().equals(userId)) {
            throw new IllegalStateException("没有权限移除该成员");
        }

        teamMemberRepository.deleteById(memberId);
    }

    /**
     * 退出团队
     */
    @Transactional
    public void leaveTeam(Long teamId, String visitorId) {
        Long userId = getOrCreateUserId(visitorId);
        TeamMember member = teamMemberRepository.findByTeamIdAndUserId(teamId, userId)
                .orElseThrow(() -> new IllegalArgumentException("您不是该团队成员"));

        // 所有者不能退出，只能解散
        if (member.isOwner()) {
            throw new IllegalStateException("团队创建者不能退出团队，请解散团队");
        }

        teamMemberRepository.deleteById(member.getId());
    }

    /**
     * 重新生成邀请码
     */
    @Transactional
    public String regenerateInviteCode(Long teamId, String visitorId) {
        Long userId = getOrCreateUserId(visitorId);
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new IllegalArgumentException("团队不存在"));

        // 只有所有者可以重新生成
        if (!team.isOwner(userId)) {
            throw new IllegalStateException("只有团队创建者可以重新生成邀请码");
        }

        team.regenerateInviteCode();
        teamRepository.save(team);

        return team.getInviteCode();
    }

    /**
     * 获取用户所在团队的所有成员ID列表
     */
    public List<Long> getTeamMemberUserIds(Long userId) {
        // 获取用户加入的所有团队
        List<TeamMember> myMemberships = teamMemberRepository.findApprovedByUserId(userId);
        
        // 获取这些团队的所有成员
        return myMemberships.stream()
                .flatMap(membership -> teamMemberRepository.findByTeamIdAndStatus(
                        membership.getTeamId(), MemberStatus.APPROVED).stream())
                .map(TeamMember::getUserId)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 验证用户是否为团队成员
     */
    private void validateTeamMember(Long teamId, Long userId) {
        teamMemberRepository.findByTeamIdAndUserId(teamId, userId)
                .filter(TeamMember::isApproved)
                .orElseThrow(() -> new IllegalStateException("您不是该团队的成员"));
    }

    private TeamDTO toTeamDTO(Team team) {
        User owner = userRepository.findById(team.getOwnerId()).orElse(null);
        long memberCount = teamMemberRepository.countApprovedByTeamId(team.getId());
        
        return TeamDTO.builder()
                .id(team.getId())
                .name(team.getName())
                .description(team.getDescription())
                .inviteCode(team.getInviteCode())
                .ownerId(team.getOwnerId())
                .ownerName(owner != null ? owner.getNickname() : null)
                .memberCount((int) memberCount)
                .createTime(team.getCreateTime())
                .build();
    }

    private TeamMemberDTO toMemberDTO(TeamMember member) {
        User user = userRepository.findById(member.getUserId()).orElse(null);
        
        return TeamMemberDTO.builder()
                .id(member.getId())
                .teamId(member.getTeamId())
                .userId(member.getUserId())
                .nickname(user != null ? user.getNickname() : null)
                .avatar(user != null ? user.getAvatar() : null)
                .role(member.getRole().name())
                .status(member.getStatus().name())
                .joinTime(member.getJoinTime())
                .build();
    }
}
