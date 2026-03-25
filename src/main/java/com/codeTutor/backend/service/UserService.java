package com.codeTutor.backend.service;

import com.codeTutor.backend.dto.request.CreateUserRequest;
import com.codeTutor.backend.dto.response.UserResponse;
import com.codeTutor.backend.model.User;
import com.codeTutor.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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
     * Paso 1 — Valida que el email no esté ya registrado en el sistema.
     */
    @Override
    protected void validate(CreateUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con el email: " + request.getEmail());
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
}
