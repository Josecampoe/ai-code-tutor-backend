package com.codeTutor.backend.service;

/**
 * Interfaz del patrón Observer que define el contrato para todos los componentes
 * que deseen ser notificados cuando el código del estudiante cambia.
 * Permite desacoplar el emisor del evento de sus receptores.
 */
public interface CodeChangeObserver {

    /**
     * Método invocado automáticamente cuando se detecta un cambio en el código.
     * Cada implementación define cómo reaccionar ante el cambio.
     */
    void onCodeChanged(String newCode, String language);
}
