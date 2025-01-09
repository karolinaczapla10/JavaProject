package com.karolinaczapla.usermanagementsystem.service;

import com.karolinaczapla.usermanagementsystem.dto.RequestResponse;
import com.karolinaczapla.usermanagementsystem.entity.Users;
import com.karolinaczapla.usermanagementsystem.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
public class UsersManagementService {

    @Autowired
    private UsersRepository usersRepository;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean checkEmailExists(String email) {
        return usersRepository.existsByEmail(email);
    }
    public RequestResponse register(@Valid RequestResponse registrationRequest, BindingResult result) {
        RequestResponse resp = new RequestResponse();

        if (usersRepository.findByEmail(registrationRequest.getEmail()).isPresent()) {
            result.rejectValue("email", "email.exists", "Email is already registered");
        }

        if (!"USER".equals(registrationRequest.getRole()) && !"ADMIN".equals(registrationRequest.getRole())) {
            result.rejectValue("role", "role.invalid", "Role must be either USER or ADMIN");
        }

        if (registrationRequest.getPassword() != null && registrationRequest.getPassword().length() < 3) {
            result.rejectValue("password", "password.short", "Password must be at least 3 characters long");
        }

        if (result.hasErrors()) {
            resp.setStatusCode(400);
            resp.setError(result.getAllErrors().toString());
            return resp;
        }

        try {
            Users ourUser = new Users();
            ourUser.setEmail(registrationRequest.getEmail());
            ourUser.setCity(registrationRequest.getCity());
            ourUser.setRole(registrationRequest.getRole());
            ourUser.setName(registrationRequest.getName());
            ourUser.setPassword(passwordEncoder.encode(registrationRequest.getPassword()));
            Users ourUsersResult = usersRepository.save(ourUser);
            if (ourUsersResult.getId() > 0) {
                resp.setOurUsers(ourUsersResult);
                resp.setMessage("User Saved Successfully");
                resp.setStatusCode(200);
            }
        } catch (Exception e) {
            resp.setStatusCode(500);
            resp.setMessage("User Not Saved ");
            resp.setError(e.getMessage());
        }
        return resp;
    }


    public RequestResponse login(RequestResponse loginRequest){
        RequestResponse response = new RequestResponse();
        try {
            authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
                            loginRequest.getPassword()));
            var user = usersRepository.findByEmail(loginRequest.getEmail()).orElseThrow();
            var jwt = jwtUtils.generateToken(user);
            var refreshToken = jwtUtils.generateRefreshToken(new HashMap<>(), user);
            response.setStatusCode(200);
            response.setToken(jwt);
            response.setRole(user.getRole());
            response.setRefreshToken(refreshToken);
            response.setExpirationTime("24Hrs");
            response.setMessage("Successfully Logged In");

        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
        }
        return response;
    }

    public RequestResponse refreshToken(RequestResponse refreshTokenReqiest){
        RequestResponse response = new RequestResponse();
        try{
            String ourEmail = jwtUtils.extractUsername(refreshTokenReqiest.getToken());
            Users users = usersRepository.findByEmail(ourEmail).orElseThrow();
            if (jwtUtils.isTokenValid(refreshTokenReqiest.getToken(), users)) {
                var jwt = jwtUtils.generateToken(users);
                response.setStatusCode(200);
                response.setToken(jwt);
                response.setRefreshToken(refreshTokenReqiest.getToken());
                response.setExpirationTime("24Hr");
                response.setMessage("Successfully Refreshed Token");
            }
            response.setStatusCode(200);
            return response;

        }catch (Exception e){
            response.setStatusCode(500);
            response.setMessage(e.getMessage());
            return response;
        }
    }


    public RequestResponse getAllUsers() {
        RequestResponse reqRes = new RequestResponse();

        try {
            List<Users> result = usersRepository.findAll();
            if (!result.isEmpty()) {
                reqRes.setOurUsersList(result);
                reqRes.setStatusCode(200);
                reqRes.setMessage("Successful");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("No users found");
            }
            return reqRes;
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
            return reqRes;
        }
    }


    public RequestResponse getUsersById(Integer id) {
        RequestResponse reqRes = new RequestResponse();
        try {
            Users usersById = usersRepository.findById(id).orElseThrow(() -> new RuntimeException("User Not found"));
            reqRes.setOurUsers(usersById);
            reqRes.setStatusCode(200);
            reqRes.setMessage("Users with id '" + id + "' found successfully");
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred: " + e.getMessage());
        }
        return reqRes;
    }


    public RequestResponse deleteUser(Integer userId) {
        RequestResponse reqRes = new RequestResponse();
        try {
            Optional<Users> userOptional = usersRepository.findById(userId);
            if (userOptional.isPresent()) {
                usersRepository.deleteById(userId);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User deleted successfully");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for deletion");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while deleting user: " + e.getMessage());
        }
        return reqRes;
    }

    public RequestResponse updateUser(Integer userId, @Valid Users updatedUser, BindingResult result) {
        RequestResponse reqRes = new RequestResponse();


        // Ręczna walidacja roli
        if (!"USER".equals(updatedUser.getRole()) && !"ADMIN".equals(updatedUser.getRole())) {
            result.rejectValue("role", "role.invalid", "Role must be either USER or ADMIN");
        }

        // Ręczna walidacja hasła (minimum 3 znaki)
        if (updatedUser.getPassword() != null && updatedUser.getPassword().length() < 3) {
            result.rejectValue("password", "password.short", "Password must be at least 3 characters long");
        }
        if (updatedUser.getEmail() != null) {
            Optional<Users> existingUserWithEmail = usersRepository.findByEmail(updatedUser.getEmail());
            if (existingUserWithEmail.isPresent() && !existingUserWithEmail.get().getId().equals(userId)) {
                result.rejectValue("email", "email.used", "Email is already in use by another user");
            }
        }
        if (result.hasErrors()) {
            reqRes.setStatusCode(400);
            reqRes.setError(result.getAllErrors().toString());
            return reqRes;
        }

        try {
            Optional<Users> userOptional = usersRepository.findById(userId);
            if (userOptional.isPresent()) {
                Users existingUser = userOptional.get();
                existingUser.setEmail(updatedUser.getEmail());
                existingUser.setName(updatedUser.getName());
                existingUser.setCity(updatedUser.getCity());
                existingUser.setRole(updatedUser.getRole());

                if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
                    existingUser.setPassword(passwordEncoder.encode(updatedUser.getPassword()));
                }

                Users savedUser = usersRepository.save(existingUser);
                reqRes.setOurUsers(savedUser);
                reqRes.setStatusCode(200);
                reqRes.setMessage("User updated successfully");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for update");
            }
        } catch (Exception e) {
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while updating user: " + e.getMessage());
        }
        return reqRes;
    }


    public RequestResponse getMyInfo(String email){
        RequestResponse reqRes = new RequestResponse();
        try {
            Optional<Users> userOptional = usersRepository.findByEmail(email);
            if (userOptional.isPresent()) {
                reqRes.setOurUsers(userOptional.get());
                reqRes.setStatusCode(200);
                reqRes.setMessage("successful");
            } else {
                reqRes.setStatusCode(404);
                reqRes.setMessage("User not found for update");
            }

        }catch (Exception e){
            reqRes.setStatusCode(500);
            reqRes.setMessage("Error occurred while getting user info: " + e.getMessage());
        }
        return reqRes;

    }
}