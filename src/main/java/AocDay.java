import java.util.ArrayList;
import java.util.List;

public abstract class AocDay<INPUT> {

    abstract INPUT prepareInput() throws Exception;

    abstract String part1(INPUT input) throws Exception;

    abstract String part2(INPUT input) throws Exception;

    public void solve() {
        solve(false);
    }

    public void solve(boolean preRun) {
        try {
            if (preRun) {
                INPUT input = prepareInput();
                part1(input);
                part2(input);
            }
            Timer timer = new Timer();
            System.out.println("SOLUTIONS:");
            timer.segment("Prepare");
            INPUT input = prepareInput();
            timer.segment("Part 1");
            System.out.println("Part 1: " + part1(input));
            timer.segment("Part 2");
            System.out.println("Part 2: " + part2(input));
            timer.stop();

            System.out.println();
            System.out.println("STATS:");
            timer.segments.forEach(segment -> System.out.println(segment.name + ": " + (segment.duration() / 1_000_000.) + "ms"));
            System.out.println("Total time: " + (timer.totalTime() / 1_000_000.) + "ms");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static class Timer {

        long startTime;
        long endTime;
        private final List<Segment> segments = new ArrayList<>();
        private OngoingSegment currentSegment;

        void segment(String segment) {
            long currentTime = System.nanoTime();
            if (startTime == 0) {
                startTime = currentTime;
            } else {
                segments.add(currentSegment.complete(currentTime));
            }
            currentSegment = new OngoingSegment(segment, currentTime);
        }

        void stop() {
            long currentTime = System.nanoTime();
            this.endTime = currentTime;
            segments.add(currentSegment.complete(currentTime));
        }

        public List<Segment> getSegments() {
            return segments;
        }

        public long totalTime() {
            return endTime - startTime;
        }

    }

    private static record OngoingSegment(String name, long startTime) {

        public Segment complete(long currentTime) {
            return new Segment(name, currentTime - startTime);
        }

    }

    private static record Segment(String name, long duration) {}

}
