import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class Day5 extends AocDay<List<Day5.Ticket>> {

    public static void main(String[] args) {
        new Day5().solve(true);
    }

    @Override
    List<Ticket> prepareInput() throws Exception {
        return Files.lines(Path.of(Day5.class.getResource("/day5").getPath()))
                .map(Ticket::parse)
                .collect(toList());
    }

    @Override
    String part1(List<Ticket> passports) {
        return passports.stream()
                       .mapToInt(Ticket::seatId)
                       .max().orElseThrow() + "";
    }

    @Override
    String part2(List<Ticket> passports) {
        int[] seatIds = passports.stream()
                .mapToInt(Ticket::seatId)
                .sorted()
                .toArray();

        int previousId = seatIds[0] - 1;
        for (int i : seatIds) {
            if (previousId != i - 1) {
                return i - 1 + "";
            } else {
                previousId = i;
            }
        }
        throw new IllegalStateException();
    }

    record Ticket(int row, int column) {

        public static Ticket parse(String value) {
            char[] values = value.toCharArray();
            return new Ticket(getRow(values), getCol(values));
        }

        private static int getRow(char[] values) {
            int minRow = 0;
            int maxRow = 127;
            for (int i = 0; i < 7; i++) {
                if (values[i] == 'F') {
                    maxRow = maxRow - (maxRow + 1 - minRow) / 2;
                } else {
                    minRow += (maxRow + 1 - minRow) / 2;
                }
            }
            return maxRow;
        }

        private static int getCol(char[] values) {
            int minCol = 0;
            int maxCol = 7;
            for (int i = 7; i < values.length; i++) {
                if (values[i] == 'L') {
                    maxCol = maxCol - (maxCol + 1 - minCol) / 2;
                } else {
                    minCol += (maxCol + 1 - minCol) / 2;
                }
            }
            return maxCol;
        }

        public int seatId() {
            return row * 8 + column;
        }

    }

}
