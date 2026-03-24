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
 * Maneja el registro, búsqueda y listado de usuarios.
 */
@Service
public class UserService {

    // Repositorio de usuarios para operaciones de base de datos
    @Autowired
    private UserRepository userRepository;

    /**
     * Crea un nuevo usuario en el sistema después de validar que el email no esté registrado.
     * Retorna un UserResponse sin la contraseña por seguridad.
     */
    public UserResponse createUser(CreateUserRequest request) {
        // Verificar que el email no esté ya registrado en el sistema
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Ya existe un usuario con el email: " + request.getEmail());
        }

        // Construir el objeto User a partir del request recibido
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        // Guardar el usuario en la base de datos
        User savedUser = userRepository.save(user);

        // Retornar la respuesta sin exponer la contraseña
        return toResponse(savedUser);
    }

    /**
     * Busca un usuario por su ID y lanza excepción si no existe.
     */
    public UserResponse getUserById(Long id) {
        // Buscar el usuario o lanzar excepción con mensaje en español
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        return toResponse(user);
    }

    /**
     * Busca un usuario por su email y lanza excepción si no existe.
     */
    public UserResponse getUserByEmail(String email) {
        // Buscar el usuario por email o lanzar excepción
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con email: " + email));
        return toResponse(user);
    }

    /**
     * Retorna la lista completa de usuarios registrados en el sistema.
     */
    public List<UserResponse> getAllUsers() {
        // Obtener todos los usuarios y convertirlos a respuesta sin contraseña
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * Convierte un objeto User a UserResponse, excluyendo la contraseña.
     */
    private UserResponse toResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
