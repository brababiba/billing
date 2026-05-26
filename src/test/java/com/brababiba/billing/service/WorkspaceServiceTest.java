package com.brababiba.billing.service;

import com.brababiba.billing.dto.UpdateWorkspaceRequest;
import com.brababiba.billing.dto.WorkspaceResponse;
import com.brababiba.billing.exception.WorkspaceNotFoundException;
import com.brababiba.billing.model.Workspace;
import com.brababiba.billing.repository.WorkspaceMemberRepository;
import com.brababiba.billing.repository.WorkspaceRepository;
import com.brababiba.billing.security.WorkspaceAuthorizationService;
import com.brababiba.billing.security.WorkspacePermission;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WorkspaceServiceTest {

    @Mock
    private WorkspaceRepository repository;

    @Mock
    private WorkspaceAuthorizationService workspaceAuthorizationService;

    @Mock
    private WorkspaceMemberRepository workspaceMemberRepository;

    @InjectMocks
    private WorkspaceService service;

    @Test
    void createShouldSaveWorkspace() {
        when(repository.save(any(Workspace.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Workspace result = service.create("Igor");

        assertNotNull(result.getId());
        assertEquals("Igor", result.getName());
        assertNotNull(result.getCreatedAt());

        verify(repository).save(any(Workspace.class));
    }

    @Test
    void getByIdShouldThrowExceptionWhenWorkspaceDoesNotExist() {

        UUID id = UUID.randomUUID();

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(WorkspaceNotFoundException.class, () -> service.getById(id));

        verify(repository).findById(id);
    }

    @Test
    void getByIdShouldReturnWorkspaceWhenExists() {

        UUID id = UUID.randomUUID();

        Workspace workspace = new Workspace();
        workspace.setId(id);
        workspace.setName("Igor");
        workspace.setCreatedAt(Instant.now());

        when(repository.findById(id))
                .thenReturn(Optional.of(workspace));

        WorkspaceResponse result = service.getById(id);

        assertEquals(id.toString(), result.id());
        assertEquals("Igor", result.name());
        assertNotNull(result.createdAt());

        verify(repository).findById(id);
    }

    @Test
    void updateShouldChangeWorkspaceName() {

        UUID id = UUID.randomUUID();
        String userEmail = "owner@test.com";

        UpdateWorkspaceRequest request = new UpdateWorkspaceRequest();
        request.setName("NewName");

        Workspace existingWorkspace = new Workspace();
        existingWorkspace.setId(id);
        existingWorkspace.setName("OldName");
        existingWorkspace.setCreatedAt(Instant.now());

        when(repository.findById(id))
                .thenReturn(Optional.of(existingWorkspace));

        when(repository.save(any(Workspace.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        WorkspaceResponse result = service.update(id, request, userEmail);

        assertEquals(id.toString(), result.id());
        assertEquals("NewName", result.name());
        assertNotNull(result.createdAt());

        verify(workspaceAuthorizationService).requirePermission(
                id,
                userEmail,
                WorkspacePermission.WORKSPACE_UPDATE
        );

        verify(repository).findById(id);
        verify(repository).save(existingWorkspace);
    }

    @Test
    void updateShouldThrowExceptionWhenWorkspaceDoesNotExist() {

        UUID id = UUID.randomUUID();
        String userEmail = "owner@test.com";

        UpdateWorkspaceRequest request = new UpdateWorkspaceRequest();
        request.setName("NewName");

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(WorkspaceNotFoundException.class, () -> service.update(id, request, userEmail));

        verify(workspaceAuthorizationService).requirePermission(
                id,
                userEmail,
                WorkspacePermission.WORKSPACE_UPDATE
        );

        verify(repository).findById(id);
        verify(repository, never()).save(any(Workspace.class));
    }

    @Test
    void deleteShouldRemoveWorkspace() {

        UUID id = UUID.randomUUID();
        String userEmail = "owner@test.com";

        Workspace workspace = new Workspace();
        workspace.setId(id);
        workspace.setName("Igor");
        workspace.setCreatedAt(Instant.now());

        when(repository.findById(id))
                .thenReturn(Optional.of(workspace));

        service.delete(id, userEmail);

        verify(workspaceAuthorizationService).requirePermission(
                id,
                userEmail,
                WorkspacePermission.WORKSPACE_DELETE
        );

        verify(repository).findById(id);
        verify(repository).delete(workspace);
        verify(workspaceMemberRepository).deleteByIdWorkspaceId(id);
    }

    @Test
    void deleteShouldThrowExceptionWhenWorkspaceDoesNotExist() {

        UUID id = UUID.randomUUID();
        String userEmail = "owner@test.com";

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(WorkspaceNotFoundException.class,
                () -> service.delete(id, userEmail));

        verify(workspaceAuthorizationService).requirePermission(
                id,
                userEmail,
                WorkspacePermission.WORKSPACE_DELETE
        );

        verify(repository).findById(id);
        verify(repository, never()).delete(any(Workspace.class));
        verify(workspaceMemberRepository, never()).deleteByIdWorkspaceId(id);
    }

    @Test
    void getAllShouldReturnWorkspaces() {

        Pageable pageable = PageRequest.of(0, 10);

        Workspace workspace1 = new Workspace();
        workspace1.setId(UUID.randomUUID());
        workspace1.setName("User1");
        workspace1.setCreatedAt(Instant.now());

        Workspace workspace2 = new Workspace();
        workspace2.setId(UUID.randomUUID());
        workspace2.setName("User2");
        workspace2.setCreatedAt(Instant.now());

        Page<Workspace> page = new PageImpl<>(List.of(workspace1, workspace2));

        when(repository.findAll(pageable))
                .thenReturn(page);

        Page<Workspace> result = service.getAll(null, pageable);

        assertEquals(2, result.getContent().size());
        assertEquals("User1", result.getContent().get(0).getName());
        assertEquals("User2", result.getContent().get(1).getName());

        verify(repository).findAll(pageable);
    }
}
