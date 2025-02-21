package com.ilo.energyallocation.migrations;

import com.ilo.energyallocation.user.dto.UserRegistrationRequestDTO;
import com.ilo.energyallocation.user.model.EnergyPreference;
import com.ilo.energyallocation.user.model.IloUser;
import com.ilo.energyallocation.user.model.Role;
import com.ilo.energyallocation.user.service.interfaces.IUserService;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.List;

@RequiredArgsConstructor
@ChangeUnit(id = "userMigration001", order = "001", author = "miguel", systemVersion = "1")
public class UserMigration {
    private final IUserService userService;
    private final MongoTemplate template;

    @Execution
    public void addInitialUsers() {
        List<UserRegistrationRequestDTO> users = List.of(
                createUser("alice", "alice@email.com", "password1", List.of(Role.USER), EnergyPreference.SOLAR),
                createUser("bob", "bob@email.com", "password2", List.of(Role.USER), EnergyPreference.WIND),
                createUser("charlie", "charlie@email.com", "password3", List.of(Role.USER), EnergyPreference.HYDRO),
                createUser("dave", "dave@email.com", "password4", List.of(Role.USER), EnergyPreference.NO_PREFERENCE),
                createUser(
                        "admin", "admin@email.com", "adminpass", List.of(Role.USER, Role.ADMIN),
                        EnergyPreference.NO_PREFERENCE
                )
        );

        users.forEach(userService::createUser);
    }

    private UserRegistrationRequestDTO createUser(
            String username, String email, String password, List<Role> roles, EnergyPreference preference) {
        return UserRegistrationRequestDTO.builder()
                .username(username)
                .email(email)
                .password(password)
                .roles(roles)
                .preference(preference)
                .build();
    }

    @RollbackExecution
    public void rollback() {
        template.remove(IloUser.class).all();
    }
}
