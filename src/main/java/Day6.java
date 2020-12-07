import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public class Day6 extends AocDay<List<String>> {

    public static void main(String[] args) {
        new Day6().solve(true);
    }

    @Override
    List<String> prepareInput() throws Exception {
        return Files.lines(Path.of(Day6.class.getResource("/day6").getPath()))
                .collect(toList());
    }

    @Override
    String part1(List<String> answers) {
        Set<Character> yesses = new HashSet<>(26);
        int count = 0;
        for (String answer : answers) {
            if (answer.isBlank()) {
                count += yesses.size();
                yesses.clear();
            } else {
                for (char c : answer.toCharArray()) {
                    yesses.add(c);
                }
            }
        }
        count += yesses.size();
        return count + "";
    }

    @Override
    String part2(List<String> answers) {
        Set<Character> yesses = new HashSet<>(26);
        boolean newGroup = true;
        int count = 0;
        for (String answer : answers) {
            if (answer.isBlank()) {
                count += yesses.size();
                yesses.clear();
                newGroup = true;
            } else {
                if (newGroup) {
                    for (char c : answer.toCharArray()) {
                        yesses.add(c);
                    }
                    newGroup = false;
                } else {
                    yesses.removeIf(c -> answer.indexOf(c) < 0);
                }
            }
        }
        count += yesses.size();
        return count + "";
    }

}
