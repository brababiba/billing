package com.brababiba.billing.service;

import com.brababiba.billing.common.WorkspaceRoles;
import com.brababiba.billing.dto.auth.AuthResponse;
import com.brababiba.billing.dto.auth.LoginRequest;
import com.brababiba.billing.dto.auth.RegisterRequest;
import com.brababiba.billing.exception.EmailAlreadyExistsException;
import com.brababiba.billing.exception.InvalidCredentialsException;
import com.brababiba.billing.model.*;
import com.brababiba.billing.repository.UserRepository;
import com.brababiba.billing.repository.UserRoleRepository;
import com.brababiba.billing.repository.WorkspaceMemberRepository;
import com.brababiba.billing.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    private final UserRoleRepository userRoleRepository;

    private final WorkspaceRepository workspaceRepository;

    private final WorkspaceMemberRepository workspaceMemberRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final JwtService jwtService;

    public void register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new EmailAlreadyExistsException(request.getEmail());
        }

        User user = new User();

        user.setId(UUID.randomUUID());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(Instant.now());
        userRepository.save(user);

        Workspace workspace = new Workspace();

        String emailPrefix = request.getEmail().split("@")[0];
        workspace.setId(UUID.randomUUID());
        workspace.setName(emailPrefix + "'s workspace");
        workspace.setCreatedAt(Instant.now());
        workspaceRepository.save(workspace);

        WorkspaceMemberId memberId = new WorkspaceMemberId();

        memberId.setWorkspaceId(workspace.getId());
        memberId.setUserId(user.getId());

        WorkspaceMember workspaceMember = new WorkspaceMember();

        workspaceMember.setId(memberId);
        workspaceMember.setRole(WorkspaceRoles.OWNER);
        workspaceMember.setCreatedAt(LocalDateTime.now());

        workspaceMemberRepository.save(workspaceMember);

        UserRoleId roleId = new UserRoleId();
        roleId.setUserId(user.getId());
        roleId.setRole("USER");

        UserRole userRole = new UserRole();
        userRole.setId(roleId);

        userRoleRepository.save(userRole);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        boolean passwordMatches = passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash()
        );

        if (!passwordMatches) {
            throw new InvalidCredentialsException();
        }

        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponse(token);
    }
}
