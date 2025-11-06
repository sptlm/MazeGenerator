package academy.util;

import academy.model.Point;

public class Validator {

    public static void validateMazeSize(int width, int height) {
        if (width <= 0) {
            throw new IllegalArgumentException("Width must be positive, got: " + width);
        }
        if (height <= 0) {
            throw new IllegalArgumentException("Height must be positive, got: " + height);
        }
    }

    public static Point parseCoordinates(String coordinates) {
        if (coordinates == null || coordinates.trim().isEmpty()) {
            throw new IllegalArgumentException("Coordinates cannot be empty");
        }

        String[] parts = coordinates.trim().split(",");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid point format: " + coordinates + ", expected format: x,y");
        }

        try {
            int x = Integer.parseInt(parts[0].trim());
            int y = Integer.parseInt(parts[1].trim());

            if (x < 0 || y < 0) {
                throw new IllegalArgumentException("Coordinates must be non-negative, got: " + coordinates);
            }

            return new Point(x, y);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid coordinate values. Expected numbers, got: " + coordinates, e);
        }
    }

    public static void validateFilename(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Filename cannot be empty");
        }
    }
}
