package com.bank.monolith.domain.repository;

import com.bank.monolith.domain.entity.User;
import com.bank.monolith.domain.enums.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUserByEmail() {
        // Arrange
        User user = User.builder()
                .email("john.doe@example.com")
                .password("encoded_password")
                .firstName("John")
                .lastName("Doe")
                .phoneNumber("+1234567890")
                .role(Role.ROLE_CUSTOMER)
                .build();

        // Act
        userRepository.save(user);
        Optional<User> found = userRepository.findByEmail("john.doe@example.com");

        // Assert
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("John");
        assertThat(found.get().getRole()).isEqualTo(Role.ROLE_CUSTOMER);
    }

    @Test
    void shouldCheckIfExistsByEmail() {
        // Arrange
        User user = User.builder()
                .email("jane.doe@example.com")
                .password("encoded_password")
                .firstName("Jane")
                .lastName("Doe")
                .phoneNumber("+0987654321")
                .role(Role.ROLE_ADMIN)
                .build();

        userRepository.save(user);

        // Act & Assert
        assertThat(userRepository.existsByEmail("jane.doe@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("non-existent@example.com")).isFalse();
    }
}
