import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;

public class Day3 extends AocDay<List<String>> {

    public static void main(String[] args) {
        new Day3().solve(true);
    }

    @Override
    List<String> prepareInput() throws Exception {
        return Files.lines(Path.of(Day3.class.getResource("/day3").getPath()))
                .collect(toList());
    }

    @Override
    String part1(List<String> input) {
        return countSlope(input, 3, 1) + "";
    }

    private int countSlope(List<String> input, int right, int down) {
        int count = 0;
        for (int row = down, col = right; row < input.size(); row += down, col += right) {
            String line = input.get(row);
            char val = line.charAt(col % line.length());
            if (val == '#') {
                count++;
            }
        }
        return count;
    }

    @Override
    String part2(List<String> input) {
        record Slope(int right, int down) {}
        int[] counts = Stream.of(
                new Slope(1, 1),
                new Slope(3, 1),
                new Slope(5, 1),
                new Slope(7, 1),
                new Slope(1, 2)
        )
                .mapToInt(slope -> countSlope(input, slope.right, slope.down))
                .toArray();
        long product = 1;
        for (int count : counts) {
            product *= count;
        }
        return product + "";
    }

}
