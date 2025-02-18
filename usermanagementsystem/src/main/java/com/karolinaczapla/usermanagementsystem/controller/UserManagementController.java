package com.karolinaczapla.usermanagementsystem.controller;

import com.karolinaczapla.usermanagementsystem.dto.RequestResponse;
import com.karolinaczapla.usermanagementsystem.entity.Users;
import com.karolinaczapla.usermanagementsystem.service.UsersManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
public class UserManagementController {
    @Autowired
    private UsersManagementService usersManagementService;

    @GetMapping("/auth/check-email")
    public ResponseEntity<Boolean> checkEmailExists(@RequestParam String email) {
        boolean emailExists = usersManagementService.checkEmailExists(email);
        return ResponseEntity.ok(emailExists);
    }
    @PostMapping("/auth/register")
    public ResponseEntity<RequestResponse> register(@Valid @RequestBody RequestResponse reg, BindingResult result) {
        return ResponseEntity.ok(usersManagementService.register(reg, result));
    }

    @PostMapping("/auth/login")
    public ResponseEntity<RequestResponse> login(@RequestBody RequestResponse req){
        return ResponseEntity.ok(usersManagementService.login(req));
    }

    @PostMapping("/auth/refresh")
    public ResponseEntity<RequestResponse> refreshToken(@RequestBody RequestResponse req){
        return ResponseEntity.ok(usersManagementService.refreshToken(req));
    }
    @GetMapping("/admin/get-all-users")
    public ResponseEntity<RequestResponse> getAllUsers(){
        return ResponseEntity.ok(usersManagementService.getAllUsers());

    }

    @GetMapping("/admin/get-users/{userId}")
    public ResponseEntity<RequestResponse> getUSerByID(@PathVariable Integer userId){
        return ResponseEntity.ok(usersManagementService.getUsersById(userId));

    }

    @PutMapping("/admin/update/{userId}")
    public ResponseEntity<RequestResponse> updateUser(@PathVariable Integer userId, @Valid @RequestBody Users reqres, BindingResult result) {
        return ResponseEntity.ok(usersManagementService.updateUser(userId, reqres, result));
    }

    @GetMapping("/adminuser/get-profile")
    public ResponseEntity<RequestResponse> getMyProfile(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        RequestResponse response = usersManagementService.getMyInfo(email);
        return  ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/admin/delete/{userId}")
    public ResponseEntity<RequestResponse> deleteUSer(@PathVariable Integer userId){
        return ResponseEntity.ok(usersManagementService.deleteUser(userId));
    }

}
