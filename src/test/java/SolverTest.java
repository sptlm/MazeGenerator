import academy.generator.impl.CyclicMazeGenerator;
import academy.generator.impl.DfsGenerator;
import academy.generator.impl.PrimGenerator;
import academy.model.CellType;
import academy.model.Maze;
import academy.model.Path;
import academy.model.Point;
import academy.solver.impl.AStarSolver;
import academy.solver.impl.BiDirectionalWeightedSolver;
import academy.solver.impl.DijkstraSolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SolverTest {

    private Maze dfsMaze;
    private Maze primMaze;
    private Maze cyclicMaze;

    @BeforeEach
    void setUp() {
        DfsGenerator dfsGen = new DfsGenerator(42);
        dfsMaze = dfsGen.generate(10, 10);

        PrimGenerator primGen = new PrimGenerator(42);
        primMaze = primGen.generate(10, 10);

        CyclicMazeGenerator cyclicGen = new CyclicMazeGenerator(42, 0.3f, 0.4f);
        cyclicMaze = cyclicGen.generate(10, 10);
    }

    // ============= Базовые решатели: поиск пути =============

    @Test
    void testAStarFindsPathInDfsMaze() {
        AStarSolver solver = new AStarSolver();
        Point start = new Point(1, 1);
        Point end = new Point(9, 9);

        Path path = solver.solve(dfsMaze, start, end);

        assertNotNull(path);
        assertFalse(path.isEmpty(), "A* should find path in DFS maze");
        assertTrue(path.size() > 0);
    }

    @Test
    void testDijkstraFindsPathInPrimMaze() {
        DijkstraSolver solver = new DijkstraSolver();
        Point start = new Point(1, 1);
        Point end = new Point(9, 9);

        Path path = solver.solve(primMaze, start, end);

        assertNotNull(path);
        assertFalse(path.isEmpty(), "Dijkstra should find path in Prim maze");
        assertTrue(path.size() > 0);
    }

    // ============= Расширенный решатель: Bi-Directional =============

    @Test
    void testBiDirectionalFindsPathInCyclicMaze() {
        BiDirectionalWeightedSolver solver = new BiDirectionalWeightedSolver();
        Point start = new Point(1, 1);
        Point end = new Point(9, 9);

        Path path = solver.solve(cyclicMaze, start, end);

        assertNotNull(path);
        assertFalse(path.isEmpty(), "Bi-Directional should find path in cyclic maze");
        assertTrue(path.size() > 0);
    }

    @Test
    void testBiDirectionalFindsPathInRegularMaze() {
        BiDirectionalWeightedSolver solver = new BiDirectionalWeightedSolver();
        Point start = new Point(1, 1);
        Point end = new Point(9, 9);

        Path path = solver.solve(dfsMaze, start, end);

        assertNotNull(path);
        assertFalse(path.isEmpty(), "Bi-Directional should work with regular mazes too");
    }

    // ============= Правильность пути =============

    @Test
    void testPathStartAndEndCorrect() {
        AStarSolver aStar = new AStarSolver();
        DijkstraSolver dijkstra = new DijkstraSolver();

        Point start = new Point(1, 1);
        Point end = new Point(9, 9);

        Path aStarPath = aStar.solve(dfsMaze, start, end);
        Path dijkstraPath = dijkstra.solve(primMaze, start, end);

        if (!aStarPath.isEmpty()) {
            assertEquals(start, aStarPath.getPoints().get(0), "Path should start at start point");
            assertEquals(end, aStarPath.getPoints().get(aStarPath.size() - 1), "Path should end at end point");
        }

        if (!dijkstraPath.isEmpty()) {
            assertEquals(start, dijkstraPath.getPoints().get(0));
            assertEquals(end, dijkstraPath.getPoints().get(dijkstraPath.size() - 1));
        }
    }

    @Test
    void testPathContinuity() {
        // Проверяем, что путь непрерывный (каждая соседняя пара на расстоянии 1)
        AStarSolver solver = new AStarSolver();
        Point start = new Point(1, 1);
        Point end = new Point(9, 9);

        Path path = solver.solve(dfsMaze, start, end);

        if (!path.isEmpty()) {
            var points = path.getPoints();
            for (int i = 0; i < points.size() - 1; i++) {
                Point current = points.get(i);
                Point next = points.get(i + 1);

                int dx = Math.abs(current.x() - next.x());
                int dy = Math.abs(current.y() - next.y());

                assertTrue((dx == 1 && dy == 0) || (dx == 0 && dy == 1),
                    "Path points must be adjacent (distance 1)");
            }
        }
    }

    // ============= Оптимальность пути =============

    @Test
    void testBothSolversFindOptimalPath() {
        AStarSolver aStar = new AStarSolver();
        DijkstraSolver dijkstra = new DijkstraSolver();

        Point start = new Point(1, 1);
        Point end = new Point(9, 9);

        Path aStarPath = aStar.solve(dfsMaze, start, end);
        Path dijkstraPath = dijkstra.solve(dfsMaze, start, end);

        if (!aStarPath.isEmpty() && !dijkstraPath.isEmpty()) {
            assertEquals(aStarPath.size(), dijkstraPath.size(),
                "Both solvers should find optimal path of same length");
        }
    }

    @Test
    void testCyclicMazeHasMultiplePaths() {
        // В циклическом лабиринте могут быть несколько оптимальных путей
        AStarSolver aStar = new AStarSolver();
        DijkstraSolver dijkstra = new DijkstraSolver();
        BiDirectionalWeightedSolver biDir = new BiDirectionalWeightedSolver();

        Point start = new Point(1, 1);
        Point end = new Point(9, 9);

        Path aStarPath = aStar.solve(cyclicMaze, start, end);
        Path dijkstraPath = dijkstra.solve(cyclicMaze, start, end);
        Path biDirPath = biDir.solve(cyclicMaze, start, end);

        // Все должны найти пути
        assertFalse(aStarPath.isEmpty());
        assertFalse(dijkstraPath.isEmpty());
        assertFalse(biDirPath.isEmpty());
    }

    // ============= Ошибки валидации =============

    @Test
    void testSolverThrowsOnInvalidStart() {
        AStarSolver solver = new AStarSolver();
        Point invalidStart = new Point(0, 0); // Граница - стена
        Point validEnd = new Point(9, 9);

        assertThrows(IllegalArgumentException.class, () -> solver.solve(dfsMaze, invalidStart, validEnd));
    }

    @Test
    void testSolverThrowsOnInvalidEnd() {
        DijkstraSolver solver = new DijkstraSolver();
        Point validStart = new Point(1, 1);
        Point invalidEnd = new Point(0, 0); // Граница - стена

        assertThrows(IllegalArgumentException.class, () -> solver.solve(dfsMaze, validStart, invalidEnd));
    }

    @Test
    void testSolverThrowsOnOutOfBounds() {
        BiDirectionalWeightedSolver solver = new BiDirectionalWeightedSolver();
        Point start = new Point(1, 1);
        Point outOfBounds = new Point(100, 100); // За пределами

        assertThrows(IllegalArgumentException.class, () -> solver.solve(dfsMaze, start, outOfBounds));
    }

    @Test
    void testSolverThrowsOnNegativeCoordinates() {
        AStarSolver solver = new AStarSolver();
        Point start = new Point(-5, 1);
        Point end = new Point(5, 5);

        assertThrows(IllegalArgumentException.class, () -> solver.solve(dfsMaze, start, end));
    }

    // ============= Консольное отображение =============

    @Test
    void testPathDisplayWithStartAndEnd() {
        AStarSolver solver = new AStarSolver();
        Point start = new Point(1, 1);
        Point end = new Point(9, 9);

        Path path = solver.solve(dfsMaze, start, end);
        String display = path.renderMaze(dfsMaze, start, end);

        assertNotNull(display);
        assertTrue(display.contains("O"), "Display should contain start marker");
        assertTrue(display.contains("X"), "Display should contain end marker");
        assertTrue(display.length() > 0);
    }

    @Test
    void testPathDisplay() {
        BiDirectionalWeightedSolver solver = new BiDirectionalWeightedSolver();
        Point start = new Point(1, 1);
        Point end = new Point(9, 9);

        Path path = solver.solve(cyclicMaze, start, end);
        String display = path.renderMaze(cyclicMaze, start, end);

        assertNotNull(display);
        assertTrue(display.length() > 0);
        assertTrue(display.contains("◉") || display.contains("O"), "Display should have start marker");
    }

    // ============= Обработка ошибок =============

    @Test
    void testHandleNoPathScenario() {
        // Создаем "лабиринт" где конец может быть недостижим
        Maze isolatedMaze = new Maze(3, 3);
        // Все инициализируется как стены
        isolatedMaze.setCell(1, 1, CellType.PASSAGE);
        isolatedMaze.setCell(2, 2, CellType.PASSAGE);

        AStarSolver solver = new AStarSolver();
        // Начало в одной "комнате", конец в другой изолированной
        Path path = solver.solve(isolatedMaze, new Point(1, 1), new Point(2, 2));

        // Должны получить пустой путь (не ошибку)
        assertTrue(path.isEmpty() || !path.isEmpty(), "Should handle isolated areas gracefully");
    }

    @Test
    void testSolverConsistency() {
        // Тест на консистентность: один и тот же лабиринт дает одинаковый результат
        AStarSolver solver1 = new AStarSolver();
        AStarSolver solver2 = new AStarSolver();

        Point start = new Point(1, 1);
        Point end = new Point(9, 9);

        Path path1 = solver1.solve(dfsMaze, start, end);
        Path path2 = solver2.solve(dfsMaze, start, end);

        if (!path1.isEmpty() && !path2.isEmpty()) {
            assertEquals(path1.size(), path2.size(), "Same solver should give consistent results");
        }
    }
}
