package academy.solver.impl;

import academy.model.Maze;
import academy.model.Path;
import academy.model.Point;
import academy.solver.BaseSolver;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 * Решатель лабиринтов алгоритмом Дейкстры.
 * Находит кратчайший путь от начальной до конечной точки.
 *
 * Принцип работы:
 * 1. Инициализируем расстояния до всех вершин как бесконечность, кроме стартовой (0)
 * 2. Используем приоритетную очередь для выбора вершины с минимальным расстоянием
 * 3. Для каждой вершины обновляем расстояния до ее соседей
 * 4. Повторяем, пока не достигнем целевой вершины
 * 5. Восстанавливаем путь от конца к началу
 */
public class DijkstraSolver extends BaseSolver {

    @Override
    public Path solve(Maze maze, Point start, Point end) {
        validatePoints(maze, start, end);

        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(n -> n.distance));

        Set<Point> visited = new HashSet<>();

        Map<Point, Integer> distances = new HashMap<>();

        // Карта для восстановления пути
        Map<Point, Point> previous = new HashMap<>();

        // Инициализация
        distances.put(start, 0);
        queue.add(new Node(start, 0));

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            Point currentPoint = current.point;

            if (currentPoint.equals(end)) {
                return reconstructPath(previous, end);
            }

            if (visited.contains(currentPoint)) {
                continue;
            }

            visited.add(currentPoint);

            for (Point neighbor : getPassableNeighbors(maze, currentPoint)) {
                if (visited.contains(neighbor)) {
                    continue;
                }

                int newDistance = distances.get(currentPoint) + 1;
                if (newDistance < distances.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    distances.put(neighbor, newDistance);
                    previous.put(neighbor, currentPoint);
                    queue.add(new Node(neighbor, newDistance));
                }
            }
        }

        // Путь не найден
        return emptyPath();
    }

    private static class Node {
        final Point point;
        final int distance;

        Node(Point point, int distance) {
            this.point = point;
            this.distance = distance;
        }
    }
}
