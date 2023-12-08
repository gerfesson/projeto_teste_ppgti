package com.panacademy.squad7.bluebank.services.impl;

import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.panacademy.squad7.bluebank.domain.models.Client;
import com.panacademy.squad7.bluebank.domain.repositories.AccountsRepository;
import com.panacademy.squad7.bluebank.domain.repositories.ClientsRepository;
import com.panacademy.squad7.bluebank.exceptions.ContentNotFoundException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.mock;
import org.mockito.Mockito;
import java.util.Optional;

@SpringBootTest
public class ClientsServiceImplTests {

    private final ClientsRepository mockClientsRepository = mock(ClientsRepository.class);
    private final AccountsRepository mockAccountsRepository = mock(AccountsRepository.class);
    private final ClientsServiceImpl service = new ClientsServiceImpl(mockClientsRepository, mockAccountsRepository);

    private Client inputClient;
    private Client expectedClient;

    @Before
    public void setup() {
        inputClient = new Client() {
            {
            setId(Long.valueOf(1234));
            }
        };

        expectedClient = new Client() {
            {
            setId(Long.valueOf(1234));
            }
        };
    }

    @Test
    public void testCreateClientWithSuccess() {
        Mockito.when(mockClientsRepository.save(inputClient)).thenReturn(inputClient);

        Client actualClient = service.create(inputClient);

        assertEquals(expectedClient.getId(), actualClient.getId());
    }

    @Test
    public void testFindClientByIdWithSuccess() {
        Mockito.when(mockClientsRepository.findById(inputClient.getId())).thenReturn(Optional.of(inputClient));

        Client actualClient = service.findById(inputClient.getId());

        assertEquals(expectedClient.getId(), actualClient.getId());
    }
    
    @Test
    public void testFindClientByIdWithFailure() {
        Mockito.when(mockClientsRepository.findById(inputClient.getId())).thenReturn(Optional.empty());

        assertThrows(ContentNotFoundException.class, () -> service.findById(inputClient.getId()));
    }
}
