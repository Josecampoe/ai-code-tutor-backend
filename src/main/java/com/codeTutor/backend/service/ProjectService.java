package com.codeTutor.backend.service;

import com.codeTutor.backend.model.Project;
import com.codeTutor.backend.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * Servicio de gestión de proyectos del estudiante.
 * Maneja el guardado, actualización y recuperación de proyectos,
 * así como el historial de versiones y las operaciones de deshacer/rehacer.
 *
 * Implementa dos patrones de diseño:
 * - Command: para encapsular operaciones del editor como objetos ejecutables y reversibles.
 * - Uso de estructuras de datos apropiadas para historial y pila de comandos.
 */
@Service
public class ProjectService {

    /**
     * Justificación del patrón Command:
     * Se utiliza Command porque el editor de código necesita soportar operaciones de deshacer (undo)
     * y rehacer (redo). Al encapsular cada acción (escribir, borrar) como un objeto Command,
     * es posible almacenarlas en pilas y ejecutarlas o revertirlas de forma ordenada,
     * sin acoplar la lógica de cada operación al servicio principal.
     */

    // Repositorio de proyectos inyectado para persistencia en base de datos
    @Autowired
    private ProjectRepository projectRepository;

    /**
     * Historial completo de versiones del código del proyecto.
     * Se usa LinkedList porque permite inserción eficiente al inicio y al final,
     * y el historial crece dinámicamente sin necesidad de redimensionar como un ArrayList.
     * Es ideal para listas de versiones donde se agregan y recorren secuencialmente.
     */
    private LinkedList<String> versionHistory = new LinkedList<>();

    /**
     * Pila de comandos ejecutados, usada para la funcionalidad de deshacer (undo).
     * Se usa Stack porque sigue el principio LIFO (último en entrar, primero en salir),
     * lo que permite deshacer siempre la acción más reciente primero, que es el comportamiento
     * esperado en cualquier editor de texto o código.
     */
    private Stack<EditorCommand> undoStack = new Stack<>();

    /**
     * Pila de comandos deshechos, usada para la funcionalidad de rehacer (redo).
     * También usa Stack por la misma razón LIFO: al rehacer, se recupera el último
     * comando deshecho, manteniendo el orden correcto de las operaciones.
     */
    private Stack<EditorCommand> redoStack = new Stack<>();

    // =========================================================
    // PATRÓN COMMAND: Interfaz y clases concretas
    // =========================================================

    /**
     * Interfaz del patrón Command que define las operaciones básicas de cualquier comando del editor.
     * Permite ejecutar y deshacer acciones de forma uniforme.
     */
    public interface EditorCommand {
        /**
         * Ejecuta la acción del comando.
         */
        void execute();

        /**
         * Revierte la acción del comando, restaurando el estado anterior.
         */
        void undo();
    }

    /**
     * Comando concreto que representa la acción de escribir texto en el editor.
     * Almacena el texto nuevo y el estado anterior para poder revertir la operación.
     */
    public static class WriteCommand implements EditorCommand {

        // Texto que se va a escribir en el editor
        private final String textToWrite;

        // Estado del código antes de ejecutar el comando (para poder deshacer)
        private final String previousState;

        // Referencia al contenedor del estado actual del código
        private final StringBuilder currentCode;

        /**
         * Constructor que inicializa el comando con el texto a escribir y el estado previo.
         */
        public WriteCommand(String textToWrite, String previousState, StringBuilder currentCode) {
            this.textToWrite = textToWrite;
            this.previousState = previousState;
            this.currentCode = currentCode;
        }

        /**
         * Ejecuta el comando: agrega el texto al código actual.
         */
        @Override
        public void execute() {
            // Agregar el nuevo texto al código existente en el editor
            currentCode.append(textToWrite);
        }

        /**
         * Deshace el comando: restaura el código al estado anterior a la escritura.
         */
        @Override
        public void undo() {
            // Restaurar el código al estado previo eliminando el texto agregado
            currentCode.setLength(0);
            currentCode.append(previousState);
        }
    }

    /**
     * Comando concreto que representa la acción de eliminar texto del editor.
     * Almacena el texto eliminado y su posición para poder restaurarlo al deshacer.
     */
    public static class DeleteCommand implements EditorCommand {

        // Texto que fue eliminado (necesario para restaurarlo al deshacer)
        private final String deletedText;

        // Posición en el código donde se realizó la eliminación
        private final int position;

        // Referencia al contenedor del estado actual del código
        private final StringBuilder currentCode;

        /**
         * Constructor que inicializa el comando con el texto eliminado y su posición.
         */
        public DeleteCommand(String deletedText, int position, StringBuilder currentCode) {
            this.deletedText = deletedText;
            this.position = position;
            this.currentCode = currentCode;
        }

        /**
         * Ejecuta el comando: elimina el texto en la posición indicada.
         */
        @Override
        public void execute() {
            // Verificar que la posición y longitud sean válidas antes de eliminar
            int endPosition = position + deletedText.length();
            if (position >= 0 && endPosition <= currentCode.length()) {
                currentCode.delete(position, endPosition);
            }
        }

        /**
         * Deshace el comando: reinserta el texto eliminado en su posición original.
         */
        @Override
        public void undo() {
            // Reinsertar el texto eliminado en la posición donde estaba originalmente
            if (position >= 0 && position <= currentCode.length()) {
                currentCode.insert(position, deletedText);
            }
        }
    }

    // =========================================================
    // MÉTODOS PRINCIPALES DEL SERVICIO
    // =========================================================

    /**
     * Guarda un proyecto en la base de datos después de validar sus campos obligatorios.
     * También registra el código inicial en el historial de versiones.
     */
    public Project saveProject(Project project) {
        // Validar que el nombre del proyecto no sea nulo ni vacío
        if (project.getName() == null || project.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del proyecto no puede estar vacío.");
        }

        // Validar que el código del proyecto no sea nulo ni vacío
        if (project.getCode() == null || project.getCode().trim().isEmpty()) {
            throw new IllegalArgumentException("El código del proyecto no puede estar vacío.");
        }

        // Guardar la versión inicial del código en el historial
        versionHistory.add(project.getCode());

        // Persistir el proyecto en la base de datos y retornar el objeto guardado
        return projectRepository.save(project);
    }

    /**
     * Recupera un proyecto de la base de datos por su identificador único.
     * Lanza una excepción en español si el proyecto no existe.
     */
    public Project getProject(Long id) {
        // Buscar el proyecto por ID y lanzar excepción si no se encuentra
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con el ID: " + id));
    }

    /**
     * Actualiza el código de un proyecto existente.
     * Guarda el código anterior en el historial antes de aplicar el cambio.
     */
    public Project updateProjectCode(Long id, String newCode) {
        // Obtener el proyecto existente para actualizar su código
        Project project = getProject(id);

        // Guardar el código anterior en el historial antes de sobreescribirlo
        versionHistory.add(project.getCode());

        // Actualizar el código del proyecto con el nuevo valor
        project.setCode(newCode);

        // Persistir los cambios y retornar el proyecto actualizado
        return projectRepository.save(project);
    }

    /**
     * Ejecuta un comando del editor, lo agrega a la pila de deshacer
     * y limpia la pila de rehacer (una nueva acción invalida el historial de redo).
     */
    public void executeCommand(EditorCommand command) {
        // Ejecutar la acción del comando
        command.execute();

        // Agregar el comando ejecutado a la pila de deshacer
        undoStack.push(command);

        // Limpiar la pila de rehacer porque una nueva acción rompe el historial de redo
        redoStack.clear();
    }

    /**
     * Deshace el último comando ejecutado moviéndolo de la pila undo a la pila redo.
     */
    public void undo() {
        // Verificar que haya comandos disponibles para deshacer
        if (!undoStack.isEmpty()) {
            // Obtener el último comando ejecutado
            EditorCommand command = undoStack.pop();

            // Revertir la acción del comando
            command.undo();

            // Mover el comando a la pila de rehacer para poder repetirlo si se desea
            redoStack.push(command);
        }
    }

    /**
     * Rehace el último comando deshecho moviéndolo de la pila redo a la pila undo.
     */
    public void redo() {
        // Verificar que haya comandos disponibles para rehacer
        if (!redoStack.isEmpty()) {
            // Obtener el último comando deshecho
            EditorCommand command = redoStack.pop();

            // Volver a ejecutar la acción del comando
            command.execute();

            // Mover el comando de vuelta a la pila de deshacer
            undoStack.push(command);
        }
    }

    /**
     * Retorna el historial completo de versiones del código del proyecto.
     */
    public List<String> getVersionHistory() {
        // Retornar la lista completa de versiones registradas
        return versionHistory;
    }
}
