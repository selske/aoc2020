import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Day1 extends AocDay<int[]> {

    public static void main(String[] args) {
        new Day1().solve(true);
    }

    @Override
    int[] prepareInput() throws IOException {
        return Files.lines(Path.of(Day1.class.getResource("/day1").getPath()))
                .mapToInt(Integer::parseInt)
                .toArray();
    }

    @Override
    String part1(int[] values) {
        for (int i = 0; i < values.length - 1; i++) {
            int a = values[i];
            for (int j = i + 1; j < values.length; j++) {
                int b = values[j];
                if (a + b == 2020) {
                    return a * b + "";
                }
            }
        }
        throw new IllegalArgumentException();
    }

    @Override
    String part2(int[] values) {
        for (int i = 0; i < values.length - 2; i++) {
            int a = values[i];
            for (int j = i + 1; j < values.length - 1; j++) {
                int b = values[j];
                for (int k = j + 1; k < values.length; k++) {
                    int c = values[k];
                    if (a + b + c == 2020) {
                        return a * b * c + "";
                    }
                }
            }
        }
        throw new IllegalArgumentException();
    }

}
