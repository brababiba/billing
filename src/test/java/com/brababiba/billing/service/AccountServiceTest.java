package com.brababiba.billing.service;

import com.brababiba.billing.dto.UpdateAccountRequest;
import com.brababiba.billing.exception.AccountNotFoundException;
import com.brababiba.billing.model.Account;
import com.brababiba.billing.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTest {

    @Mock
    private AccountRepository repository;

    @InjectMocks
    private AccountService service;

    @Test
    void createShouldSaveAccount() {
        when(repository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Account result = service.create("Igor");

        assertNotNull(result.getId());
        assertEquals("Igor", result.getName());
        assertNotNull(result.getCreatedAt());

        verify(repository).save(any(Account.class));
    }

    @Test
    void getByIdShouldThrowExceptionWhenAccountDoesNotExist() {

        UUID id = UUID.randomUUID();

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> service.getById(id));

        verify(repository).findById(id);
    }

    @Test
    void getByIdShouldReturnAccountWhenExists() {

        UUID id = UUID.randomUUID();

        Account account = new Account();
        account.setId(id);
        account.setName("Igor");
        account.setCreatedAt(Instant.now());

        when(repository.findById(id))
                .thenReturn(Optional.of(account));

        Account result = service.getById(id);

        assertEquals(id, result.getId());
        assertEquals("Igor", result.getName());
        assertNotNull(result.getCreatedAt());

        verify(repository).findById(id);
    }

    @Test
    void updateShouldChangeAccountName() {

        UUID id = UUID.randomUUID();

        UpdateAccountRequest request = new UpdateAccountRequest();
        request.setName("NewName");

        Account existingAcount = new Account();
        existingAcount.setId(id);
        existingAcount.setName("OldName");
        existingAcount.setCreatedAt(Instant.now());

        when(repository.findById(id))
                .thenReturn(Optional.of(existingAcount));

        when(repository.save(any(Account.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Account result = service.update(id, request);

        assertEquals(id, result.getId());
        assertEquals("NewName", result.getName());
        assertNotNull(result.getCreatedAt());

        verify(repository).findById(id);
        verify(repository).save(existingAcount);
    }

    @Test
    void updateShouldThrowExceptionWhenAccountDoesNotExist() {

        UUID id = UUID.randomUUID();

        UpdateAccountRequest request = new UpdateAccountRequest();
        request.setName("NewName");

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class, () -> service.update(id, request));

        verify(repository).findById(id);
        verify(repository, never()).save(any(Account.class));
    }

    @Test
    void deleteShouldRemoveAccount() {

        UUID id = UUID.randomUUID();

        Account account = new Account();
        account.setId(id);
        account.setName("Igor");
        account.setCreatedAt(Instant.now());

        when(repository.findById(id))
                .thenReturn(Optional.of(account));

        service.delete(id);

        verify(repository).findById(id);
        verify(repository).delete(account);
    }

    @Test
    void deleteShouldThrowExceptionWhenAccountDoesNotExist() {

        UUID id = UUID.randomUUID();

        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(AccountNotFoundException.class,
                () -> service.delete(id));

        verify(repository).findById(id);
        verify(repository, never()).delete(any(Account.class));
    }

    @Test
    void getAllShouldReturnAccounts() {

        Account account1 = new Account();
        account1.setId(UUID.randomUUID());
        account1.setName("User1");
        account1.setCreatedAt(Instant.now());

        Account account2 = new Account();
        account2.setId(UUID.randomUUID());
        account2.setName("User2");
        account2.setCreatedAt(Instant.now());

        when(repository.findAll())
                .thenReturn(List.of(account1, account2));

        List<Account> result = service.getAll();

        assertEquals(2, result.size());
        assertEquals("User1", result.get(0).getName());
        assertEquals("User2", result.get(1).getName());

        verify(repository).findAll();
    }
}
