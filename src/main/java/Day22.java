
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static java.lang.Integer.parseInt;
import static java.util.stream.Collectors.toList;

public class Day22 extends AocDay<Day22.Input> {

    public static void main(String[] args) {
        new Day22().solve(true);
    }

    @Override
    Input prepareInput() throws Exception {
        final List<String> lines = Files.readAllLines(Path.of(Day22.class.getResource("/day22").getPath()));
        List<List<Integer>> cards = List.of(new ArrayList<>(), new ArrayList<>());
        int player = 0;
        for (String line : lines) {
            if (line.startsWith("Player")) {
                continue;
            } else if (line.isBlank()) {
                player = 1;
            } else {
                cards.get(player).add(parseInt(line));
            }
        }
        return new Input(cards.get(0), cards.get(1));
    }

    @Override
    String part1(Input input) {
        Game game = input.createGame();
        game.play();
        return game.winningScore() + "";
    }

    @Override
    String part2(Input input) {
        Game game = input.createGame();
        game.playRecursive();
        return game.winningScore() + "";
    }

    static record Input(List<Integer> player1, List<Integer> player2) {

        public Game createGame() {
            return new Game(player1, player2);
        }

    }

    static class Game {

        private int winner = -1;
        private final Deque<Integer> player1;
        private final Deque<Integer> player2;

        Game(Collection<Integer> player1, Collection<Integer> player2) {
            this.player1 = new LinkedList<>(player1);
            this.player2 = new LinkedList<>(player2);
        }

        public void play() {
            while (!player1.isEmpty() && !player2.isEmpty()) {
                playRound();
            }
            if (player1.isEmpty()) {
                winner = 1;
            } else {
                winner = 0;
            }
        }

        private void playRound() {
            final Integer card1 = player1.pop();
            final Integer card2 = player2.pop();
            if (card1 > card2) {
                player1.addLast(card1);
                player1.addLast(card2);
            } else if (card2 > card1) {
                player2.addLast(card2);
                player2.addLast(card1);
            } else {
                player1.addLast(card1);
                player2.addLast(card2);
            }
        }

        public void playRecursive() {
            Set<State> previousStates = new HashSet<>();
            while (!player1.isEmpty() && !player2.isEmpty()) {
                if (!previousStates.add(new State(player1, player2))) {
                    winner = 0;
                    return;
                }
                final int player1Card = player1.peek();
                final int player2Card = player2.peek();
                if (player1Card < player1.size() && player2Card < player2.size()) {
                    final Game recursiveGame = new Game(
                            player1.stream().skip(1).limit(player1Card).collect(toList()),
                            player2.stream().skip(1).limit(player2Card).collect(toList())
                    );
                    recursiveGame.playRecursive();
                    if (recursiveGame.winner == 0) {
                        player1.addLast(player1.pop());
                        player1.addLast(player2.pop());
                    } else {
                        player2.addLast(player2.pop());
                        player2.addLast(player1.pop());
                    }
                } else {
                    playRound();
                    if (player1.isEmpty()) {
                        winner = 1;
                        return;
                    } else if (player2.isEmpty()) {
                        winner = 0;
                        return;
                    }
                }
            }
        }

        public int winningScore() {
            List<Integer> winningCards;
            if (winner == 0) {
                winningCards = new ArrayList<>(player1);
            } else {
                winningCards = new ArrayList<>(player2);
            }
            int score = 0;
            for (int i = 0; i < winningCards.size(); i++) {
                score += winningCards.get(i) * (winningCards.size() - i);
            }
            return score;
        }

        @Override
        public String toString() {
            return "Game{" +
                   "player1=" + player1 +
                   ", player2=" + player2 +
                   '}';
        }

    }

    private static record State(List<Integer> player1, List<Integer> player2) {

        private State(Collection<Integer> player1, Collection<Integer> player2) {
            this(new ArrayList<>(player1), new ArrayList<>(player2));
        }

    }

}
