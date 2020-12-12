import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.BiFunction;

import static java.lang.Integer.parseInt;
import static java.lang.Math.*;

public class Day12 extends AocDay<List<String>> {

    public static void main(String[] args) {
        new Day12().solve(true);
    }

    @Override
    List<String> prepareInput() throws Exception {
        return Files.readAllLines(Path.of(Day10.class.getResource("/day12").getPath()));
    }

    @Override
    String part1(List<String> input) {
        Direction currentDirection = Direction.E;
        Coordinate position = new Coordinate(0, 0);
        for (String s : input) {
            char direction = s.charAt(0);
            int amount = parseInt(s.substring(1));
            position = switch (direction) {
                case 'N' -> Direction.N.apply(position, amount);
                case 'S' -> Direction.S.apply(position, amount);
                case 'E' -> Direction.E.apply(position, amount);
                case 'W' -> Direction.W.apply(position, amount);
                case 'F' -> currentDirection.apply(position, amount);
                case 'L' -> {
                    currentDirection = currentDirection.rotateLeft(amount);
                    yield position;
                }
                case 'R' -> {
                    currentDirection = currentDirection.rotateRight(amount);
                    yield position;
                }
                default -> throw new IllegalArgumentException();
            };
        }

        return abs(position.x) + abs(position.y) + "";
    }

    @Override
    String part2(List<String> input) {
        Coordinate position = new Coordinate(0, 0);
        Coordinate waypointRelative = new Coordinate(10, 1);
        for (String s : input) {
            char direction = s.charAt(0);
            int amount = parseInt(s.substring(1));
            waypointRelative = switch (direction) {
                case 'N' -> Direction.N.apply(waypointRelative, amount);
                case 'S' -> Direction.S.apply(waypointRelative, amount);
                case 'E' -> Direction.E.apply(waypointRelative, amount);
                case 'W' -> Direction.W.apply(waypointRelative, amount);
                case 'F' -> {
                    position = new Coordinate(position.x + amount * waypointRelative.x, position.y + amount * waypointRelative.y);
                    yield waypointRelative;
                }
                case 'L' -> rotate(waypointRelative, toRadians(-amount));
                case 'R' -> rotate(waypointRelative, toRadians(amount));
                default -> throw new IllegalArgumentException();
            };
        }

        return abs(position.x) + abs(position.y) + "";
    }

    private static Coordinate rotate(Coordinate waypointRelative, double angle) {
        final int x = waypointRelative.x;
        final int y = waypointRelative.y;
        return new Coordinate(
                (int) Math.round(x * cos(angle) + y * sin(angle)),
                (int) Math.round(y * cos(angle) - x * sin(angle))
        );
    }

    private enum Direction {
        N((coordinate, amount) -> new Coordinate(coordinate.x, coordinate.y + amount)),
        E((coordinate, amount) -> new Coordinate(coordinate.x + amount, coordinate.y)),
        S((coordinate, amount) -> new Coordinate(coordinate.x, coordinate.y - amount)),
        W((coordinate, amount) -> new Coordinate(coordinate.x - amount, coordinate.y)),
        ;

        private final BiFunction<Coordinate, Integer, Coordinate> coordinateFunction;

        Direction(BiFunction<Coordinate, Integer, Coordinate> coordinateFunction) {
            this.coordinateFunction = coordinateFunction;
        }

        Coordinate apply(Coordinate coordinate, int amount) {
            return coordinateFunction.apply(coordinate, amount);
        }

        Direction rotateLeft(int degrees) {
            return values()[(this.ordinal() + values().length - degrees / 90) % values().length];
        }

        Direction rotateRight(int degrees) {
            return values()[(this.ordinal() + degrees / 90) % values().length];
        }
    }

    private record Coordinate(int x, int y) {

    }

}
