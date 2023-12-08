package com.panacademy.squad7.bluebank.domain.repositories;

import com.panacademy.squad7.bluebank.domain.enums.RoleType;
import com.panacademy.squad7.bluebank.domain.models.Client;
import com.panacademy.squad7.bluebank.domain.models.User;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityNotFoundException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
class UsersRepositoryTest {

    @Autowired
    private UsersRepository usersRepository;

    private User user;
    private Client client;
    @BeforeEach
    public void setupTestData(){
        // Given : Setup object or precondition
        LocalDate date = LocalDate.of(2020, 1, 8);


        client = client.builder()
               // .id(1L)
                .name("Teste")
                .lastname("Teste")
                .birthDate(date )
                .motherName("Teste")
                .email("cliente@cliente.com")
                .build();

        user = user.builder()
                .id(1L)
                .username("Teste")
                .password("123456")
                .role(RoleType.A)
                .client(client)
                .build();
    }

    @Test
    @DisplayName(" Test for save User operation")
    public void givenUserObject_whenSave_thenReturnSaveEmployee(){
        User saveUser = usersRepository.save(user);

        assert(saveUser.getUsername()).equals(user.getUsername());
        assert(saveUser.getRole()).equals(user.getRole());
        assert(saveUser.getPassword()).equals(user.getPassword());


    }
    @Test
    @DisplayName("JUnit test for user a UserId nonexistent")
    public void WhenSendIdNonexistent ()  {
//      try {
          User user = usersRepository.getById(10L);
       //   fail("Expected an EntityNotFoundException to be thrown");

//        when(usersRepository.findById(10L)).thenReturn(Optional.empty());
       //  (usersRepository.getById(10L)).thenThrow(new NullPointerException("Error occurred"));

//        assertThrows(NullPointerException.class, () -> dictMock.getMeaning("word"));

    }
}