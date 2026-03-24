package com.codeTutor.backend.service;

import com.codeTutor.backend.model.CodeSnapshot;
import com.codeTutor.backend.model.Project;
import com.codeTutor.backend.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * Servicio de gestión de proyectos del estudiante.
 * Maneja el guardado, actualización y recuperación de proyectos,
 * así como el historial de versiones y las operaciones de deshacer/rehacer.
 *
 * Implementa el patrón Command para encapsular operaciones del editor
 * como objetos ejecutables y reversibles.
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
     */
    private LinkedList<String> versionHistory = new LinkedList<>();

    /**
     * Pila de comandos ejecutados, usada para la funcionalidad de deshacer (undo).
     * Se usa Stack por su principio LIFO: siempre se deshace la acción más reciente primero.
     */
    private Stack<EditorCommand> undoStack = new Stack<>();

    /**
     * Pila de comandos deshechos, usada para la funcionalidad de rehacer (redo).
     * También Stack LIFO: al rehacer se recupera el último comando deshecho.
     */
    private Stack<EditorCommand> redoStack = new Stack<>();

    // =========================================================
    // PATRÓN COMMAND: Interfaz y clases concretas
    // =========================================================

    /**
     * Interfaz del patrón Command que define las operaciones básicas de cualquier comando del editor.
     */
    public interface EditorCommand {
        /** Ejecuta la acción del comando. */
        void execute();
        /** Revierte la acción del comando, restaurando el estado anterior. */
        void undo();
    }

    /**
     * Comando concreto que representa la acción de escribir texto en el editor.
     */
    public static class WriteCommand implements EditorCommand {

        // Texto que se va a escribir en el editor
        private final String textToWrite;
        // Estado del código antes de ejecutar el comando
        private final String previousState;
        // Referencia al contenedor del estado actual del código
        private final StringBuilder currentCode;

        public WriteCommand(String textToWrite, String previousState, StringBuilder currentCode) {
            this.textToWrite = textToWrite;
            this.previousState = previousState;
            this.currentCode = currentCode;
        }

        /** Ejecuta el comando: agrega el texto al código actual. */
        @Override
        public void execute() {
            currentCode.append(textToWrite);
        }

        /** Deshace el comando: restaura el código al estado anterior. */
        @Override
        public void undo() {
            currentCode.setLength(0);
            currentCode.append(previousState);
        }
    }

    /**
     * Comando concreto que representa la acción de eliminar texto del editor.
     */
    public static class DeleteCommand implements EditorCommand {

        // Texto que fue eliminado (necesario para restaurarlo al deshacer)
        private final String deletedText;
        // Posición en el código donde se realizó la eliminación
        private final int position;
        // Referencia al contenedor del estado actual del código
        private final StringBuilder currentCode;

        public DeleteCommand(String deletedText, int position, StringBuilder currentCode) {
            this.deletedText = deletedText;
            this.position = position;
            this.currentCode = currentCode;
        }

        /** Ejecuta el comando: elimina el texto en la posición indicada. */
        @Override
        public void execute() {
            int endPosition = position + deletedText.length();
            if (position >= 0 && endPosition <= currentCode.length()) {
                currentCode.delete(position, endPosition);
            }
        }

        /** Deshace el comando: reinserta el texto eliminado en su posición original. */
        @Override
        public void undo() {
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
     */
    public Project saveProject(Project project) {
        // Validar que el nombre del proyecto no sea nulo ni vacío
        if (project.getName() == null || project.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del proyecto no puede estar vacío.");
        }

        // Persistir el proyecto en la base de datos y retornar el objeto guardado
        return projectRepository.save(project);
    }

    /**
     * Recupera un proyecto de la base de datos por su identificador único.
     * Lanza una excepción en español si el proyecto no existe.
     */
    public Project getProject(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con el ID: " + id));
    }

    /**
     * Actualiza el código de un proyecto registrando la versión anterior en el historial.
     * El código vive en CodeSnapshot, por lo que se guarda el contenido del último snapshot.
     */
    public Project updateProjectCode(Long id, String newCode) {
        // Obtener el proyecto existente
        Project project = getProject(id);

        // Guardar el contenido del último snapshot en el historial antes de actualizar
        if (project.getCodeSnapshots() != null && !project.getCodeSnapshots().isEmpty()) {
            String lastCode = project.getCodeSnapshots()
                    .get(project.getCodeSnapshots().size() - 1)
                    .getContent();
            versionHistory.add(lastCode);
        }

        // Crear un nuevo snapshot con el código actualizado
        CodeSnapshot newSnapshot = CodeSnapshot.builder()
                .content(newCode)
                .project(project)
                .versionNumber(
                    project.getCodeSnapshots() != null
                        ? project.getCodeSnapshots().size() + 1
                        : 1
                )
                .versionLabel("actualización")
                .build();

        // Agregar el nuevo snapshot a la lista del proyecto
        if (project.getCodeSnapshots() != null) {
            project.getCodeSnapshots().add(newSnapshot);
        }

        // Persistir los cambios y retornar el proyecto actualizado
        return projectRepository.save(project);
    }

    /**
     * Ejecuta un comando del editor, lo agrega a la pila de deshacer
     * y limpia la pila de rehacer.
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
        if (!undoStack.isEmpty()) {
            EditorCommand command = undoStack.pop();
            command.undo();
            redoStack.push(command);
        }
    }

    /**
     * Rehace el último comando deshecho moviéndolo de la pila redo a la pila undo.
     */
    public void redo() {
        if (!redoStack.isEmpty()) {
            EditorCommand command = redoStack.pop();
            command.execute();
            undoStack.push(command);
        }
    }

    /**
     * Retorna el historial completo de versiones del código del proyecto.
     */
    public List<String> getVersionHistory() {
        return versionHistory;
    }
}
