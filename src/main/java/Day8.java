import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Integer.parseInt;

public class Day8 extends AocDay<List<String>> {

    public static void main(String[] args) {
        new Day8().solve(true);
    }

    @Override
    List<String> prepareInput() throws Exception {
        return Files.lines(Path.of(Day8.class.getResource("/day8").getPath()))
                .collect(Collectors.toList());
    }

    @Override
    String part1(List<String> lines) {
        return run(lines).accumulatorValue() + "";
    }

    @Override
    String part2(List<String> lines) {
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            Outcome outcome = null;
            if (line.startsWith("nop")) {
                lines.set(i, line.replaceFirst("nop", "jmp"));
                outcome = run(lines);
                lines.set(i, line);
            } else if (line.startsWith("jmp")) {
                lines.set(i, line.replaceFirst("jmp", "nop"));
                outcome = run(lines);
                lines.set(i, line);
            }
            if (outcome != null && !outcome.isInfiniteLoop()) {
                return outcome.accumulatorValue + "";
            }
        }
        throw new IllegalArgumentException();
    }

    private Outcome run(List<String> lines) {
        boolean[] visited = new boolean[lines.size()];
        int accumulator = 0;
        for (int i = 0; i < lines.size(); ) {
            if (visited[i]) {
                return new Outcome(true, accumulator);
            }
            visited[i] = true;
            String[] split = lines.get(i).split(" ");
            switch (split[0]) {
                case "nop" -> i++;
                case "acc" -> {
                    accumulator += parseInt(split[1]);
                    i++;
                }
                case "jmp" -> i += parseInt(split[1]);
                default -> throw new IllegalArgumentException();
            }
        }
        return new Outcome(false, accumulator);
    }

    private record Outcome(boolean isInfiniteLoop, Integer accumulatorValue) {}

}
