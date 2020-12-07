import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.toList;

public class Day2 extends AocDay<List<Day2.Input>> {

    public static void main(String[] args) {
        new Day2().solve(true);
    }

    @Override
    List<Input> prepareInput() throws Exception {
        return Files.lines(Path.of(Day2.class.getResource("/day2").getPath()))
                .map(Input::parse)
                .collect(toList());
    }

    @Override
    String part1(List<Input> input) {
        return input.stream()
                       .parallel()
                       .filter(in -> {
                           long count = in.value.chars().filter(i -> i == in.letter).count();
                           return count >= in.min && count <= in.max;
                       })
                       .count() + "";
    }

    @Override
    String part2(List<Input> input) {
        return input.stream()
                       .filter(in -> in.value.charAt(in.min - 1) == in.letter ^ in.value.charAt(in.max - 1) == in.letter)
                       .count() + "";
    }

    static record Input(int min, int max, char letter, String value) {

        private static final Pattern PATTERN = Pattern.compile("(\\d+)-(\\d+) ([a-z]): ([a-z]+)");

        public static Input parse(String input) {
            Matcher matcher = PATTERN.matcher(input);
            matcher.matches();

            int min = parseInt(matcher.group(1));
            int max = parseInt(matcher.group(2));
            char letter = matcher.group(3).charAt(0);
            String value = matcher.group(4);
            return new Input(min, max, letter, value);
        }

    }

}
