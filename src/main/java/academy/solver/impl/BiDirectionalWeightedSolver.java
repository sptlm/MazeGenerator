package academy.solver.impl;

import academy.model.Maze;
import academy.model.Path;
import academy.model.Point;
import academy.solver.BaseSolver;
import academy.util.Validator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Решатель лабиринтов с двусторонним поиском (Bi-directional Search). Одновременно ищет путь от начала и от конца,
 * встречаясь в середине. Поддерживает взвешенные графы (различные типы поверхностей).
 *
 * <p>Принцип работы: 1. Запускаем поиск из стартовой точки (forward search) 2. Одновременно запускаем поиск из конечной
 * точки (backward search) 3. Когда поиски встречаются (посещаем общую точку), объединяем пути 4. Восстанавливаем полный
 * путь от начала к концу
 */
public class BiDirectionalWeightedSolver extends BaseSolver {
    @Override
    public Path solve(Maze maze, Point start, Point end) {
        Validator.validatePoints(maze, start, end);
        // Приоритетные очереди для двусторонних поисков
        PriorityQueue<Node> forwardQueue = new PriorityQueue<>(Comparator.comparingInt(n -> n.cost));
        PriorityQueue<Node> backwardQueue = new PriorityQueue<>(Comparator.comparingInt(n -> n.cost));

        // Карты посещенных узлов и их стоимостей
        Map<Point, Integer> forwardCosts = new HashMap<>();
        Map<Point, Integer> backwardCosts = new HashMap<>();

        // Карты для восстановления путей
        Map<Point, Point> forwardPrevious = new HashMap<>();
        Map<Point, Point> backwardPrevious = new HashMap<>();

        // Инициализация
        forwardCosts.put(start, 0);
        backwardCosts.put(end, 0);
        forwardQueue.add(new Node(start, 0));
        backwardQueue.add(new Node(end, 0));

        while (!forwardQueue.isEmpty() || !backwardQueue.isEmpty()) {
            if (!forwardQueue.isEmpty()) {
                Point meeting = expandForward(maze, forwardQueue, forwardCosts, forwardPrevious, backwardCosts);
                if (meeting != null) {
                    return constructPath(forwardPrevious, backwardPrevious, meeting);
                }
            }

            if (!backwardQueue.isEmpty()) {
                Point meeting = expandBackward(maze, backwardQueue, backwardCosts, backwardPrevious, forwardCosts);
                if (meeting != null) {
                    return constructPath(forwardPrevious, backwardPrevious, meeting);
                }
            }
        }

        return emptyPath();
    }

    private Point expandForward(
            Maze maze,
            PriorityQueue<Node> queue,
            Map<Point, Integer> forwardCosts,
            Map<Point, Point> forwardPrev,
            Map<Point, Integer> backwardCosts) {
        if (queue.isEmpty()) return null;

        Node current = queue.poll();
        Point currentPoint = current.point;

        // Проверяем, встретились ли поиски
        if (backwardCosts.containsKey(currentPoint)) {
            return currentPoint;
        }

        // Расширяем соседей
        for (Point neighbor : getPassableNeighbors(maze, currentPoint)) {
            int weight = maze.getWeight(neighbor.x(), neighbor.y());
            int newCost = forwardCosts.get(currentPoint) + weight;

            if (newCost < forwardCosts.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                forwardCosts.put(neighbor, newCost);
                forwardPrev.put(neighbor, currentPoint);
                queue.add(new Node(neighbor, newCost));
            }
        }

        return null;
    }

    private Point expandBackward(
            Maze maze,
            PriorityQueue<Node> queue,
            Map<Point, Integer> backwardCosts,
            Map<Point, Point> backwardPrev,
            Map<Point, Integer> forwardCosts) {
        if (queue.isEmpty()) return null;

        Node current = queue.poll();
        Point currentPoint = current.point;

        // Проверяем, встретились ли поиски
        if (forwardCosts.containsKey(currentPoint)) {
            return currentPoint;
        }

        // Расширяем соседей
        for (Point neighbor : getPassableNeighbors(maze, currentPoint)) {
            int weight = maze.getWeight(neighbor.x(), neighbor.y());
            int newCost = backwardCosts.get(currentPoint) + weight;

            if (newCost < backwardCosts.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                backwardCosts.put(neighbor, newCost);
                backwardPrev.put(neighbor, currentPoint);
                queue.add(new Node(neighbor, newCost));
            }
        }

        return null;
    }

    private Path constructPath(Map<Point, Point> forwardPrev, Map<Point, Point> backwardPrev, Point meeting) {
        List<Point> pathPoints = new ArrayList<>();

        // Путь от start к meeting
        Point current = meeting;
        while (current != null) {
            pathPoints.add(current);
            current = forwardPrev.get(current);
        }
        Collections.reverse(pathPoints);

        // Путь от meeting к end
        current = backwardPrev.get(meeting);
        List<Point> secondHalf = new ArrayList<>();
        while (current != null) {
            secondHalf.add(current);
            current = backwardPrev.get(current);
        }

        pathPoints.addAll(secondHalf);

        return new Path(pathPoints);
    }

    private static class Node {
        final Point point;
        final int cost;

        Node(Point point, int cost) {
            this.point = point;
            this.cost = cost;
        }
    }
}
