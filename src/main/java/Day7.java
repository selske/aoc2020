import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Pattern;

public class Day7 extends AocDay<Day7.Bag> {

    public static void main(String[] args) {
        new Day7().solve(true);
    }

    private static final Pattern PATTERN = Pattern.compile("(.*) bags contain (.*)");
    private static final Pattern CONTAIN_PATTERN = Pattern.compile("(\\d+) (.*) bags?.?");

    @Override
    Bag prepareInput() throws Exception {
        Map<String, Bag> bags = new HashMap<>();
        Files.lines(Path.of(Day7.class.getResource("/day7").getPath()))
                .flatMap(line -> PATTERN.matcher(line).results())
                .forEach(matchResult -> {
                    String colour = matchResult.group(1);
                    Bag bag = bags.computeIfAbsent(colour, Bag::new);
                    String contain = matchResult.group(2);
                    Arrays.stream(contain.split(", "))
                            .flatMap(c -> CONTAIN_PATTERN.matcher(c).results())
                            .forEach(containMatchResult -> {
                                Bag containBag = bags.computeIfAbsent(containMatchResult.group(2), Bag::new);
                                containBag.containedIn(bag);
                                bag.contains(containBag, Integer.valueOf(containMatchResult.group(1)));
                            });
                });
        return bags.get("shiny gold");
    }

    @Override
    String part1(Bag shinyGold) {
        Set<Bag> canContain = new HashSet<>();
        canContain(canContain, shinyGold);
        return canContain.size() + "";
    }

    private void canContain(Set<Bag> canContain, Bag bagToCheck) {
        bagToCheck.containedIn.forEach(bag -> {
            if (canContain.add(bag)) {
                canContain(canContain, bag);
            }
        });
    }

    @Override
    String part2(Bag shinyGold) {
        return countContaining(shinyGold) + "";
    }

    private int countContaining(Bag bag) {
        if (bag.contains.isEmpty()) {
            return 0;
        }
        return bag.contains.entrySet().stream()
                .mapToInt(e -> (countContaining(e.getKey()) + 1) * e.getValue())
                .sum();
    }

    protected static class Bag {

        private final String colour;
        private final Set<Bag> containedIn = new HashSet<>();
        private final Map<Bag, Integer> contains = new HashMap<>();

        private Bag(String colour) {
            this.colour = colour;
        }

        public void contains(Bag bag, Integer amount) {
            contains.put(bag, amount);
        }

        public void containedIn(Bag bag) {
            this.containedIn.add(bag);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Bag bag = (Bag) o;
            return colour.equals(bag.colour);
        }

        @Override
        public int hashCode() {
            return Objects.hash(colour);
        }

        @Override
        public String toString() {
            return "Bag{" +
                   "colour='" + colour + '\'' +
                   '}';
        }

    }

}
