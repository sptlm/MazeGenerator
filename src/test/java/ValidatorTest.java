import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import academy.model.Point;
import academy.util.Validator;
import org.junit.jupiter.api.Test;

class ValidatorTest {

    // ============= Тесты размеров лабиринта =============

    @Test
    void testValidMazeSize() {
        assertDoesNotThrow(() -> Validator.validateMazeSize(1, 1));
        assertDoesNotThrow(() -> Validator.validateMazeSize(10, 10));
        assertDoesNotThrow(() -> Validator.validateMazeSize(100, 100));
        assertDoesNotThrow(() -> Validator.validateMazeSize(1, 100));
        assertDoesNotThrow(() -> Validator.validateMazeSize(100, 1));
    }

    @Test
    void testInvalidMazeSizeZero() {
        assertThrows(IllegalArgumentException.class, () -> Validator.validateMazeSize(0, 10));
        assertThrows(IllegalArgumentException.class, () -> Validator.validateMazeSize(10, 0));
        assertThrows(IllegalArgumentException.class, () -> Validator.validateMazeSize(0, 0));
    }

    @Test
    void testInvalidMazeSizeNegative() {
        assertThrows(IllegalArgumentException.class, () -> Validator.validateMazeSize(-1, 10));
        assertThrows(IllegalArgumentException.class, () -> Validator.validateMazeSize(10, -1));
        assertThrows(IllegalArgumentException.class, () -> Validator.validateMazeSize(-5, -5));
        assertThrows(IllegalArgumentException.class, () -> Validator.validateMazeSize(-100, 50));
    }

    // ============= Тесты координат =============

    @Test
    void testParseValidCoordinates() {
        Point p1 = Validator.parseCoordinates("0,0");
        assertEquals(0, p1.x());
        assertEquals(0, p1.y());

        Point p2 = Validator.parseCoordinates("5,10");
        assertEquals(5, p2.x());
        assertEquals(10, p2.y());

        Point p3 = Validator.parseCoordinates("100,200");
        assertEquals(100, p3.x());
        assertEquals(200, p3.y());
    }

    @Test
    void testParseCoordinatesWithSpaces() {
        Point p1 = Validator.parseCoordinates(" 5 , 10 ");
        assertEquals(5, p1.x());
        assertEquals(10, p1.y());

        Point p2 = Validator.parseCoordinates("  1  ,  2  ");
        assertEquals(1, p2.x());
        assertEquals(2, p2.y());
    }

    @Test
    void testParseInvalidCoordinatesEmpty() {
        assertThrows(IllegalArgumentException.class, () -> Validator.parseCoordinates(""));
        assertThrows(IllegalArgumentException.class, () -> Validator.parseCoordinates("   "));
    }

    @Test
    void testParseInvalidCoordinatesNull() {
        assertThrows(IllegalArgumentException.class, () -> Validator.parseCoordinates(null));
    }

    @Test
    void testParseInvalidCoordinatesFormat() {
        assertThrows(IllegalArgumentException.class, () -> Validator.parseCoordinates("5"));
        assertThrows(IllegalArgumentException.class, () -> Validator.parseCoordinates("5,"));
        assertThrows(IllegalArgumentException.class, () -> Validator.parseCoordinates(",10"));
        assertThrows(IllegalArgumentException.class, () -> Validator.parseCoordinates("5,10,15"));
        assertThrows(IllegalArgumentException.class, () -> Validator.parseCoordinates("5,10,15,20"));
    }

    @Test
    void testParseInvalidCoordinatesNonNumeric() {
        assertThrows(IllegalArgumentException.class, () -> Validator.parseCoordinates("a,b"));
        assertThrows(IllegalArgumentException.class, () -> Validator.parseCoordinates("5,abc"));
        assertThrows(IllegalArgumentException.class, () -> Validator.parseCoordinates("xyz,10"));
        assertThrows(IllegalArgumentException.class, () -> Validator.parseCoordinates("1.5,2.5"));
    }

    @Test
    void testParseNegativeCoordinates() {
        assertThrows(IllegalArgumentException.class, () -> Validator.parseCoordinates("-1,5"));
        assertThrows(IllegalArgumentException.class, () -> Validator.parseCoordinates("5,-1"));
        assertThrows(IllegalArgumentException.class, () -> Validator.parseCoordinates("-5,-10"));
        assertThrows(IllegalArgumentException.class, () -> Validator.parseCoordinates("-1,-1"));
    }

    @Test
    void testParseLargeCoordinates() {
        // Очень большие числа должны парситься корректно
        Point p = Validator.parseCoordinates("999999,999999");
        assertEquals(999999, p.x());
        assertEquals(999999, p.y());
    }

    // ============= Тесты имен файлов =============

    @Test
    void testValidFilename() {
        assertDoesNotThrow(() -> Validator.validateFilename("maze.txt"));
        assertDoesNotThrow(() -> Validator.validateFilename("solution.txt"));
        assertDoesNotThrow(() -> Validator.validateFilename("maze_1.txt"));
        assertDoesNotThrow(() -> Validator.validateFilename("test-file.dat"));
        assertDoesNotThrow(() -> Validator.validateFilename("my_maze_file.txt"));
    }

    @Test
    void testInvalidFilenameEmpty() {
        assertThrows(IllegalArgumentException.class, () -> Validator.validateFilename(""));
        assertThrows(IllegalArgumentException.class, () -> Validator.validateFilename("   "));
    }

    @Test
    void testInvalidFilenameNull() {
        assertThrows(IllegalArgumentException.class, () -> Validator.validateFilename(null));
    }

    // ============= Комбинированные тесты =============

    @Test
    void testMultipleValidations() {
        // Проверяем несколько валидаций подряд
        assertDoesNotThrow(() -> {
            Validator.validateMazeSize(10, 10);
            Validator.validateFilename("maze.txt");
            Point p = Validator.parseCoordinates("1,1");
            assertNotNull(p);
        });
    }

    @Test
    void testValidationWithBoundaryValues() {
        // Минимальные значения
        assertDoesNotThrow(() -> Validator.validateMazeSize(1, 1));
        Point pMin = Validator.parseCoordinates("0,0");
        assertEquals(0, pMin.x());

        // Максимальные разумные значения
        assertDoesNotThrow(() -> Validator.validateMazeSize(10000, 10000));
        Point pMax = Validator.parseCoordinates("10000,10000");
        assertEquals(10000, pMax.x());
    }

    @Test
    void testErrorMessagesAreDescriptive() {
        // Проверяем, что ошибки содержат описание проблемы
        try {
            Validator.validateMazeSize(-1, 10);
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().length() > 0, "Error message should not be empty");
        }

        try {
            Validator.parseCoordinates("abc,def");
            fail("Should throw exception");
        } catch (IllegalArgumentException e) {
            assertTrue(e.getMessage().length() > 0, "Error message should describe the issue");
        }
    }
}
