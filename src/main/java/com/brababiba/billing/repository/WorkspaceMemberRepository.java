package com.brababiba.billing.repository;

import com.brababiba.billing.model.WorkspaceMember;
import com.brababiba.billing.model.WorkspaceMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, WorkspaceMemberId> {

    List<WorkspaceMember> findByIdUserId(UUID userId);
}
