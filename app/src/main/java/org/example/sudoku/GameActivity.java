package org.example.sudoku;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewParent;
import android.widget.TableRow;

public class GameActivity extends Activity {
    public static final String KEY_RESTORE = "key_restore";
    public static final String PREF_RESTORE = "pref_restore";
    public static final String INIT_RESTORE = "init_restore";
    private GameFragment mGameFragment;
    private String gameData = null;
    private int init = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        // Restore game here...
        mGameFragment = (GameFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_game);
        int restore = getIntent().getIntExtra(KEY_RESTORE, 0);
        if (restore == -1) {
            gameData = getPreferences(MODE_PRIVATE)
                    .getString(PREF_RESTORE, null);
            init = getPreferences(MODE_PRIVATE)
                    .getInt(INIT_RESTORE, -1);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameData = mGameFragment.getState();
        init = mGameFragment.getInit();
        getPreferences(MODE_PRIVATE).edit()
                .putString(PREF_RESTORE, gameData)
                .commit();
        getPreferences(MODE_PRIVATE).edit()
                .putInt(INIT_RESTORE, init)
                .commit();
        Log.d("Pseudoku", "state = " + gameData);
    }

    public void selectedTile(View view) {
        ViewParent rowView = view.getParent().getParent().getParent();
        String viewId = view.getResources().getResourceName(view.getId());
        String viewParentId = view.getResources().getResourceName(((TableRow) rowView).getId());
        Log.d("GameActivity", "Yo!");
        GameFragment fragment = (GameFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_game);
        int x = viewId.charAt(26) - '1';
        int y = viewParentId.charAt(26) - 'A';
        fragment.showKeypadOrError(x, y);
    }

    public String getContinue() {
        return getPreferences(MODE_PRIVATE)
                .getString(PREF_RESTORE, null);
    }

    public int getInit() {
        return getPreferences(MODE_PRIVATE)
                .getInt(INIT_RESTORE, -1);
    }

}