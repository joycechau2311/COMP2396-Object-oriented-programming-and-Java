import java.util.*;

public class ComputerPlayer {
    private final Random rand = new Random();

    public int[] getMove(char[][] board) {
        List<int[]> empty = new ArrayList<>();
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 3; j++)
                if (board[i][j] == 0)
                    empty.add(new int[]{i, j});
        if (empty.isEmpty())
        	return null;
        return empty.get(rand.nextInt(empty.size()));
    }
}