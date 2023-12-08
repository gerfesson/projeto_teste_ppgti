package com.panacademy.squad7.bluebank.services.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import com.panacademy.squad7.bluebank.domain.models.Address;
import com.panacademy.squad7.bluebank.domain.models.Client;
import com.panacademy.squad7.bluebank.domain.repositories.AddressesRepository;
import com.panacademy.squad7.bluebank.exceptions.InvalidInputException;

@SpringBootTest
public class AddressesServiceImplTests {
    private final AddressesRepository mockAddressesRepository = mock(AddressesRepository.class);
    private final AddressesServiceImpl service = new AddressesServiceImpl(mockAddressesRepository);
    private Address inputAddress;
    private Address expectedAddress;

    @Before
    public void setup() {
        inputAddress = new Address() {
            {
            setStreet("AV Salgado Filho");
            setClient(new Client() {
                {
                setId(Long.valueOf(1234));
                }
            });
            }
        };

        expectedAddress = new Address() {
            {
            setStreet("AV Salgado Filho");
            setClient(new Client() {
                {
                setId(Long.valueOf(1234));
                }
            });
            }
        };
    }

    @Test
    public void testCreateAddressWithSuccess() {
        Optional<Address> optionalAddress = Optional.of(expectedAddress);

        Mockito.when(mockAddressesRepository.save(inputAddress)).thenReturn(inputAddress);
        Mockito.when(mockAddressesRepository.findByClientId(inputAddress.getId())).thenReturn(optionalAddress);

        Address actualAddress = service.create(inputAddress);

        assertEquals(expectedAddress.getStreet(), actualAddress.getStreet());
    }

    @Test
    public void testCreateAddressWithUnexpectedException() {
        Optional<Address> optionalAddress = Optional.of(expectedAddress);

        Mockito.when(mockAddressesRepository.save(inputAddress)).thenThrow(new InvalidInputException("Error"));
        Mockito.when(mockAddressesRepository.findByClientId(1234L)).thenReturn(optionalAddress);

        assertThrows(RuntimeException.class, () -> {
            service.create(inputAddress);
        });
    }

    @Test
    public void testCreateAddressWithInvalidInputException() {
        inputAddress.setId(1L);
        expectedAddress.setId(12L);
        Optional<Address> optionalAddress = Optional.of(expectedAddress);

        Mockito.when(mockAddressesRepository.save(inputAddress)).thenThrow(new InvalidInputException("Error"));
        Mockito.when(mockAddressesRepository.findByClientId(1234L)).thenReturn(optionalAddress);

        assertThrows(InvalidInputException.class, () -> {
            service.create(inputAddress);
        });
    }
    
}
