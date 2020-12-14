import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;
import static java.util.stream.Collectors.toList;

public class Day14 extends AocDay<List<String>> {

    public static void main(String[] args) {
        new Day14().solve(true);
    }

    @Override
    List<String> prepareInput() throws Exception {
        return Files.readAllLines(Path.of(Day14.class.getResource("/day14").getPath()));
    }

    private static final Pattern MEM_PATTERN = Pattern.compile("mem\\[(\\d+)] = (\\d+)");

    @Override
    String part1(List<String> input) {
        Map<Integer, Long> memory = new HashMap<>();
        long oneMask = 0;
        long zeroMask = Long.MAX_VALUE;
        for (String line : input) {
            if (line.startsWith("mask")) {
                oneMask = 0;
                zeroMask = Long.MAX_VALUE;
                String mask = line.split(" = ")[1];
                for (int i = 0; i < mask.toCharArray().length; i++) {
                    final char val = mask.toCharArray()[i];
                    if (val == '1') {
                        oneMask += 1L << mask.length() - i - 1;
                    } else if (val == '0') {
                        zeroMask -= 1L << mask.length() - i - 1;
                    }
                }
            } else if (line.startsWith("mem")) {
                final Matcher matcher = MEM_PATTERN.matcher(line);
                if (!matcher.matches()) {
                    throw new IllegalArgumentException();
                }
                int address = parseInt(matcher.group(1));
                long value = parseLong(matcher.group(2));
                value |= oneMask;
                value &= zeroMask;
                memory.put(address, value);
            }
        }
        return memory.values().stream().mapToLong(Long::longValue).sum() + "";
    }

    @Override
    String part2(List<String> input) {
        Map<Long, Long> memory = new HashMap<>();
        String mask = null;
        long oneMask = 0;
        for (String line : input) {
            if (line.startsWith("mask")) {
                oneMask = 0;
                mask = line.split(" = ")[1];
                for (int i = 0; i < mask.toCharArray().length; i++) {
                    final char val = mask.toCharArray()[i];
                    if (val == '1') {
                        oneMask += 1L << mask.length() - i - 1;
                    }
                }
            } else if (line.startsWith("mem")) {
                final Matcher matcher = MEM_PATTERN.matcher(line);
                if (!matcher.matches()) {
                    throw new IllegalArgumentException();
                }
                int address = parseInt(matcher.group(1));
                long value = parseLong(matcher.group(2));
                final List<Integer> floatingBits = new ArrayList<>();
                for (int i = 0; i < mask.length(); i++) {
                    if (mask.charAt(i) == 'X') {
                        floatingBits.add(mask.length() - i - 1);
                    }
                }
                List<Long> addresses = List.of(address | oneMask);
                for (Integer i : floatingBits) {
                    addresses = addresses.stream()
                            .flatMap(a -> Stream.of(
                                    a | 1L << i,
                                    a & Long.MAX_VALUE - (1L << i)
                            ))
                            .collect(toList());
                }
                addresses.forEach(a -> memory.put(a, value));
            }
        }
        return memory.values().stream().mapToLong(Long::longValue).sum() + "";
    }

}
