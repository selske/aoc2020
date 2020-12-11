import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;

public class Day11 extends AocDay<Map<Day11.Coordinate, Day11.Seat>> {

    public static void main(String[] args) {
        new Day11().solve(true);
    }

    private int rows;
    private int cols;

    @Override
    Map<Coordinate, Seat> prepareInput() throws Exception {
        List<String> lines = Files.readAllLines(Path.of(Day10.class.getResource("/day11").getPath()));
        Map<Coordinate, Seat> seats = new HashMap<>();
        for (int row = 0; row < lines.size(); row++) {
            String line = lines.get(row);
            for (int col = 0; col < line.length(); col++) {
                final Coordinate coordinate = new Coordinate(row, col);
                switch (line.charAt(col)) {
                    case 'L' -> seats.put(coordinate, new Seat(coordinate, false));
                    case '#' -> seats.put(coordinate, new Seat(coordinate, true));
                }
            }
        }
        rows = lines.size();
        cols = lines.get(0).length();
        return seats;
    }

    @Override
    String part1(Map<Coordinate, Seat> initialSeats) {
        return solve(initialSeats, 2, 4);
    }

    @Override
    String part2(Map<Coordinate, Seat> seats) {
        return solve(seats, Integer.MAX_VALUE, 5);
    }

    private String solve(Map<Coordinate, Seat> initialSeats, int limit, int tolerance) {
        Map<Coordinate, Seat> previousSeats = initialSeats;
        while (true) {
            Map<Coordinate, Seat> finalPreviousSeats = previousSeats;
            Map<Coordinate, Seat> seats = previousSeats.entrySet().stream()
                    .parallel()
                    .collect(toMap(Map.Entry::getKey, e -> {
                        Seat seat = e.getValue();
                        long count = countNeighbours(finalPreviousSeats, seat, limit);
                        if (!seat.occupied && count == 0) {
                            return new Seat(seat.coordinate, true);
                        } else if (seat.occupied && count >= tolerance) {
                            return new Seat(seat.coordinate, false);
                        } else {
                            return seat;
                        }
                    }));
            if (previousSeats.equals(seats)) {
                return seats.values().stream().filter(Seat::occupied).count() + "";
            }
            previousSeats = seats;
        }
    }

    private interface CoordinateFunction{

        Coordinate apply(Coordinate input, int i);

    }

    long countNeighbours(Map<Coordinate, Seat> seats, Seat seat, int limit) {
        return Stream.<CoordinateFunction>of(
                (coordinate, i) -> new Coordinate(coordinate.row, coordinate.col + i),
                (coordinate, i) -> new Coordinate(coordinate.row, coordinate.col - i),
                (coordinate, i) -> new Coordinate(coordinate.row + i, coordinate.col + i),
                (coordinate, i) -> new Coordinate(coordinate.row + i, coordinate.col - i),
                (coordinate, i) -> new Coordinate(coordinate.row - i, coordinate.col + i),
                (coordinate, i) -> new Coordinate(coordinate.row - i, coordinate.col - i),
                (coordinate, i) -> new Coordinate(coordinate.row + i, coordinate.col),
                (coordinate, i) -> new Coordinate(coordinate.row - i, coordinate.col)
        )
                .filter(coordinateFunction -> {
                    for (int i = 1; i < limit; i++) {
                        final Coordinate coordinate = coordinateFunction.apply(seat.coordinate, i);
                        if (coordinate.row < 0 || coordinate.row > rows || coordinate.col < 0 || coordinate.col > cols) {
                            return false;
                        }
                        Seat potentialNeighbour = seats.get(coordinate);
                        if (potentialNeighbour != null) {
                            return potentialNeighbour.occupied();
                        }
                    }
                    return false;
                })
                .count();
    }

    record Coordinate(int row, int col) {}

    record Seat(Coordinate coordinate, boolean occupied) {

    }

}
