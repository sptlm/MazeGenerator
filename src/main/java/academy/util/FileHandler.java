package academy.util;

import academy.model.CellType;
import academy.model.Maze;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class FileHandler {

    public static void saveMaze(Maze maze, String filename) throws IOException {
        Validator.validateFilename(filename);
        Path path = Path.of(filename);

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(maze.toString());
        }
    }

    public static void saveSolution(String content, String filename) throws IOException {
        Validator.validateFilename(filename);
        Path path = Path.of(filename);

        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(content);
        }
    }

    public static Maze loadMaze(String filename) throws IOException {
        Path path = Path.of(filename);

        if (!Files.exists(path)) {
            throw new IOException("File not found: " + filename);
        }

        try (BufferedReader reader = Files.newBufferedReader(path)) {
            List<String> lines = reader.lines().toList();

            if (lines.isEmpty()) {
                throw new IOException("Empty maze file");
            }

            int height = lines.size();
            int width = lines.get(0).length();

            for (String line : lines) {
                if (line.length() != width) {
                    throw new IOException("Inconsistent maze width in file");
                }
            }

            Maze maze = new Maze(width - 2, height - 2);

            for (int y = 0; y < height; y++) {
                String line = lines.get(y);
                for (int x = 0; x < width; x++) {
                    char c = line.charAt(x);
                    CellType cellType = CellType.fromSymbol(c);
                    maze.setCell(x, y, cellType);
                }
            }

            return maze;
        }
    }
}
