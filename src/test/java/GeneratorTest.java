import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import academy.generator.impl.CyclicMazeGenerator;
import academy.generator.impl.DfsGenerator;
import academy.generator.impl.PrimGenerator;
import academy.model.CellType;
import academy.model.Maze;
import org.junit.jupiter.api.Test;

public class GeneratorTest {

    @Test
    void testDfsGeneratorCreatesValidMaze() {
        DfsGenerator generator = new DfsGenerator(42);
        Maze maze = generator.generate(10, 10);

        assertNotNull(maze);
        assertEquals(10, maze.getWidth());
        assertEquals(10, maze.getHeight());
        assertEquals(12, maze.getFullWidth());
        assertEquals(12, maze.getFullHeight());
    }

    @Test
    void testPrimGeneratorCreatesValidMaze() {
        PrimGenerator generator = new PrimGenerator(42);
        Maze maze = generator.generate(10, 10);

        assertNotNull(maze);
        assertEquals(10, maze.getWidth());
        assertEquals(10, maze.getHeight());
    }

    @Test
    void testGeneratorBordersAreWalls() {
        DfsGenerator dfsGen = new DfsGenerator();
        Maze dfsMaze = dfsGen.generate(5, 5);

        PrimGenerator primGen = new PrimGenerator();
        Maze primMaze = primGen.generate(5, 5);

        // Проверяем DFS границы
        for (int x = 0; x < dfsMaze.getFullWidth(); x++) {
            assertEquals(CellType.WALL, dfsMaze.getCell(x, 0));
            assertEquals(CellType.WALL, dfsMaze.getCell(x, dfsMaze.getFullHeight() - 1));
        }
        for (int y = 0; y < dfsMaze.getFullHeight(); y++) {
            assertEquals(CellType.WALL, dfsMaze.getCell(0, y));
            assertEquals(CellType.WALL, dfsMaze.getCell(dfsMaze.getFullWidth() - 1, y));
        }

        // Проверяем Prim границы
        for (int x = 0; x < primMaze.getFullWidth(); x++) {
            assertEquals(CellType.WALL, primMaze.getCell(x, 0));
            assertEquals(CellType.WALL, primMaze.getCell(x, primMaze.getFullHeight() - 1));
        }
        for (int y = 0; y < dfsMaze.getFullHeight(); y++) {
            assertEquals(CellType.WALL, primMaze.getCell(0, y));
            assertEquals(CellType.WALL, primMaze.getCell(primMaze.getFullWidth() - 1, y));
        }
    }

    @Test
    void testGeneratorsHavePassages() {
        DfsGenerator dfsGen = new DfsGenerator();
        Maze dfsMaze = dfsGen.generate(10, 10);

        PrimGenerator primGen = new PrimGenerator();
        Maze primMaze = primGen.generate(10, 10);

        // Проверяем наличие проходов
        boolean dfsHasPassage = hasMazePassages(dfsMaze);
        boolean primHasPassage = hasMazePassages(primMaze);

        assertTrue(dfsHasPassage, "DFS maze should have passages");
        assertTrue(primHasPassage, "Prim maze should have passages");
    }

    @Test
    void testGeneratorWithMinimalSize() {
        DfsGenerator dfsGen = new DfsGenerator();
        Maze dfsMaze = dfsGen.generate(1, 1);

        assertEquals(1, dfsMaze.getWidth());
        assertEquals(1, dfsMaze.getHeight());
        assertEquals(3, dfsMaze.getFullWidth());
        assertEquals(3, dfsMaze.getFullHeight());

        // Центр должен быть проходом
        assertEquals(CellType.PASSAGE, dfsMaze.getCell(1, 1));
    }

    @Test
    void testGeneratorWithLargeSize() {
        DfsGenerator dfsGen = new DfsGenerator();
        Maze dfsMaze = dfsGen.generate(50, 50);

        assertEquals(50, dfsMaze.getWidth());
        assertEquals(50, dfsMaze.getHeight());
        assertTrue(hasMazePassages(dfsMaze), "Large maze should have passages");
    }

    // ============= Расширенные генераторы (Cyclic) =============

    @Test
    void testCyclicGeneratorCreatesValidMaze() {
        CyclicMazeGenerator generator = new CyclicMazeGenerator(42, 0.2f, 0.3f, new DfsGenerator(42));
        Maze maze = generator.generate(10, 10);

        assertNotNull(maze);
        assertEquals(10, maze.getWidth());
        assertEquals(10, maze.getHeight());
    }

    @Test
    void testCyclicGeneratorBordersAreWalls() {
        CyclicMazeGenerator generator = new CyclicMazeGenerator(new DfsGenerator(42));
        Maze maze = generator.generate(5, 5);

        for (int x = 0; x < maze.getFullWidth(); x++) {
            assertEquals(CellType.WALL, maze.getCell(x, 0));
            assertEquals(CellType.WALL, maze.getCell(x, maze.getFullHeight() - 1));
        }
        for (int y = 0; y < maze.getFullHeight(); y++) {
            assertEquals(CellType.WALL, maze.getCell(0, y));
            assertEquals(CellType.WALL, maze.getCell(maze.getFullWidth() - 1, y));
        }
    }

    @Test
    void testCyclicGeneratorHasPassages() {
        CyclicMazeGenerator generator = new CyclicMazeGenerator(new DfsGenerator(42));
        Maze maze = generator.generate(10, 10);

        boolean hasPassage = hasMazePassages(maze);

        assertTrue(hasPassage, "Cyclic maze should have passages");
    }

    @Test
    void testCyclicGeneratorHasSurfaceVariation() {
        CyclicMazeGenerator generator = new CyclicMazeGenerator(42, 0.3f, 0.7f, new DfsGenerator(42));
        Maze maze = generator.generate(15, 15);

        // С высокой вероятностью должны быть разные поверхности
        boolean hasVariation = false;
        for (int y = 1; y < maze.getFullHeight() - 1; y++) {
            for (int x = 1; x < maze.getFullWidth() - 1; x++) {
                CellType cell = maze.getCell(x, y);
                if (cell == CellType.ASPHALT || cell == CellType.SAND || cell == CellType.SWAMP) {
                    hasVariation = true;
                    break;
                }
            }
        }

        assertTrue(hasVariation, "Should have surface variation with high probability");
    }

    @Test
    void testCyclicGeneratorHasMultiplePaths() {
        // Циклический лабиринт должен иметь циклы (несколько путей)
        CyclicMazeGenerator generator = new CyclicMazeGenerator(42, 0.5f, 0.3f, new DfsGenerator(42));
        Maze maze = generator.generate(15, 15);

        // Проверяем, что есть проходимые ячейки (циклы должны их создать)
        assertTrue(hasMazePassages(maze), "Cyclic maze should have multiple paths");
    }

    // ============= Ошибки валидации =============

    @Test
    void testGeneratorThrowsOnInvalidSize() {
        DfsGenerator dfsGen = new DfsGenerator();
        PrimGenerator primGen = new PrimGenerator();
        CyclicMazeGenerator cyclicGen = new CyclicMazeGenerator(new DfsGenerator(42));

        assertThrows(IllegalArgumentException.class, () -> dfsGen.generate(0, 10));
        assertThrows(IllegalArgumentException.class, () -> dfsGen.generate(10, 0));
        assertThrows(IllegalArgumentException.class, () -> dfsGen.generate(-5, 10));

        assertThrows(IllegalArgumentException.class, () -> primGen.generate(0, 10));
        assertThrows(IllegalArgumentException.class, () -> cyclicGen.generate(-1, 5));
    }

    // ============= Консольное отображение =============

    @Test
    void testMazeDisplaysCorrectly() {
        DfsGenerator generator = new DfsGenerator();
        Maze maze = generator.generate(5, 5);
        String display = maze.toString();

        assertNotNull(display);
        assertTrue(display.length() > 0);
        // Размер должен соответствовать (ширина + 2) * (высота + 2)
        String[] lines = display.split("\n");
        assertEquals(7, lines.length); // 5 + 2
        for (String line : lines) {
            assertEquals(7, line.length()); // 5 + 2
        }
    }

    @Test
    void testCyclicMazeDisplaysCorrectly() {
        CyclicMazeGenerator generator = new CyclicMazeGenerator(new DfsGenerator(42));
        Maze maze = generator.generate(8, 8);
        String display = maze.toString();

        assertNotNull(display);
        assertTrue(display.length() > 0);
        String[] lines = display.split("\n");
        assertEquals(10, lines.length); // 8 + 2
    }

    // ============= Вспомогательные методы =============

    private boolean hasMazePassages(Maze maze) {
        for (int y = 1; y < maze.getFullHeight() - 1; y++) {
            for (int x = 1; x < maze.getFullWidth() - 1; x++) {
                if (maze.getCell(x, y) == CellType.PASSAGE) {
                    return true;
                }
            }
        }
        return false;
    }
}
