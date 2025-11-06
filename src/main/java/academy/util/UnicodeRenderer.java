package academy.util;

import academy.model.Maze;
import academy.model.Path;
import academy.model.Point;

public class UnicodeRenderer {

    static final String WALL_H = "─";
    static final String WALL_V = "│";

    static final String CORNER_TL = "┌";
    static final String CORNER_TR = "┐";
    static final String CORNER_BL = "└";
    static final String CORNER_BR = "┘";

    static final String T_DOWN = "┬";
    static final String T_UP = "┴";
    static final String T_RIGHT = "├";
    static final String T_LEFT = "┤";

    static final String CROSS = "┼";

    static final String FULL_BLOCK = "█";

    static final String START = "◉";
    static final String END = "◎";
    static final String PATH = "·";

    private static String getSymbol(Maze maze, int x, int y) {
        if (maze.getCell(x, y).getSymbol() != '#') {
            return String.valueOf(maze.getCell(x, y).getSymbol());
        }

        boolean top = y > 0 && maze.getCell(x, y - 1).getSymbol() == '#';
        boolean bottom = y < maze.getFullHeight() - 1 && maze.getCell(x, y + 1).getSymbol() == '#';
        boolean left = x > 0 && maze.getCell(x - 1, y).getSymbol() == '#';
        boolean right = x < maze.getFullWidth() - 1 && maze.getCell(x + 1, y).getSymbol() == '#';

        if (top && bottom && left && right) {
            return CROSS;
        }

        if (top && bottom && left) {
            return T_LEFT;
        }
        if (top && bottom && right) {
            return T_RIGHT;
        }
        if (top && left && right) {
            return T_UP;
        }
        if (bottom && left && right) {
            return T_DOWN;
        }

        if (top && bottom) {
            return WALL_V;
        }
        if (left && right) {
            return WALL_H;
        }
        if (top && left) {
            return CORNER_BR;
        }
        if (top && right) {
            return CORNER_BL;
        }
        if (bottom && left) {
            return CORNER_TR;
        }
        if (bottom && right) {
            return CORNER_TL;
        }

        if (top || bottom) {
            return WALL_V;
        }
        if (left || right) {
            return WALL_H;
        }

        return FULL_BLOCK;
    }

    public static String render(Maze maze, Point start, Point end) {
        StringBuilder sb = new StringBuilder();

        for (int y = 0; y < maze.getFullHeight(); y++) {
            for (int x = 0; x < maze.getFullWidth(); x++) {
                Point current = new Point(x, y);
                String symbol;

                if (current.equals(start)) {
                    symbol = START;
                } else if (current.equals(end)) {
                    symbol = END;
                } else if (maze.getCell(x, y).getSymbol() == '#') {
                    symbol = getSymbol(maze, x, y);
                } else {
                    symbol = String.valueOf(maze.getCell(x, y).getSymbol());
                }

                sb.append(symbol);
            }
            if (y < maze.getFullHeight() - 1) {
                sb.append('\n');
            }
        }

        return sb.toString();
    }

    public static String renderWithPath(Maze maze, Path path, Point start, Point end) {
        StringBuilder sb = new StringBuilder();

        for (int y = 0; y < maze.getFullHeight(); y++) {
            for (int x = 0; x < maze.getFullWidth(); x++) {
                Point current = new Point(x, y);
                String symbol;

                if (current.equals(start)) {
                    symbol = START;
                } else if (current.equals(end)) {
                    symbol = END;
                } else if (path.getPoints().contains(current)) {
                    symbol = PATH;
                } else if (maze.getCell(x, y).getSymbol() == '#') {
                    symbol = getSymbol(maze, x, y);
                } else {
                    symbol = String.valueOf(maze.getCell(x, y).getSymbol());
                }
                sb.append(symbol);
            }
            if (y < maze.getFullHeight() - 1) {
                sb.append('\n');
            }
        }

        return sb.toString();
    }
}
