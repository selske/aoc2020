import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.Integer.max;

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
    String part2(int[] numbersWithoutLeadingZero) {
        int[] numbers = new int[numbersWithoutLeadingZero.length + 1];
        System.arraycopy(numbersWithoutLeadingZero, 0, numbers, 1, numbersWithoutLeadingZero.length);
        int previous = 0;
        int oneCount = 0;
        long possibilities = 1;
        for (int i = 0; i < numbers.length; i++) {
            int number = numbers[i];
            if (number - previous == 1) {
                oneCount++;
            } else if (number - previous >= 3) {
                possibilities *= permutations(Arrays.copyOfRange(numbers, max(i - oneCount - 1, 0), i));
                oneCount = 0;
            }
            previous = number;
        }
        possibilities *= permutations(Arrays.copyOfRange(numbers, numbers.length - oneCount - 1, numbers.length));
        return possibilities + "";
    }

    private int permutations(int[] subList) {
        Set<List<Integer>> permutations = Set.of(List.of(subList[0]));
        for (int i = 1; i < subList.length - 1; i++) {
            int val = subList[i];
            permutations = permutations.stream()
                    .flatMap(p -> {
                        List<Integer> withNext = new ArrayList<>(p);
                        withNext.add(val);
                        return Stream.of(p, withNext);
                    })
                    .collect(Collectors.toSet());
        }
        permutations = permutations.stream()
                .map(p -> {
                    ArrayList<Integer> withLast = new ArrayList<>(p);
                    withLast.add(subList[subList.length - 1]);
                    return withLast;
                })
                .filter(this::isValid)
                .collect(Collectors.toSet());

        return permutations.size();
    }

    private boolean isValid(List<Integer> integers) {
        for (int i = 0; i < integers.size() - 1; i++) {
            if (integers.get(i + 1) - integers.get(i) > 3) {
                return false;
            }
        }
        return true;
    }

}
