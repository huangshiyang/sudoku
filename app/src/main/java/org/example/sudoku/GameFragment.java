package org.example.sudoku;

import android.app.Dialog;
import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import static android.content.Context.MODE_PRIVATE;

public class GameFragment extends Fragment {
    private static final String TAG = "Pseudoku";

    public static final String KEY_DIFFICULTY =
            "org.example.sudoku.difficulty";
    private static final String PREF_PUZZLE = "puzzle";
    public static final int DIFFICULTY_EASY = 0;
    public static final int DIFFICULTY_MEDIUM = 1;
    public static final int DIFFICULTY_HARD = 2;
    protected static final int DIFFICULTY_CONTINUE = -1;

    private int puzzle[];

    private final int[] rowId = {R.id.tileA, R.id.tileB, R.id.tileC, R.id.tileD, R.id.tileE, R.id.tileF, R.id.tileG, R.id.tileH, R.id.tileI};
    private final int[] columnId = {R.id.tile1, R.id.tile2, R.id.tile3, R.id.tile4, R.id.tile5, R.id.tile6, R.id.tile7, R.id.tile8, R.id.tile9};
    private final int[] numbers = {R.drawable.num1, R.drawable.num2, R.drawable.num3, R.drawable.num4, R.drawable.num5, R.drawable.num6, R.drawable.num7, R.drawable.num8, R.drawable.num9};

    private final String easyPuzzle =
            "360000000004230800000004200" +
                    "070460003820000014500013020" +
                    "001900000007048300000000045";
    private final String mediumPuzzle =
            "650000070000506000014000005" +
                    "007009000002314700000700800" +
                    "500000630000201000030000097";
    private final String hardPuzzle =
            "009000000080605020501078000" +
                    "000000700706040102004000000" +
                    "000720903090301080000000600";

    private PuzzleView puzzleView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.board, container, false);
        initViews(rootView);
        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");

        int diff = getActivity().getIntent().getIntExtra(KEY_DIFFICULTY,
                DIFFICULTY_EASY);
        puzzle = getPuzzle(diff);
        calculateUsedTiles();

        puzzleView = new PuzzleView(this);
//        puzzleView = (PuzzleView) getActivity().findViewById(R.id.puzzle_view);
        getActivity().setContentView(puzzleView);
        puzzleView.requestFocus();

        // ...
        // If the activity is restarted, do a continue next time
        getActivity().getIntent().putExtra(KEY_DIFFICULTY, DIFFICULTY_CONTINUE);
    }

    private void initViews(View rootView) {
        for (int row = 0; row < 9; row++) {
            View rowView = rootView.findViewById(rowId[row]);
            for (int col = 0; col < 9; col++) {
                final ImageButton button = (ImageButton) rowView.findViewById(columnId[col]);
                int num = getTile(row, col) - 1;
                if (num > 0) {
                    button.setImageResource(numbers[num]);
                }
            }
        }
    }

    private void makeMove(int large, int small) {
    }

    public void restartGame() {
        initGame();
        initViews(getView());
    }

    public void initGame() {
        Log.d("Pseudoku", "init game");
    }

    /**
     * Create a string containing the state of the game.
     */
    public String getState() {
        StringBuilder builder = new StringBuilder();
        builder.append("In some state or other.");
        return builder.toString();
    }

    /**
     * Restore the state of the game from the given string.
     */
    public void putState(String gameData) {
    }

    /**
     * Given a difficulty level, come up with a new puzzle
     */
    private int[] getPuzzle(int diff) {
        String puz;
        switch (diff) {
            case DIFFICULTY_CONTINUE:
                puz = getActivity().getPreferences(MODE_PRIVATE).getString(PREF_PUZZLE, easyPuzzle);
                break;
            // ...
            case DIFFICULTY_HARD:
                puz = hardPuzzle;
                break;
            case DIFFICULTY_MEDIUM:
                puz = mediumPuzzle;
                break;
            case DIFFICULTY_EASY:
            default:
                puz = easyPuzzle;
                break;
        }

        return fromPuzzleString(puz);
    }

    /**
     * Convert an array into a puzzle string
     */
    static private String toPuzzleString(int[] puz) {
        StringBuilder buf = new StringBuilder();
        for (int element : puz) {
            buf.append(element);
        }
        return buf.toString();
    }

    /**
     * Convert a puzzle string into an array
     */
    static protected int[] fromPuzzleString(String string) {
        int[] puz = new int[string.length()];
        for (int i = 0; i < puz.length; i++) {
            puz[i] = string.charAt(i) - '0';
        }
        return puz;
    }

    /**
     * Return the tile at the given coordinates
     */
    private int getTile(int x, int y) {
        return puzzle[y * 9 + x];
    }

    /**
     * Change the tile at the given coordinates
     */
    private void setTile(int x, int y, int value) {
        puzzle[y * 9 + x] = value;
    }

    /**
     * Return a string for the tile at the given coordinates
     */
    protected String getTileString(int x, int y) {
        int v = getTile(x, y);
        if (v == 0)
            return "";
        else
            return String.valueOf(v);
    }

    /**
     * Change the tile only if it's a valid move
     */
    protected boolean setTileIfValid(int x, int y, int value) {
        int tiles[] = getUsedTiles(x, y);
        if (value != 0) {
            for (int tile : tiles) {
                if (tile == value)
                    return false;
            }
        }
        setTile(x, y, value);
        calculateUsedTiles();
        return true;
    }

    /**
     * Open the keypad if there are any valid moves
     */
    protected void showKeypadOrError(int x, int y) {
        int tiles[] = getUsedTiles(x, y);
        if (tiles.length == 9) {
            Toast toast = Toast.makeText(getContext(),
                    R.string.no_moves_label, Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            Dialog v = new Keypad(getContext(), tiles, puzzleView);
            v.show();
        }
    }

    /**
     * Cache of used tiles
     */
    private final int used[][][] = new int[9][9][];

    /**
     * Return cached used tiles visible from the given coords
     */
    protected int[] getUsedTiles(int x, int y) {
        return used[x][y];
    }

    /**
     * Compute the two dimensional array of used tiles
     */
    private void calculateUsedTiles() {
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                used[x][y] = calculateUsedTiles(x, y);
                // Log.d(TAG, "used[" + x + "][" + y + "] = "
                // + toPuzzleString(used[x][y]));
            }
        }
    }

    /**
     * Compute the used tiles visible from this position
     */
    private int[] calculateUsedTiles(int x, int y) {
        int c[] = new int[9];
        // horizontal
        for (int i = 0; i < 9; i++) {
            if (i == x)
                continue;
            int t = getTile(i, y);
            if (t != 0)
                c[t - 1] = t;
        }
        // vertical
        for (int i = 0; i < 9; i++) {
            if (i == y)
                continue;
            int t = getTile(x, i);
            if (t != 0)
                c[t - 1] = t;
        }
        // same cell block
        int startx = (x / 3) * 3;
        int starty = (y / 3) * 3;
        for (int i = startx; i < startx + 3; i++) {
            for (int j = starty; j < starty + 3; j++) {
                if (i == x && j == y)
                    continue;
                int t = getTile(i, j);
                if (t != 0)
                    c[t - 1] = t;
            }
        }
        // compress
        int nused = 0;
        for (int t : c) {
            if (t != 0)
                nused++;
        }
        int c1[] = new int[nused];
        nused = 0;
        for (int t : c) {
            if (t != 0)
                c1[nused++] = t;
        }
        return c1;
    }
}
