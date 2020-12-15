import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

public class Day15 extends AocDay<int[]> {

    public static void main(String[] args) {
        new Day15().solve(true);
    }

    @Override
    int[] prepareInput() throws Exception {
        return Arrays.stream(Files.readAllLines(Path.of(Day15.class.getResource("/day15").getPath())).get(0).split(","))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    @Override
    String part1(int[] input) {
        return solve(input, 2020);
    }

    @Override
    String part2(int[] input) {
        return solve(input, 30_000_000);
    }

    private String solve(int[] input, int n) {
        int[] lastSpokenIndices = new int[n];
        Arrays.fill(lastSpokenIndices, -1);
        int round = 0;
        for (int i = 0; i < input.length - 1; i++) {
            lastSpokenIndices[input[i]] = round;
            round++;
        }
        int lastSpoken = input[input.length - 1];
        while (round < n) {
            int speak;
            if (lastSpokenIndices[lastSpoken] != -1) {
                speak = round - 1 - lastSpokenIndices[lastSpoken];
            } else {
                speak = 0;
            }
            lastSpokenIndices[lastSpoken] = round - 1;

            lastSpoken = speak;
            round++;
        }
        return lastSpoken + "";
    }

}
