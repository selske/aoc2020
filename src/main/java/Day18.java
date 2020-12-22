import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Day18 extends AocDay<List<String>> {

    public static void main(String[] args) {
        new Day18().solve(true);
    }

    @Override
    List<String> prepareInput() throws Exception {
        return Files.readAllLines(Path.of(Day18.class.getResource("/day18").getPath()));
    }

    @Override
    String part1(List<String> input) {
        return input.stream()
                       .mapToLong(this::computePart1)
                       .sum() + "";
    }

    private long computePart1(String expression) {
        int groupDepth = 0;
        int groupIndex = -1;
        long currentVal = Integer.MIN_VALUE;
        char currentOperator = 'x';
        for (int i = 0; i < expression.toCharArray().length; i++) {
            long val = Character.getNumericValue(expression.charAt(i));
            if (expression.charAt(i) == ' ') continue;

            if (expression.charAt(i) == '(') {
                if (groupDepth == 0) {
                    groupIndex = i;
                }
                groupDepth++;
            } else if (expression.charAt(i) == ')') {
                groupDepth--;
                if (groupDepth == 0) {
                    val = computePart1(expression.substring(groupIndex + 1, i));
                }
            }

            if (groupDepth == 0) {
                if (expression.charAt(i) == '+' || expression.charAt(i) == '*') {
                    currentOperator = expression.charAt(i);
                }
                if (val != -1) {
                    if (currentVal == Integer.MIN_VALUE) {
                        currentVal = val;
                    } else {
                        if (currentOperator == '+') {
                            currentVal += val;
                        } else {
                            currentVal *= val;
                        }
                    }
                }
            }
        }
        return currentVal;
    }

    @Override
    String part2(List<String> input) {
        return input.stream()
                       .mapToLong(this::computePart2)
                       .sum() + "";
    }

    private long computePart2(String expression) {
        int groupDepth = 0;
        int groupIndex = -1;
        StringBuilder simplified = new StringBuilder();
        for (int i = 0; i < expression.toCharArray().length; i++) {
            if (expression.charAt(i) == '(') {
                if (groupDepth == 0) {
                    groupIndex = i;
                }
                groupDepth++;
            } else if (expression.charAt(i) == ')') {
                groupDepth--;
                if (groupDepth == 0) {
                    simplified.append(computePart2(expression.substring(groupIndex + 1, i)));
                }
            } else {
                if (groupDepth == 0) {
                    simplified.append(expression.charAt(i));
                }
            }
        }
        return computePart2Simplified(simplified.toString());
    }

    private long computePart2Simplified(String expression) {
        final String[] split = expression.split(" ");

        List<Long> multiply = new ArrayList<>();
        List<Long> add = new ArrayList<>();
        for (int i = 0; i < split.length; i++) {
            if (List.of("+", "*").contains(split[i])) {
                continue;
            }
            if (add.isEmpty() && i < split.length - 1 && split[i + 1].equals("+")) {
                add.add(Long.valueOf(split[i]));
            } else if (i > 0 && split[i - 1].equals("+")) {
                add.add(Long.valueOf(split[i]));
            } else {
                if (!add.isEmpty()) {
                    multiply.add(add.stream().mapToLong(Long::longValue).sum());
                    add.clear();
                    i--;
                    continue;
                }
                multiply.add(Long.valueOf(split[i]));
            }
        }
        if (!add.isEmpty()) {
            multiply.add(add.stream().mapToLong(Long::longValue).sum());
        }
        long product = 1;
        for (Long l : multiply) {
            product *= l;
        }
        return product;
    }

}
