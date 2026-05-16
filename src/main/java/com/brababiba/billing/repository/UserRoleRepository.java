package com.brababiba.billing.repository;

import com.brababiba.billing.model.UserRole;
import com.brababiba.billing.model.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {
}
