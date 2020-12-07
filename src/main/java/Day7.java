import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;

public class Day7 extends AocDay<Map<String, Day7.Bag>> {

    public static void main(String[] args) {
        new Day7().solve(true);
    }

    private static final Pattern PATTERN = Pattern.compile("(.*) bags contain (.*)");
    private static final Pattern CONTAIN_PATTERN = Pattern.compile("(\\d+) (.*) bags?.?");

    @Override
    Map<String, Bag> prepareInput() throws Exception {
        Map<String, Bag> bags = new HashMap<>();
        Files.lines(Path.of(Day7.class.getResource("/day7").getPath()))
                .collect(toList()).forEach(line -> {
            Matcher matcher = PATTERN.matcher(line);
            if (!matcher.matches()) {
                throw new IllegalArgumentException();
            }
            String colour = matcher.group(1);
            Bag bag = bags.computeIfAbsent(colour, Bag::new);
            String contain = matcher.group(2);
            Arrays.stream(contain.split(", "))
                    .filter(c -> !"no other bags.".equals(c))
                    .forEach(c -> {
                        Matcher containMatcher = CONTAIN_PATTERN.matcher(c);
                        if (!containMatcher.matches()) {
                            throw new IllegalArgumentException();
                        }
                        Bag containBag = bags.computeIfAbsent(containMatcher.group(2), Bag::new);
                        containBag.containedIn(bag);
                        bag.contains(containBag, Integer.valueOf(containMatcher.group(1)));
                    });
        });
        return bags;
    }

    @Override
    String part1(Map<String, Bag> bags) {
        Bag shinyGold = bags.get("shiny gold");

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
    String part2(Map<String, Bag> bags) {
        Bag shinyGold = bags.get("shiny gold");

        return countContaining(shinyGold) - 1 + "";
    }

    private int countContaining(Bag bag) {
        if (bag.contains.isEmpty()) {
            return 1;
        }
        int sum = bag.contains.entrySet().stream()
                .mapToInt(e -> countContaining(e.getKey()) * e.getValue())
                .sum();
        return sum + 1;
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
