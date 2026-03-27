package com.codeTutor.backend.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.codeTutor.backend.model.LearningTopic;
import com.codeTutor.backend.repository.LearningTopicRepository;

/**
 * Carga los temas de aprendizaje iniciales en la base de datos al arrancar la aplicación.
 * Solo inserta si la tabla está vacía, evitando duplicados en cada redeploy.
 */
@Component
public class DataSeeder implements CommandLineRunner {

    @Autowired
    private LearningTopicRepository topicRepository;

    @Override
    public void run(String... args) {
        if (topicRepository.count() > 0) return;

        topicRepository.saveAll(List.of(

            // ── ESTRUCTURAS DE DATOS ──────────────────────────────────
            LearningTopic.builder()
                .name("Stack")
                .category("DATA_STRUCTURE")
                .description("Estructura LIFO. Aprende a implementar y usar pilas para resolver problemas como validación de paréntesis, historial de navegación y más.")
                .difficulty("BEGINNER")
                .build(),

            LearningTopic.builder()
                .name("Queue")
                .category("DATA_STRUCTURE")
                .description("Estructura FIFO. Ideal para colas de tareas, BFS en grafos y sistemas de mensajería.")
                .difficulty("BEGINNER")
                .build(),

            LearningTopic.builder()
                .name("LinkedList")
                .category("DATA_STRUCTURE")
                .description("Lista enlazada simple y doble. Entiende punteros, inserción y eliminación eficiente.")
                .difficulty("BEGINNER")
                .build(),

            LearningTopic.builder()
                .name("HashMap")
                .category("DATA_STRUCTURE")
                .description("Tabla hash para búsquedas O(1). Aprende a resolver problemas de frecuencia, agrupación y caché.")
                .difficulty("BEGINNER")
                .build(),

            LearningTopic.builder()
                .name("Binary Tree")
                .category("DATA_STRUCTURE")
                .description("Árbol binario y BST. Recorridos inorder, preorder, postorder y búsqueda eficiente.")
                .difficulty("INTERMEDIATE")
                .build(),

            LearningTopic.builder()
                .name("Graph")
                .category("DATA_STRUCTURE")
                .description("Grafos dirigidos y no dirigidos. BFS, DFS y detección de ciclos.")
                .difficulty("INTERMEDIATE")
                .build(),

            LearningTopic.builder()
                .name("Heap")
                .category("DATA_STRUCTURE")
                .description("Min-heap y max-heap. Implementación de colas de prioridad y algoritmos de ordenamiento.")
                .difficulty("INTERMEDIATE")
                .build(),

            LearningTopic.builder()
                .name("AVL Tree")
                .category("DATA_STRUCTURE")
                .description("Árbol binario de búsqueda auto-balanceado. Garantiza O(log n) en inserción, búsqueda y eliminación.")
                .difficulty("ADVANCED")
                .build(),

            // ── PATRONES DE DISEÑO ────────────────────────────────────
            LearningTopic.builder()
                .name("Singleton")
                .category("DESIGN_PATTERN")
                .description("Garantiza una única instancia de una clase. Aprende cuándo usarlo y sus variantes thread-safe.")
                .difficulty("BEGINNER")
                .build(),

            LearningTopic.builder()
                .name("Factory Method")
                .category("DESIGN_PATTERN")
                .description("Crea objetos sin especificar la clase exacta. Ideal para sistemas extensibles.")
                .difficulty("BEGINNER")
                .build(),

            LearningTopic.builder()
                .name("Observer")
                .category("DESIGN_PATTERN")
                .description("Notifica cambios a múltiples objetos. Base de sistemas de eventos y reactive programming.")
                .difficulty("INTERMEDIATE")
                .build(),

            LearningTopic.builder()
                .name("Strategy")
                .category("DESIGN_PATTERN")
                .description("Encapsula algoritmos intercambiables. Elimina condicionales y aplica Open/Closed principle.")
                .difficulty("INTERMEDIATE")
                .build(),

            LearningTopic.builder()
                .name("Decorator")
                .category("DESIGN_PATTERN")
                .description("Agrega comportamiento a objetos dinámicamente sin modificar su clase.")
                .difficulty("INTERMEDIATE")
                .build(),

            LearningTopic.builder()
                .name("Facade")
                .category("DESIGN_PATTERN")
                .description("Simplifica interfaces complejas. Reduce el acoplamiento entre capas de una aplicación.")
                .difficulty("BEGINNER")
                .build(),

            LearningTopic.builder()
                .name("Command")
                .category("DESIGN_PATTERN")
                .description("Encapsula acciones como objetos. Permite undo/redo, colas de comandos y logging.")
                .difficulty("INTERMEDIATE")
                .build(),

            LearningTopic.builder()
                .name("Builder")
                .category("DESIGN_PATTERN")
                .description("Construye objetos complejos paso a paso. Evita constructores con demasiados parámetros.")
                .difficulty("BEGINNER")
                .build(),

            // ── ALGORITMOS ────────────────────────────────────────────
            LearningTopic.builder()
                .name("Binary Search")
                .category("ALGORITHM")
                .description("Búsqueda eficiente O(log n) en arreglos ordenados. Fundamento de muchos algoritmos avanzados.")
                .difficulty("BEGINNER")
                .build(),

            LearningTopic.builder()
                .name("Bubble Sort")
                .category("ALGORITHM")
                .description("Algoritmo de ordenamiento básico. Entiende comparaciones e intercambios.")
                .difficulty("BEGINNER")
                .build(),

            LearningTopic.builder()
                .name("Merge Sort")
                .category("ALGORITHM")
                .description("Ordenamiento divide y vencerás O(n log n). Aprende recursión y merge de arreglos.")
                .difficulty("INTERMEDIATE")
                .build(),

            LearningTopic.builder()
                .name("Recursion")
                .category("ALGORITHM")
                .description("Funciones que se llaman a sí mismas. Fibonacci, factorial, torres de Hanoi y más.")
                .difficulty("BEGINNER")
                .build(),

            LearningTopic.builder()
                .name("Dynamic Programming")
                .category("ALGORITHM")
                .description("Optimización con memoización. Resuelve problemas de mochila, Fibonacci optimizado y LCS.")
                .difficulty("ADVANCED")
                .build(),

            LearningTopic.builder()
                .name("BFS & DFS")
                .category("ALGORITHM")
                .description("Recorrido en anchura y profundidad. Esenciales para grafos, árboles y búsqueda de caminos.")
                .difficulty("INTERMEDIATE")
                .build()
        ));

        System.out.println("[DataSeeder] Temas de aprendizaje cargados correctamente.");
    }
}
