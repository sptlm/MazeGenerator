package academy.solver.impl;

import static academy.util.Validator.validatePoints;

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
 * Решатель лабиринтов алгоритмом A* (A-star). Использует эвристику манхеттенского расстояния для ускорения поиска.
 *
 * <p>Принцип работы: 1. Оценка стоимости f(n) = g(n) + h(n), где: - g(n) - фактическая стоимость пути от старта до n -
 * h(n) - эвристическая оценка стоимости от n до цели (манхеттенское расстояние) 2. Используем приоритетную очередь для
 * выбора узла с минимальной f(n) 3. Гарантирует нахождение оптимального пути при допустимой эвристике
 */
public class AStarSolver extends BaseSolver {

    @Override
    public Path solve(Maze maze, Point start, Point end) {
        validatePoints(maze, start, end);

        // Приоритетная очередь для узлов (сортировка по f-значению)
        PriorityQueue<Node> queue = new PriorityQueue<>(Comparator.comparingInt(n -> n.f));

        // Множество посещенных узлов
        Set<Point> visited = new HashSet<>();

        // Карта для хранения g-значений (стоимость пути от старта)
        Map<Point, Integer> gScore = new HashMap<>();

        // Карта для восстановления пути
        Map<Point, Point> previous = new HashMap<>();

        gScore.put(start, 0);
        int h = heuristic(start, end);
        queue.add(new Node(start, 0, h));

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

                int newG = gScore.get(currentPoint) + 1;
                if (newG < gScore.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    previous.put(neighbor, currentPoint);
                    gScore.put(neighbor, newG);
                    int f = newG + heuristic(neighbor, end);
                    queue.add(new Node(neighbor, newG, f));
                }
            }
        }

        return emptyPath();
    }

    private static class Node {
        final Point point;
        final int g; // Стоимость пути от старта
        final int f; // Общая оценка: f = g + h

        Node(Point point, int g, int f) {
            this.point = point;
            this.g = g;
            this.f = f;
        }
    }
}
