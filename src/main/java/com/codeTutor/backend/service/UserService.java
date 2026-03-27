package com.codeTutor.backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codeTutor.backend.dto.request.CreateUserRequest;
import com.codeTutor.backend.dto.request.LoginRequest;
import com.codeTutor.backend.dto.response.LoginResponse;
import com.codeTutor.backend.dto.response.UserResponse;
import com.codeTutor.backend.model.User;
import com.codeTutor.backend.repository.UserRepository;

/**
 * Servicio que gestiona las operaciones relacionadas con los usuarios de la plataforma.
 * Extiende BaseEntityService para aplicar el patrón Template Method en la creación de usuarios.
 * El flujo de creación (validar → construir → persistir → responder) está definido en la clase base.
 */
@Service
public class UserService extends BaseEntityService<CreateUserRequest, UserResponse, User> {

    // Repositorio de usuarios para operaciones de base de datos
    @Autowired
    private UserRepository userRepository;

    // =========================================================
    // IMPLEMENTACIÓN DEL PATRÓN TEMPLATE METHOD
    // =========================================================

    /**
     * Paso 1 — Valida que el email y el username no estén ya registrados en el sistema.
     */
    @Override
    protected void validate(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con el email: " + request.getEmail());
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Ya existe un usuario con el nombre: " + request.getUsername());
        }
    }

    /**
     * Paso 2 — Construye el objeto User a partir del request.
     */
    @Override
    protected User buildEntity(CreateUserRequest request) {
        return User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();
    }

    /**
     * Paso 3 — Persiste el usuario en la base de datos.
     */
    @Override
    protected User persist(User user) {
        return userRepository.save(user);
    }

    /**
     * Paso 4 — Convierte el User guardado a UserResponse sin exponer la contraseña.
     */
    @Override
    protected UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }

    // =========================================================
    // MÉTODOS ADICIONALES (no forman parte del Template Method)
    // =========================================================

    /**
     * Crea un nuevo usuario usando el flujo del Template Method heredado de BaseEntityService.
     * Mantiene el nombre original para compatibilidad con UserController.
     */
    public UserResponse createUser(CreateUserRequest request) {
        // Delegar al Template Method de la clase base (validate → build → persist → toResponse)
        return create(request);
    }

    /**
     * Busca un usuario por su ID y lanza excepción si no existe.
     */
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        return toResponse(user);
    }

    /**
     * Busca un usuario por su email y lanza excepción si no existe.
     */
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
        return toResponse(user);
    }

    /**
     * Retorna la lista completa de usuarios registrados en el sistema.
     */
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Verifica las credenciales del usuario y retorna sus datos si son correctas.
     * Lanza excepción si el email no existe o la contraseña no coincide.
     */
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("No existe una cuenta con ese email"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        return LoginResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .message("Inicio de sesión exitoso")
                .build();
    }
}
