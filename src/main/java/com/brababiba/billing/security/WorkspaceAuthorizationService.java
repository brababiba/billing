package com.brababiba.billing.security;

import com.brababiba.billing.exception.WorkspaceNotFoundException;
import com.brababiba.billing.model.User;
import com.brababiba.billing.model.WorkspaceMember;
import com.brababiba.billing.repository.UserRepository;
import com.brababiba.billing.repository.WorkspaceMemberRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class WorkspaceAuthorizationService {

    private final WorkspaceMemberRepository workspaceMemberRepository;

    private final UserRepository userRepository;

    public WorkspaceAuthorizationService(
            WorkspaceMemberRepository workspaceMemberRepository,
            UserRepository userRepository
    ) {
        this.workspaceMemberRepository = workspaceMemberRepository;
        this.userRepository = userRepository;
    }

    public void requirePermission(
            UUID workspaceId,
            String userEmail,
            WorkspacePermission permission
    ) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow();

        WorkspaceMember workspaceMember = workspaceMemberRepository
                .findByIdWorkspaceIdAndIdUserId(workspaceId, user.getId())
                .orElseThrow(() -> new WorkspaceNotFoundException(workspaceId.toString()));

        WorkspaceRole role = WorkspaceRole.valueOf(workspaceMember.getRole());

        if (!role.hasPermission(permission)) {
            throw new WorkspaceNotFoundException(workspaceId.toString());
        }
    }

}
