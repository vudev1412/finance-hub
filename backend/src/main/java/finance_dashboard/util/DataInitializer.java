package finance_dashboard.util;

import finance_dashboard.domain.entity.Permission;
import finance_dashboard.domain.entity.Role;
import finance_dashboard.domain.entity.User;
import finance_dashboard.repository.PermissionRepository;
import finance_dashboard.repository.RoleRepository;
import finance_dashboard.repository.UserRepository;
import finance_dashboard.util.Permissions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String ROLE_USER = "ROLE_USER";
    private static final String ADMIN_EMAIL = "admin@finance.com";

    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        seedPermissionsAndRoles();
        seedAdminUser();
    }

    // ── 1. Seed Permissions + Roles ──────────────────────────

    private void seedPermissionsAndRoles() {
        Permission userRead = createPermission(Permissions.USER_READ);
        Permission userWrite = createPermission(Permissions.USER_WRITE);
        Permission transactionRead = createPermission(Permissions.TRANSACTION_READ);
        Permission transactionWrite = createPermission(Permissions.TRANSACTION_WRITE);
        Permission budgetRead = createPermission(Permissions.BUDGET_READ);
        Permission budgetWrite = createPermission(Permissions.BUDGET_WRITE);

        createRole(ROLE_ADMIN, Set.of(
                userRead, userWrite,
                transactionRead, transactionWrite,
                budgetRead, budgetWrite
        ));

        createRole(ROLE_USER, Set.of(
                transactionRead, transactionWrite,
                budgetRead, budgetWrite
        ));
    }

    private Permission createPermission(String name) {
        return permissionRepository.findByName(name)
                .orElseGet(() -> {
                    log.info("Creating permission: {}", name);
                    return permissionRepository.save(
                            Permission.builder().name(name).build());
                });
    }

    private void createRole(String roleName, Set<Permission> permissions) {
        roleRepository.findByName(roleName)
                .orElseGet(() -> {
                    log.info("Creating role: {}", roleName);
                    return roleRepository.save(
                            Role.builder()
                                    .name(roleName)
                                    .permissions(permissions)
                                    .build());
                });
    }

    // ── 2. Seed Admin User ────────────────────────────────────

    private void seedAdminUser() {
        if (userRepository.existsByEmail(ADMIN_EMAIL)) {
            return;
        }

        Role adminRole = roleRepository.findByName(ROLE_ADMIN)
                .orElseThrow(() -> new IllegalStateException(
                        "Role " + ROLE_ADMIN + " must be seeded before creating admin user"));

        User admin = User.builder()
                .name("Admin")
                .email(ADMIN_EMAIL)
                .password(passwordEncoder.encode("123456"))
                .roles(Set.of(adminRole))
                .build();

        userRepository.save(admin);
        log.info("Admin user created: {}", admin.getEmail());
    }
}