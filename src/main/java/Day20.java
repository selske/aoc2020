import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.*;

public class Day20 extends AocDay<List<Day20.Tile>> {

    public static void main(String[] args) {
        new Day20().solve(false);
    }

    private static final Pattern TILE_ID_PATTERN = Pattern.compile("Tile (\\d+):");

    @Override
    List<Tile> prepareInput() throws Exception {
        final List<String> lines = Files.readAllLines(Path.of(Day20.class.getResource("/day20").getPath()));
        List<Tile> tiles = new ArrayList<>();

        int tileId = 0;
        StringBuilder imageBuilder = new StringBuilder();
        for (String line : lines) {
            final Matcher tileIdMatcher = TILE_ID_PATTERN.matcher(line);
            if (tileIdMatcher.matches()) {
                tileId = parseInt(tileIdMatcher.group(1));
            } else if (line.isBlank()) {
                tiles.add(getTile(tileId, imageBuilder));
                imageBuilder.setLength(0);
            } else {
                imageBuilder.append(line).append("\n");
            }
        }
        tiles.add(getTile(tileId, imageBuilder));
        return tiles;
    }

    private Tile getTile(int tileId, StringBuilder imageBuilder) {
        final String[] rows = imageBuilder.toString().split("\n");
        String topEdge = rows[0];
        String leftEdge = Arrays.stream(rows).map(row -> row.charAt(0) + "").collect(joining());
        String bottomEdge = rows[rows.length - 1];
        String rightEdge = Arrays.stream(rows).map(row -> row.charAt(row.length() - 1) + "").collect(joining());
        return new Tile(tileId, topEdge, rightEdge, bottomEdge, leftEdge, imageBuilder.toString());
    }

    @Override
    String part1(List<Tile> tiles) {
        Map<Integer, Set<Tile>> permutations = tiles.stream()
                .collect(toMap(t -> t.id, t -> {
                    final Set<Tile> possibilities = new HashSet<>();

                    Tile rotated = t.rotate();
                    for (int i = 0; i < 4; i++) {
                        possibilities.add(rotated);
                        possibilities.add(rotated.flipHorizontal());
                        possibilities.add(rotated.flipVertical());
                        rotated = rotated.rotate();
                    }

                    return possibilities;
                }));

        Set<NeighbourMapping> neighbourMappings = new HashSet<>();
        for (Tile tile : permutations.values().stream().flatMap(Collection::stream).collect(toSet())) {
            Set<Tile> topCandidates = findPotentialNeighbours(permutations, tile, t -> t.bottomEdge.equals(tile.topEdge));
            Set<Tile> rightCandidates = findPotentialNeighbours(permutations, tile, t -> t.leftEdge.equals(tile.rightEdge));
            Set<Tile> bottomCandidates = findPotentialNeighbours(permutations, tile, t -> t.topEdge.equals(tile.bottomEdge));
            Set<Tile> leftCandidates = findPotentialNeighbours(permutations, tile, t -> t.rightEdge.equals(tile.leftEdge));

            neighbourMappings.add(new NeighbourMapping(tile, topCandidates, rightCandidates, bottomCandidates, leftCandidates));
        }

        final Set<Integer> corners = neighbourMappings.stream()
                .filter(nm -> nm.topNeighbours.isEmpty())
                .filter(nm -> nm.leftNeighbours.isEmpty())
                .map(nm -> nm.tile.id)
                .collect(toSet());

        long product = 1;
        for (int cornerId : corners) {
            product *= cornerId;
        }
        return product + "";
    }

    private Set<Tile> findPotentialNeighbours(Map<Integer, Set<Tile>> permutations, Tile tile, Predicate<Tile> filter) {
        return permutations.entrySet().stream()
                .filter(e -> e.getKey() != tile.id)
                .map(Map.Entry::getValue)
                .flatMap(Collection::stream)
                .filter(filter)
                .collect(toSet());
    }

    @Override
    String part2(List<Tile> tiles) {
        Map<Integer, Set<Tile>> permutations = tiles.stream()
                .collect(toMap(t -> t.id, t -> {
                    final Set<Tile> possibilities = new HashSet<>();

                    Tile rotated = t.rotate();
                    for (int i = 0; i < 4; i++) {
                        possibilities.add(rotated);
                        possibilities.add(rotated.flipHorizontal());
                        possibilities.add(rotated.flipVertical());
                        rotated = rotated.rotate();
                    }

                    return possibilities;
                }));

        Set<NeighbourMapping> neighbourMappings = new HashSet<>();
        for (Tile tile : permutations.values().stream().flatMap(Collection::stream).collect(toSet())) {
            Set<Tile> topCandidates = findPotentialNeighbours(permutations, tile, t -> t.bottomEdge.equals(tile.topEdge));
            Set<Tile> rightCandidates = findPotentialNeighbours(permutations, tile, t -> t.leftEdge.equals(tile.rightEdge));
            Set<Tile> bottomCandidates = findPotentialNeighbours(permutations, tile, t -> t.topEdge.equals(tile.bottomEdge));
            Set<Tile> leftCandidates = findPotentialNeighbours(permutations, tile, t -> t.rightEdge.equals(tile.leftEdge));

            neighbourMappings.add(new NeighbourMapping(tile, topCandidates, rightCandidates, bottomCandidates, leftCandidates));
        }

        boolean[][] image = toImage(getTileArray(tiles, neighbourMappings));

        List<boolean[][]> images = new ArrayList<>();
        boolean[][] rotated = image;
        for (int i = 0; i < 4; i++) {
            images.add(rotated);
            images.add(flipHorizontal(rotated));
            images.add(flipVertical(rotated));
            rotated = rotate(rotated);
        }

        Mask mask = new Mask("""
                                  #
                #    ##    ##    ###
                 #  #  #  #  #  #  
                """);

        for (boolean[][] permutation : images) {
            boolean orientedCorrectly = false;
            for (int row = 0; row < permutation.length - mask.height(); row++) {
                for (int col = 0; col < permutation[row].length - mask.width(); col++) {
                    int finalRow = row;
                    int finalCol = col;
                    final boolean found = mask.maskPixels.stream()
                            .allMatch(pixel -> permutation[finalRow + pixel.rowOffset][finalCol + pixel.colOffset]);
                    if (found) {
                        orientedCorrectly = true;
                        mask.maskPixels.forEach(pixel -> permutation[finalRow + pixel.rowOffset][finalCol + pixel.colOffset] = false);
                    }
                }
            }
            if (orientedCorrectly) {
                int count = 0;
                for (boolean[] booleans : permutation) {
                    for (boolean aBoolean : booleans) {
                        if (aBoolean) {
                            count++;
                        }
                    }
                }

                return count + "";
            }
        }
        throw new IllegalArgumentException();
    }

    private boolean[][] toImage(Tile[][] tileArray) {
        int tileSize = tileArray[0][0].pixels.length;
        boolean[][] image = new boolean[tileSize * tileArray.length][tileSize * tileArray.length];
        for (int tileRow = 0; tileRow < tileArray.length; tileRow++) {
            for (int tileCol = 0; tileCol < tileArray[tileRow].length; tileCol++) {
                Tile tile = tileArray[tileRow][tileCol];
                for (int pixelRow = 0; pixelRow < tile.pixels.length; pixelRow++) {
                    if (image[tileRow * tileSize + pixelRow] == null) {
                        image[tileRow * tileSize + pixelRow] = new boolean[tileSize];
                    }
                    for (int pixelCol = 0; pixelCol < tile.pixels.length; pixelCol++) {
                        image[tileRow * tileSize + pixelRow][tileCol * tileSize + pixelCol] = tile.pixels[pixelRow][pixelCol];
                    }
                }
            }
        }
        return image;
    }

    private Tile[][] getTileArray(List<Tile> tiles, Set<NeighbourMapping> neighbourMappings) {
        final NeighbourMapping startingCorner = neighbourMappings.stream()
                .filter(nm -> nm.topNeighbours.isEmpty())
                .filter(nm -> nm.leftNeighbours.isEmpty())
                .findFirst().orElseThrow();

        final int arraySize = (int) Math.sqrt(tiles.size());
        Tile[][] tileArray = new Tile[arraySize][arraySize];
        for (int row = 0; row < arraySize; row++) {
            NeighbourMapping leftEdge;
            if (row == 0) {
                leftEdge = startingCorner;
            } else {
                final Tile aboveTile = tileArray[row - 1][0];
                NeighbourMapping above = neighbourMappings.stream()
                        .filter(nm -> nm.tile.equals(aboveTile))
                        .findFirst().orElseThrow();
                if (above.bottomNeighbours.size() > 1) {
                    throw new IllegalStateException();
                }
                leftEdge = neighbourMappings.stream()
                        .filter(nm -> nm.tile.equals(above.bottomNeighbours.iterator().next()))
                        .findFirst().orElseThrow();
            }
            NeighbourMapping current = leftEdge;
            tileArray[row][0] = current.tile;

            for (int col = 1; col < arraySize; col++) {
                if (leftEdge.rightNeighbours().size() > 1) {
                    throw new IllegalArgumentException();
                }
                final Tile currentTile = leftEdge.rightNeighbours().iterator().next();
                tileArray[row][col] = currentTile;
                leftEdge = neighbourMappings.stream().filter(nm -> nm.tile.equals(currentTile)).findFirst().orElseThrow();
            }
        }
        return tileArray;
    }

    private record NeighbourMapping(
            Tile tile,
            Set<Tile> topNeighbours,
            Set<Tile> rightNeighbours,
            Set<Tile> bottomNeighbours,
            Set<Tile> leftNeighbours
    ) {}

    static final record Tile(int id, String topEdge, String rightEdge, String bottomEdge, String leftEdge,
                             boolean[][] pixels) {

        Tile(int id, String topEdge, String rightEdge, String bottomEdge, String leftEdge, String content) {
            this(id, topEdge, rightEdge, bottomEdge, leftEdge, toPixels(content));
        }

        private static boolean[][] toPixels(String content) {
            final List<String> lines = content.lines().collect(toList());
            boolean[][] pixels = new boolean[lines.size() - 2][];
            for (int i = 0; i < lines.size() - 2; i++) {
                String line = lines.get(i + 1);
                pixels[i] = new boolean[line.length() - 2];
                for (int j = 0; j < line.length() - 2; j++) {
                    if (line.charAt(j + 1) == '#') {
                        pixels[i][j] = true;
                    }
                }
            }
            return pixels;
        }

        public Tile rotate() {
            return new Tile(id, rightEdge, reverse(bottomEdge), leftEdge, reverse(topEdge), Day20.rotate(pixels));
        }

        public Tile flipVertical() {
            return new Tile(id, bottomEdge, reverse(rightEdge), topEdge, reverse(leftEdge), Day20.flipVertical(this.pixels));
        }

        public Tile flipHorizontal() {
            return new Tile(id, reverse(topEdge), leftEdge, reverse(bottomEdge), rightEdge, Day20.flipHorizontal(this.pixels));
        }

        private String reverse(String input) {
            return new StringBuilder(input).reverse().toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Tile tile = (Tile) o;
            return id == tile.id && topEdge.equals(tile.topEdge) && rightEdge.equals(tile.rightEdge) && bottomEdge.equals(tile.bottomEdge) && leftEdge.equals(tile.leftEdge) && Arrays.deepEquals(pixels, tile.pixels);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(id, topEdge, rightEdge, bottomEdge, leftEdge);
            result = 31 * result + Arrays.deepHashCode(pixels);
            return result;
        }

    }

    private static boolean[][] flipVertical(boolean[][] input) {
        final int size = input.length;
        boolean[][] flippedPixels = new boolean[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                flippedPixels[size - row - 1][col] = input[row][col];
            }
        }
        return flippedPixels;
    }

    private static boolean[][] flipHorizontal(boolean[][] input) {
        final int size = input.length;
        boolean[][] flippedPixels = new boolean[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                flippedPixels[row][size - col - 1] = input[row][col];
            }
        }
        return flippedPixels;
    }

    private static boolean[][] rotate(boolean[][] input) {
        final int size = input.length;
        boolean[][] rotatedPixels = new boolean[size][size];
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                rotatedPixels[size - col - 1][row] = input[row][col];
            }
        }
        return rotatedPixels;
    }

    private static final record Mask(List<MaskPixel> maskPixels) {

        private Mask(String mask) {
            this(parse(mask));
        }

        public int width() {
            return maskPixels.stream().mapToInt(MaskPixel::colOffset).max().orElseThrow();
        }

        public int height() {
            return maskPixels.stream().mapToInt(MaskPixel::rowOffset).max().orElseThrow();
        }

        private static List<MaskPixel> parse(String mask) {
            List<MaskPixel> maskPixels = new ArrayList<>();
            final String[] lines = mask.split("\n");
            for (int row = 0; row < lines.length; row++) {
                String line = lines[row];
                for (int col = 0; col < line.length(); col++) {
                    if (line.charAt(col) == '#') {
                        maskPixels.add(new MaskPixel(row, col));
                    }
                }
            }
            return maskPixels;
        }

        private record MaskPixel(int rowOffset, int colOffset) {}

    }

}
