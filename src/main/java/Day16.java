import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.toList;

public class Day16 extends AocDay<Day16.Input> {

    public static void main(String[] args) {
        new Day16().solve();;
    }

    @Override
    Input prepareInput() throws Exception {
        final List<String> lines = Files.readAllLines(Path.of(Day16.class.getResource("/day16").getPath()));
        int i = 0;
        List<Class> classes = new ArrayList<>();
        for (String line : lines) {
            if (line.isBlank()) break;
            final String[] split = line.split(": ");
            List<Range> ranges = Arrays.stream(split[1].split(" or "))
                    .map(Range::new)
                    .collect(toList());
            classes.add(new Class(split[0], ranges));
            i++;
        }
        i += 2;
        int[] myTicket = Arrays.stream(lines.get(i).split(",")).mapToInt(Integer::parseInt).toArray();
        i += 3;
        List<int[]> nearbyTickets = new ArrayList<>();
        for (; i < lines.size(); i++) {
            String line = lines.get(i);
            nearbyTickets.add(
                    Arrays.stream(line.split(","))
                            .mapToInt(Integer::parseInt)
                            .toArray()
            );
        }
        return new Input(classes, myTicket, nearbyTickets);
    }

    @Override
    String part1(Input input) {
        List<Range> ranges = input.classes.stream().map(Class::ranges).flatMap(Collection::stream).collect(toList());
        return input.nearbyTickets.stream()
                       .parallel()
                       .flatMapToInt(values -> Arrays.stream(values)
                               .filter(value -> ranges.stream()
                                       .noneMatch(range -> range.contains(value))))
                       .sum() + "";
    }

    @Override
    String part2(Input input) {
        List<Set<Class>> possibleClasses = Stream.generate(() -> new HashSet<>(input.classes)).limit(input.myTicket.length).collect(toList());
        input.nearbyTickets.stream()
                .filter(values -> {
                    for (int i = 0; i < values.length; i++) {
                        final int value = values[i];
                        final Set<Class> classes = possibleClasses.get(i);
                        if (classes.stream().noneMatch(c -> c.canContain(value))) {
                            return false;
                        }
                    }
                    return true;
                })
                .forEach(values -> {
                    for (int i = 0; i < values.length; i++) {
                        int val = values[i];
                        final Set<Class> classes = possibleClasses.get(i);
                        classes.removeIf(c -> !c.canContain(val));
                    }
                });

        long value = 1;
        for (int i = 0; i < possibleClasses.size(); i++) {
            final Set<Class> classes = possibleClasses.get(i);
            if (classes.size() == 1) {
                final Class deduced = classes.iterator().next();
                classes.clear();
                possibleClasses.forEach(pcs -> pcs.remove(deduced));
                if (deduced.name.startsWith("departure")) {
                    value *= input.myTicket[i];
                }
                i = 0;
            }
        }

        return value + "";
    }

    static record Range(int from, int to) {

        Range(String input) {
            this(parseInt(input.split("-")[0]), parseInt(input.split("-")[1]));
        }

        public boolean contains(int value) {
            return value >= from && value <= to;
        }

    }

    static record Class(String name, List<Range> ranges) {

        public boolean canContain(int val) {
            return ranges.stream().anyMatch(r -> r.contains(val));
        }

    }

    static record Input(List<Class> classes, int[] myTicket, List<int[]> nearbyTickets) {}

}
