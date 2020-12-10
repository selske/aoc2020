import java.nio.file.Files;
import java.nio.file.Path;

public class Day10 extends AocDay<int[]> {

    public static void main(String[] args) {
        new Day10().solve(true);
    }

    @Override
    int[] prepareInput() throws Exception {
        return Files.lines(Path.of(Day10.class.getResource("/day10").getPath()))
                .mapToInt(Integer::parseInt)
                .sorted()
                .toArray();
    }

    @Override
    String part1(int[] numbers) {
        int previous = 0;
        int oneCount = 0;
        int threeCount = 1;
        for (int number : numbers) {
            if (number - previous == 1) {
                oneCount++;
            } else if (number - previous == 3) {
                threeCount++;
            }
            previous = number;
        }
        return oneCount * threeCount + "";
    }

    @Override
    String part2(int[] numbers) {
        int previous = 0;
        int oneCount = 0;
        long possibilities = 1;
        for (int number : numbers) {
            if (number - previous == 1) {
                oneCount++;
            } else if (number - previous == 3) {
                possibilities = adjustPossibilities(oneCount, possibilities);
                oneCount = 0;
            }
            previous = number;
        }
        possibilities = adjustPossibilities(oneCount, possibilities);
        return possibilities + "";
    }

    private long adjustPossibilities(int oneCount, long possibilities) {
        switch (oneCount) {
            case 0, 1 -> possibilities *= 1;
            case 2 -> possibilities *= 2;
            case 3 -> possibilities *= 4;
            case 4 -> possibilities *= 7;
            default -> throw new IllegalArgumentException();
        }
        return possibilities;
    }

}
