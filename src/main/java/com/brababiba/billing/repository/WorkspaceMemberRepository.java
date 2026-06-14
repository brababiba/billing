package com.brababiba.billing.repository;

import com.brababiba.billing.model.WorkspaceMember;
import com.brababiba.billing.model.WorkspaceMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, WorkspaceMemberId> {

    List<WorkspaceMember> findByIdUserId(UUID userId);

    Optional<WorkspaceMember> findByIdWorkspaceIdAndIdUserId(UUID workspaceId, UUID userId);

    List<WorkspaceMember> findByIdWorkspaceId(UUID workspaceId);

    void deleteByIdWorkspaceId(UUID workspaceId);
}
