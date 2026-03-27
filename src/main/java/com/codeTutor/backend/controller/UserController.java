package com.codeTutor.backend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.codeTutor.backend.dto.request.CreateUserRequest;
import com.codeTutor.backend.dto.request.LoginRequest;
import com.codeTutor.backend.dto.response.LoginResponse;
import com.codeTutor.backend.dto.response.UserResponse;
import com.codeTutor.backend.service.UserService;

import jakarta.validation.Valid;

/**
 * Controlador REST para la gestión de usuarios.
 * Expone endpoints para registrar, consultar y listar usuarios.
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    // Servicio de usuarios inyectado
    @Autowired
    private UserService userService;

    /**
     * POST /api/users
     * Registra un nuevo usuario en el sistema.
     */
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse response = userService.createUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * GET /api/users/{id}
     * Retorna un usuario por su ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(@PathVariable Long id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/users/email/{email}
     * Retorna un usuario por su email.
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {
        UserResponse response = userService.getUserByEmail(email);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/users
     * Retorna la lista de todos los usuarios registrados.
     */
    @GetMapping
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> response = userService.getAllUsers();
        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/users/login
     * Verifica las credenciales del usuario y retorna sus datos si son correctas.
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(response);
    }
}
