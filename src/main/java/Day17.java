import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toSet;

public class Day17 extends AocDay<Set<List<Integer>>> {

    public static void main(String[] args) {
        new Day17().solve(true);
    }

    @Override
    Set<List<Integer>> prepareInput() throws Exception {
        final List<String> lines = Files.readAllLines(Path.of(Day17.class.getResource("/day17").getPath()));

        final Set<List<Integer>> cubes = new HashSet<>();
        for (int y = 0; y < lines.size(); y++) {
            String line = lines.get(y);
            for (int x = 0; x < line.toCharArray().length; x++) {
                if (line.charAt(x) == '#') {
                    cubes.add(List.of(x, y));
                }
            }
        }
        return cubes;
    }

    @Override
    String part1(Set<List<Integer>> input) {
        return solve(input, 3);
    }

    @Override
    String part2(Set<List<Integer>> input) {
        return solve(input, 4);
    }

    private String solve(Set<List<Integer>> input, int dimensions) {
        Set<List<Integer>> cubes = input.stream()
                .map(i -> {
                    final List<Integer> expanded = new ArrayList<>(Collections.nCopies(dimensions, 0));
                    expanded.set(0, i.get(0));
                    expanded.set(1, i.get(1));
                    return expanded;
                })
                .collect(toSet());

        for (int i = 0; i < 6; i++) {
            Bounds bounds = calculateNewBounds(cubes, dimensions);

            List<Integer> current = new ArrayList<>(dimensions);
            for (int val : bounds.minValues) {
                current.add(val);
            }

            List<List<Integer>> range = new ArrayList<>();
            while (true) {
                range.add(new ArrayList<>(current));
                boolean done = true;
                for (int dimension = 0; dimension < dimensions; dimension++) {
                    if (current.get(dimension) != bounds.maxValues[dimension]) {
                        done = false;
                        break;
                    }
                }
                if (done) {
                    break;
                }

                for (int dimension = 0; dimension < dimensions; dimension++) {
                    if (current.get(dimension) < bounds.maxValues[dimension]) {
                        current.set(dimension, current.get(dimension) + 1);
                        break;
                    } else {
                        current.set(dimension, bounds.minValues[dimension]);
                    }
                }
            }
            Set<List<Integer>> finalCubes = cubes;
            cubes = range.stream()
                    .parallel()
                    .map(cube -> active(finalCubes, cube, dimensions))
                    .flatMap(Optional::stream)
                    .collect(Collectors.toSet());
        }

        return cubes.size() + "";
    }

    private Optional<List<Integer>> active(Set<List<Integer>> cubes, List<Integer> current, int dimensions) {
        long activeNeighbours = getNeighbours(current, dimensions).stream()
                .filter(cubes::contains)
                .count();
        if (cubes.contains(current)) {
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

    private Bounds calculateNewBounds(Set<List<Integer>> coordinates, int dimensions) {
        int[] minValues = new int[dimensions];
        Arrays.fill(minValues, Integer.MAX_VALUE);
        int[] maxValues = new int[dimensions];
        Arrays.fill(maxValues, Integer.MIN_VALUE);

        for (List<Integer> coordinate : coordinates) {
            for (int i = 0; i < coordinate.size(); i++) {
                final Integer c = coordinate.get(i);
                if (c < minValues[i]) {
                    minValues[i] = c;
                }
                if (c > maxValues[i]) {
                    maxValues[i] = c;
                }
            }
        }
        for (int i = 0; i < minValues.length; i++) {
            if (minValues[i] == Integer.MAX_VALUE) {
                minValues[i] = 0;
            }
            minValues[i]--;
        }
        for (int i = 0; i < maxValues.length; i++) {
            if (maxValues[i] == Integer.MIN_VALUE) {
                maxValues[i] = 0;
            }
            maxValues[i]++;
        }

        return new Bounds(minValues, maxValues);
    }

    public Collection<List<Integer>> getNeighbours(List<Integer> coordinate, int dimensions) {
        int[] minValues = new int[dimensions];
        Arrays.fill(minValues, -1);
        int[] maxValues = new int[dimensions];
        Arrays.fill(maxValues, 1);

        Set<List<Integer>> neighbours = new HashSet<>();
        int[] current = Arrays.copyOf(minValues, dimensions);
        while (true) {
            List<Integer> neighbour = new ArrayList<>(coordinate);
            for (int i = 0; i < neighbour.size(); i++) {
                neighbour.set(i, neighbour.get(i) + current[i]);
            }
            if (!neighbour.equals(coordinate)) {
                neighbours.add(neighbour);
            }

            boolean done = true;
            for (int dimension = 0; dimension < dimensions; dimension++) {
                if (current[dimension] != maxValues[dimension]) {
                    done = false;
                    break;
                }
            }
            if (done) {
                break;
            }

            for (int dimension = 0; dimension < dimensions; dimension++) {
                if (current[dimension] < maxValues[dimension]) {
                    current[dimension]++;
                    break;
                } else {
                    current[dimension] = minValues[dimension];
                }
            }
        }
        return neighbours;
    }

    static record Bounds(int[] minValues, int[] maxValues) {

    }

}
