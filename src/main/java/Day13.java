import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.parseInt;

public class Day13 extends AocDay<List<String>> {

    public static void main(String[] args) {
        new Day13().solve(true);
    }

    @Override
    List<String> prepareInput() throws Exception {
        return Files.readAllLines(Path.of(Day13.class.getResource("/day13").getPath()));
    }

    @Override
    String part1(List<String> input) {
        int startTime = parseInt(input.get(0));
        final int[] buses = Arrays.stream(input.get(1).split(","))
                .filter(bus -> !bus.equals("x"))
                .mapToInt(Integer::parseInt)
                .toArray();

        int bestBus = -1;
        int bestBusWaitTime = Integer.MAX_VALUE;
        for (int bus : buses) {
            int timestamp = 0;
            while (timestamp < startTime) {
                timestamp += bus;
            }
            int waitTime = timestamp - startTime;
            if (waitTime < bestBusWaitTime) {
                bestBusWaitTime = waitTime;
                bestBus = bus;
            }
        }

        return bestBus * bestBusWaitTime + "";
    }

    @Override
    String part2(List<String> input) {
        final String[] buses = input.get(1).split(",");
        List<Input> inputs = new ArrayList<>();
        for (int i = 0; i < buses.length; i++) {
            if (!buses[i].equals("x")) {
                final int bus = Integer.parseInt(buses[i]);
                inputs.add(new Input(bus - i, bus));
            }
        }

        long nProduct = 1;
        for (Input delay : inputs) {
            nProduct *= delay.ni();
        }
        long N = nProduct;
        return inputs.stream()
                       .mapToLong(in -> in.biNixi(N))
                       .sum() % N + "";
    }

    private record Input(int bi, int ni) {

        public long biNixi(long N) {
            long Ni = N / ni;
            long xi = 0;
            for (long x = ni; x < Integer.MAX_VALUE; x++) {
                if ((Ni % ni * x) % ni == 1) {
                    xi = x;
                    break;
                }
            }
            return bi * Ni * xi;
        }

        @Override
        public String toString() {
            return "x \u2261 " + bi + " (mod " + ni + ")";
        }

    }

}
