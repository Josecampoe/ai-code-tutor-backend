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
public abstract class BaseEntityService<REQUEST, RESPONSE, ENTITY> {

    /**
     * Template Method: define el esqueleto del algoritmo de creación.
     * Los pasos están fijos en este orden; las subclases implementan cada paso.
     * Este método es final para que ninguna subclase pueda cambiar el flujo.
     */
    public final RESPONSE create(REQUEST request) {
        validate(request);
        ENTITY entity = buildEntity(request);
        ENTITY saved = persist(entity);
        return toResponse(saved);
    }

    protected abstract void validate(REQUEST request);
    protected abstract ENTITY buildEntity(REQUEST request);
    protected abstract ENTITY persist(ENTITY entity);
    protected abstract RESPONSE toResponse(ENTITY entity);
}
