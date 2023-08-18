package com.pointers.ices4hu.controllers;

import com.pointers.ices4hu.models.User;
import com.pointers.ices4hu.requests.AddInstructorRequest;
import com.pointers.ices4hu.responses.MessageResponse;
import com.pointers.ices4hu.responses.UserResponseProfilePicture;
import com.pointers.ices4hu.services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //@PostMapping
    //public User createUser(@RequestBody User user){
    //    return userService.createUser(user);
    //}


    //@GetMapping("/{userId}")
    //public User getUser(@PathVariable Long userId){
    //    return userService.getUser(userId);
    //}

    //@PutMapping("/{userId}")
    //public User updateUser(@PathVariable Long userId, @RequestBody User updatedUser){
    //    return userService.updateUser(userId, updatedUser);
    //}


    @DeleteMapping("/{loginID}")
    @PreAuthorize("hasAnyAuthority('department_manager', 'instructor', 'student')")
    public ResponseEntity<Object> deleteUser(@PathVariable String loginID) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals(loginID))
            return new ResponseEntity<>(
                    new MessageResponse("The user is not authorized to delete another user!"),
                    HttpStatus.UNAUTHORIZED
            );

        User user = userService.getUserByLoginID(loginID);
        if (user == null)
            return new ResponseEntity<>(new MessageResponse("No such user!"), HttpStatus.UNAUTHORIZED);

        return userService.deleteUser(user.getId());

    }

    @PostMapping("/admin/create_instructor")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Object> createInstructor(@RequestBody AddInstructorRequest addInstructorRequest) {
        return userService.createInstructor(addInstructorRequest);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Object> getUsers() {
        return userService.getUsers();
    }

    @DeleteMapping("/admin")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Object> deleteUser(@RequestParam Long user) {
        return userService.deleteUser(user);
    }

    @PostMapping("/admin/ban")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Object> banUser(@RequestParam Long user) {
        return userService.banUser(user);
    }

    @PostMapping("/admin/remove_ban")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<Object> removeBanOfUser(@RequestParam Long user) {
        return userService.removeBanOfUser(user);
    }

    @GetMapping
    @PreAuthorize("hasAnyAuthority('admin', 'department_manager', 'instructor', 'student')")
    public ResponseEntity<Object> getUserInformation(@RequestParam String loginId) {
        return userService.getUserInformation(loginId);
    }

    @PutMapping
    @PreAuthorize("hasAnyAuthority('admin', 'department_manager', 'instructor', 'student')")
    public ResponseEntity<Object> updateUserInformation(@RequestParam String loginId,
                                                        @RequestBody UserResponseProfilePicture
                                                                userResponseProfilePicture) {
        return userService.updateUserInformation(loginId, userResponseProfilePicture);
    }

}
