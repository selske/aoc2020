public abstract class AocDay<INPUT> {

    abstract INPUT prepareInput() throws Exception;

    abstract String part1(INPUT input) throws Exception;

    abstract String part2(INPUT input) throws Exception;

    public void solve() {
        solve(false, true);
    }

    public void solve(boolean preRun) {
        solve(preRun, true);
    }

    public void solve(boolean preRun, boolean printSummary) {
        try {
            if (preRun) {
                INPUT input = prepareInput();
                part1(input);
                part2(input);
            }
            long beforePrepare = System.nanoTime();
            INPUT input = prepareInput();
            long afterPrepare = System.nanoTime();
            long beforePart1 = System.nanoTime();
            String part1Solution = part1(input);
            long afterPart1 = System.nanoTime();
            long beforePart2 = System.nanoTime();
            String part2Solution = part2(input);
            long afterPart2 = System.nanoTime();

            if (printSummary) {
                System.out.println("SOLUTIONS:");
                System.out.println("Part1: " + part1Solution);
                System.out.println("Part2: " + part2Solution);
                System.out.println();
                System.out.println("STATS:");
                System.out.println("Prepare: " + ((afterPrepare - beforePrepare) / 1_000_000.) + "ms");
                System.out.println("Part1: " + ((afterPart1 - beforePart1) / 1_000_000.) + "ms");
                System.out.println("Part2: " + ((afterPart2 - beforePart2) / 1_000_000.) + "ms");
                System.out.println("Total time: " + ((afterPart2 - beforePrepare) / 1_000_000.) + "ms");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
