import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Function;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toSet;

public class Day17 extends AocDay<Set<Day17.Coordinate>> {

    public static void main(String[] args) {
        new Day17().solve(true);
    }

    @Override
    Set<Coordinate> prepareInput() throws Exception {
        final List<String> lines = Files.readAllLines(Path.of(Day15.class.getResource("/day17").getPath()));

        final Set<Coordinate> cubes = new HashSet<>();
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.toCharArray().length; x++) {
                if (line.charAt(x) == '#') {
                    cubes.add(new Coordinate(x, y, 0, 0));
                }
            }
        }
        return cubes;
    }

    @Override
    String part1(Set<Coordinate> input) {
        return solve(input, false);
    }

    @Override
    String part2(Set<Coordinate> input) {
        return solve(input, true);
    }

    private String solve(Set<Coordinate> input, boolean include4thDimension) {
        Set<Coordinate> cubes = input;

        for (int i = 0; i < 6; i++) {
            Bounds bounds = calculateNewBounds(cubes);

            Set<Coordinate> finalCubes = cubes;
            cubes = IntStream.rangeClosed(bounds.xMin, bounds.xMax)
                    .parallel()
                    .mapToObj(x -> IntStream.rangeClosed(bounds.yMin, bounds.yMax)
                            .mapToObj(y -> IntStream.rangeClosed(bounds.zMin, bounds.zMax)
                                    .mapToObj(z -> IntStream.rangeClosed(bounds.wMin, bounds.wMax)
                                            .filter(w -> include4thDimension || w == 0)
                                            .mapToObj(w -> {
                                                Coordinate current = new Coordinate(x, y, z, w);
                                                return active(finalCubes, current, include4thDimension);
                                            })
                                            .flatMap(Optional::stream)
                                    )
                                    .flatMap(Function.identity())
                            )
                            .flatMap(Function.identity())
                    )
                    .flatMap(Function.identity())
                    .collect(toSet());
        }

        return cubes.size() + "";
    }

    private Optional<Coordinate> active(Set<Coordinate> finalCubes, Coordinate current, boolean include4thDimension) {
        long activeNeighbours = current.getNeighbours(include4thDimension).stream()
                .filter(finalCubes::contains)
                .count();
        if (finalCubes.contains(current)) {
            if (activeNeighbours == 2 || activeNeighbours == 3) {
                return Optional.of(current);
            }
        } else {
            if (activeNeighbours == 3) {
                return Optional.of(current);
            }
        }
        return Optional.empty();
    }

    private Bounds calculateNewBounds(Set<Coordinate> coordinates) {
        Bounds bounds = new Bounds();
        for (Coordinate coordinate : coordinates) {
            if (coordinate.x < bounds.xMin) {
                bounds.xMin = coordinate.x;
            } else if (coordinate.x > bounds.xMax) {
                bounds.xMax = coordinate.x;
            }
            if (coordinate.y < bounds.yMin) {
                bounds.yMin = coordinate.y;
            } else if (coordinate.y > bounds.yMax) {
                bounds.yMax = coordinate.y;
            }
            if (coordinate.z < bounds.zMin) {
                bounds.zMin = coordinate.z;
            } else if (coordinate.z > bounds.zMax) {
                bounds.zMax = coordinate.z;
            }
            if (coordinate.w < bounds.wMin) {
                bounds.wMin = coordinate.w;
            } else if (coordinate.w > bounds.wMax) {
                bounds.wMax = coordinate.w;
            }
        }

        if (bounds.xMin == Integer.MAX_VALUE) {
            bounds.xMin = 0;
        }
        if (bounds.xMax == Integer.MIN_VALUE) {
            bounds.xMax = 0;
        }
        if (bounds.yMin == Integer.MAX_VALUE) {
            bounds.yMin = 0;
        }
        if (bounds.yMax == Integer.MIN_VALUE) {
            bounds.yMax = 0;
        }
        if (bounds.zMin == Integer.MAX_VALUE) {
            bounds.zMin = 0;
        }
        if (bounds.zMax == Integer.MIN_VALUE) {
            bounds.zMax = 0;
        }
        if (bounds.wMin == Integer.MAX_VALUE) {
            bounds.wMin = 0;
        }
        if (bounds.wMax == Integer.MIN_VALUE) {
            bounds.wMax = 0;
        }

        bounds.xMin--;
        bounds.xMax++;
        bounds.yMin--;
        bounds.yMax++;
        bounds.zMin--;
        bounds.zMax++;
        bounds.wMin--;
        bounds.wMax++;

        return bounds;
    }

    static record Coordinate(int x, int y, int z, int w) {

        public Collection<Coordinate> getNeighbours(boolean include4thDimension) {
            return IntStream.rangeClosed(-1, 1)
                    .mapToObj(x -> IntStream.rangeClosed(-1, 1)
                            .mapToObj(y -> IntStream.rangeClosed(-1, 1)
                                    .mapToObj(z -> IntStream.rangeClosed(-1, 1)
                                            .filter(w -> !(x == 0 && y == 0 && z == 0 && w == 0))
                                            .filter(w -> include4thDimension || w == 0)
                                            .mapToObj(w -> (CoordinateFunction) coordinate -> new Coordinate(coordinate.x + x, coordinate.y + y, coordinate.z + z, coordinate.w + w))
                                    )
                                    .flatMap(Function.identity())
                            )
                            .flatMap(Function.identity())
                    )
                    .flatMap(Function.identity())
                    .map(cf -> cf.apply(this))
                    .collect(toSet());
        }

        private interface CoordinateFunction {

            Coordinate apply(Coordinate input);

        }

    }

    static class Bounds {

        int xMin = Integer.MAX_VALUE;
        int xMax = Integer.MIN_VALUE;
        int yMin = Integer.MAX_VALUE;
        int yMax = Integer.MIN_VALUE;
        int zMin = Integer.MAX_VALUE;
        int zMax = Integer.MIN_VALUE;
        int wMin = Integer.MAX_VALUE;
        int wMax = Integer.MIN_VALUE;

        @Override
        public String toString() {
            return "Bounds{" +
                   "xMin=" + xMin +
                   ", xMax=" + xMax +
                   ", yMin=" + yMin +
                   ", yMax=" + yMax +
                   ", zMin=" + zMin +
                   ", zMax=" + zMax +
                   ", wMin=" + wMin +
                   ", wMax=" + wMax +
                   '}';
        }

    }

}
