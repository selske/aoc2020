import java.nio.file.Files;
import java.nio.file.Path;

public class Day9 extends AocDay<long[]> {

    public static final int PREAMBLE = 25;

    public static void main(String[] args) {
        new Day9().solve(true);
    }

    @Override
    long[] prepareInput() throws Exception {
        return Files.lines(Path.of(Day9.class.getResource("/day9").getPath()))
                .mapToLong(Long::parseLong)
                .toArray();
    }

    private long invalidNumber;

    @Override
    String part1(long[] numbers) {
        xmas:
        for (int i = PREAMBLE; i < numbers.length; i++) {
            long val = numbers[i];
            for (int j = i - PREAMBLE; j < i - 1; j++) {
                long a = numbers[j];
                for (int k = j + 1; k < i; k++) {
                    long b = numbers[k];
                    if (a + b == val) {
                        continue xmas;
                    }
                }
            }
            invalidNumber = val;
            return invalidNumber + "";
        }

        throw new IllegalArgumentException();
    }

    @Override
    String part2(long[] numbers) {
        for (int i = 0; i < numbers.length; i++) {
            long val = numbers[i];
            for (int j = i + 1; j < numbers.length; j++) {
                long b = numbers[j];
                val += b;
                if (val == invalidNumber) {
                    long smallest = numbers[i];
                    long largest = numbers[i];
                    for (int k = i + 1; k < j; k++) {
                        if (numbers[k] < smallest) {
                            smallest = numbers[k];
                        } else if (numbers[k] > largest) {
                            largest = numbers[k];
                        }
                    }
                    return smallest + largest + "";
                } else if (val > invalidNumber) {
                    break;
                }
            }
        }
        throw new IllegalArgumentException();
    }

}
