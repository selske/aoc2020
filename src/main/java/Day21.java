import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.util.Map.Entry.comparingByKey;

public class Day21 extends AocDay<List<String>> {

    public static void main(String[] args) {
        new Day21().solve(true);
    }

    @Override
    List<String> prepareInput() throws Exception {
        return Files.readAllLines(Path.of(Day21.class.getResource("/day21").getPath()));
    }

    @Override
    String part1(List<String> input) {
        Map<String, Set<String>> possibleAllergens = new ConcurrentHashMap<>();
        List<String> allIngredients = new ArrayList<>();
        for (String line : input) {
            final String[] parts = line.split(" \\(contains ");
            final List<String> ingredients = Arrays.asList(parts[0].split(" "));
            allIngredients.addAll(ingredients);
            String allergens = parts[1].substring(0, parts[1].length() - 1);
            Arrays.stream(allergens.split(", "))
                    .forEach(allergen -> {
                        Set<String> possibleIngredients = possibleAllergens.computeIfAbsent(allergen, k -> new HashSet<>());
                        if (possibleIngredients.isEmpty()) {
                            possibleIngredients.addAll(ingredients);
                        } else {
                            possibleIngredients.removeIf(ingredient -> !ingredients.contains(ingredient));
                        }
                    });
        }

        final Set<String> PotentiallyDangerousIngredients = possibleAllergens.values().stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());

        return allIngredients.stream().filter(ingredient -> !PotentiallyDangerousIngredients.contains(ingredient)).count() + "";
    }

    @Override
    String part2(List<String> input) {
        Map<String, Set<String>> possibleAllergens = new HashMap<>();
        for (String line : input) {
            final String[] parts = line.split(" \\(contains ");
            final List<String> ingredients = Arrays.asList(parts[0].split(" "));
            String allergens = parts[1].substring(0, parts[1].length() - 1);
            Arrays.stream(allergens.split(", "))
                    .forEach(allergen -> {
                        Set<String> possibleIngredients = possibleAllergens.computeIfAbsent(allergen, k -> new HashSet<>());
                        if (possibleIngredients.isEmpty()) {
                            possibleIngredients.addAll(ingredients);
                        } else {
                            possibleIngredients.removeIf(ingredient -> !ingredients.contains(ingredient));
                        }
                    });
        }

        while (!possibleAllergens.values().stream().allMatch(v -> v.size() == 1)) {
            possibleAllergens.entrySet().stream()
                    .filter(e -> e.getValue().size() == 1)
                    .forEach(e -> {
                        for (Map.Entry<String, Set<String>> otherEntries : possibleAllergens.entrySet()) {
                            if (!otherEntries.getKey().equals(e.getKey())) {
                                otherEntries.getValue().removeAll(e.getValue());
                            }
                        }
                    });
        }

        return possibleAllergens.entrySet().stream()
                .sorted(comparingByKey())
                .map(Map.Entry::getValue)
                .flatMap(Collection::stream)
                .collect(Collectors.joining(","));
    }

}
