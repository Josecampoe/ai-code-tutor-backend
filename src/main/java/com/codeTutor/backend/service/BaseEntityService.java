package com.codeTutor.backend.service;

/**
 * Clase abstracta base que implementa el patrón Template Method.
 * Define el esqueleto del algoritmo de creación de entidades:
 * 1. Validar el request
 * 2. Construir la entidad
 * 3. Persistir la entidad
 * 4. Convertir a respuesta
 *
 * Justificación del patrón Template Method:
 * Se utiliza Template Method porque todos los servicios (UserService, ProjectService,
 * CodeSnapshotService) siguen el mismo flujo al crear una entidad: validar, construir,
 * guardar y retornar una respuesta. Sin este patrón, ese flujo se repite en cada servicio
 * con pequeñas variaciones, generando duplicación de código.
 * Template Method define el flujo una sola vez en la clase base y deja que cada subclase
 * implemente únicamente los pasos que le son propios, respetando el principio DRY
 * (Don't Repeat Yourself) y facilitando el mantenimiento.
 *
 * @param <REQ> Tipo del objeto request (DTO de entrada)
 * @param <RES> Tipo del objeto response (DTO de salida)
 * @param <ENT> Tipo de la entidad JPA
 */
public abstract class BaseEntityService<REQ, RES, ENT> {

    /**
     * Template Method: define el esqueleto del algoritmo de creación.
     * Los pasos están fijos en este orden; las subclases implementan cada paso.
     * Este método es final para que ninguna subclase pueda cambiar el flujo.
     */
    public final RES create(REQ request) {
        // Paso 1: Validar el request antes de procesar
        validate(request);

        // Paso 2: Construir la entidad a partir del request
        ENT entity = buildEntity(request);

        // Paso 3: Persistir la entidad en la base de datos
        ENT saved = persist(entity);

        // Paso 4: Convertir la entidad guardada a DTO de respuesta
        return toResponse(saved);
    }

    /**
     * Paso 1 — Validar el request.
     * Cada subclase define sus propias reglas de validación.
     * Lanza RuntimeException o IllegalArgumentException si el request es inválido.
     */
    protected abstract void validate(REQ request);

    /**
     * Paso 2 — Construir la entidad JPA a partir del request.
     * Cada subclase mapea los campos del DTO a la entidad correspondiente.
     */
    protected abstract ENT buildEntity(REQ request);

    /**
     * Paso 3 — Persistir la entidad en la base de datos.
     * Cada subclase llama a su repositorio correspondiente.
     */
    protected abstract ENT persist(ENT entity);

    /**
     * Paso 4 — Convertir la entidad a DTO de respuesta.
     * Cada subclase mapea los campos de la entidad al DTO de salida.
     */
    protected abstract RES toResponse(ENT entity);
}
