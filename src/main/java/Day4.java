import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.toList;

public class Day4 extends AocDay<List<Day4.Passport>> {

    public static void main(String[] args) {
        new Day4().solve(true);
    }

    @Override
    List<Passport> prepareInput() throws Exception {
        List<String> lines = Files.lines(Path.of(Day4.class.getResource("/day4").getPath()))
                .collect(toList());
        List<Passport> passports = new ArrayList<>();
        StringBuilder currentPassport = new StringBuilder();
        for (String line : lines) {
            if (line.isBlank()) {
                passports.add(new Passport(currentPassport.toString()));
                currentPassport.setLength(0);
            } else {
                currentPassport.append(line).append(' ');
            }
        }
        passports.add(new Passport(currentPassport.toString()));
        return passports;
    }

    @Override
    String part1(List<Passport> passports) {
        return passports.stream()
                       .filter(Passport::hasRequiredFields)
                       .count() + "";
    }

    @Override
    String part2(List<Passport> passports) {
        return passports.stream()
                       .filter(Passport::isValid)
                       .count() + "";
    }

    static record Passport(String value) {

        public boolean hasRequiredFields() {
            return Stream.of(Field.values())
                    .allMatch(p -> p.isPresent(this));
        }

        public boolean isValid() {
            return Stream.of(Field.values())
                    .allMatch(field -> field.isValid(this));
        }

    }

    private enum Field {
        BYR("byr", passport -> {
            Matcher matcher = Pattern.compile("(\\d+)").matcher(passport);
            if (matcher.matches()) {
                int year = parseInt(matcher.group(1));
                return year >= 1920 && year <= 2002;
            } else {
                return false;
            }
        }),
        IYR("iyr", Pattern.compile("20(1[0-9]|20)").asMatchPredicate()),
        EYR("eyr", Pattern.compile("20(2[0-9]|30)").asMatchPredicate()),
        HGT("hgt", passport -> {
            Matcher matcher = Pattern.compile("(\\d+)(cm|in)").matcher(passport);
            if (matcher.matches()) {
                int height = parseInt(matcher.group(1));
                if ("in".equals(matcher.group(2))) {
                    return height >= 59 && height <= 76;
                } else {
                    return height >= 150 && height <= 193;
                }
            } else {
                return false;
            }
        }),
        HCL("hcl", Pattern.compile("#[0-9a-f]{6}").asMatchPredicate()),
        ECL("ecl", List.of("amb","blu","brn","gry","grn","hzl","oth")::contains),
        PID("pid", Pattern.compile("\\d{9}").asMatchPredicate()),
        ;

        private final Pattern pattern;
        private final Predicate<String> validator;

        Field(String name, Predicate<String> validator) {
            this.pattern = Pattern.compile(".*" + name + ":(\\S+) .*");
            this.validator = validator;
        }

        public boolean isPresent(Passport passport) {
            return pattern.matcher(passport.value()).matches();
        }

        public boolean isValid(Passport passport) {
            Matcher matcher = pattern.matcher(passport.value());
            if (!matcher.matches()) {
                return false;
            }
            return validator.test(matcher.group(1));
        }
    }

}
