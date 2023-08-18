package com.pointers.ices4hu.services;

import com.pointers.ices4hu.models.Department;
import com.pointers.ices4hu.models.Schedule;
import com.pointers.ices4hu.models.User;
import com.pointers.ices4hu.repositories.DepartmentRepository;
import com.pointers.ices4hu.repositories.ScheduleRepository;
import com.pointers.ices4hu.security.password.PasswordGenerationManager;
import com.pointers.ices4hu.types.UserType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

@Component
public class StartupService implements ApplicationListener<ApplicationReadyEvent> {

    private final UserService userService;
    private final PasswordGenerationManager passwordGenerationManager;
    private final PasswordEncoder passwordEncoder;
    private final ScheduleRepository scheduleRepository;
    private final DepartmentRepository departmentRepository;

    @Value("${ices4hu.general.admin.username}")
    private String ADMIN_USERNAME;

    public StartupService(UserService userService,
                          PasswordGenerationManager passwordGenerationManager,
                          PasswordEncoder passwordEncoder,
                          ScheduleRepository scheduleRepository,
                          DepartmentRepository departmentRepository) {
        this.userService = userService;
        this.passwordGenerationManager = passwordGenerationManager;
        this.passwordEncoder = passwordEncoder;
        this.scheduleRepository = scheduleRepository;
        this.departmentRepository = departmentRepository;
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {

        createAdminAccountIfNotExists();
        createScheduleRecordIfNotExists();
        createDepartmentsIfNotExists();

    }

    private void createDepartmentsIfNotExists() {
        List<String> departments = Stream.of("Computer Engineering", "Mathematics",
                "Literature", "Physics", "Psychology").toList();

        for(int i = 0; i < departments.size(); i++) {
            long id = i + 1;
            String departmentName = departments.get(i);
            Department department = departmentRepository.findById(id).orElse(null);
            if (department == null || !department.getName().equals(departmentName)) {
                department = new Department();
                department.setId(id);
                department.setName(departmentName);
                departmentRepository.save(department);
            }
        }
    }

    private void createAdminAccountIfNotExists() {
        User user = userService.getUserByLoginID(ADMIN_USERNAME);
        if (user == null) {
            user = new User();
            user.setLoginID(ADMIN_USERNAME);
            user.setName("ICES4HU");
            user.setSurname("Admin");
            user.setUserType(UserType.ADMIN.getValue());
            user.setBanned(false);
            user.setRegistrationDateTime(LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault()));

            String password = passwordGenerationManager.generatePassword();
            // encode the password so that it does not get saved
            // in plain text format
            user.setPassword(passwordEncoder.encode(password));

            userService.createUser(user);

            System.out.println("[INFO] New admin account has been created!");
            System.out.println("[INFO] Username: " + ADMIN_USERNAME);
            System.out.println("[INFO] Password: " + password);
            System.out.println("[WARNING] Save this information! You will not be able to see it again.");

        }
    }

    private void createScheduleRecordIfNotExists() {
        Schedule schedule = scheduleRepository.findById(1L).orElse(null);
        if (schedule == null) {
            schedule = new Schedule();
            schedule.setId(1L);
            scheduleRepository.save(schedule);
        }
    }

}
