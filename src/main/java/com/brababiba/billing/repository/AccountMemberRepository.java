package com.brababiba.billing.repository;

import com.brababiba.billing.model.AccountMember;
import com.brababiba.billing.model.AccountMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountMemberRepository extends JpaRepository<AccountMember, AccountMemberId> {
}
