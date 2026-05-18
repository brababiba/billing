package com.brababiba.billing.repository;

import com.brababiba.billing.model.WorkspaceMember;
import com.brababiba.billing.model.WorkspaceMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, WorkspaceMemberId> {
}
