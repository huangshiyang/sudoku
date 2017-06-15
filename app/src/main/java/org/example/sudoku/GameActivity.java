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
    private GameFragment mGameFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        // Restore game here...
        mGameFragment = (GameFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_game);
        boolean restore = getIntent().getBooleanExtra(KEY_RESTORE, false);
        if (restore) {
            String gameData = getPreferences(MODE_PRIVATE)
                    .getString(PREF_RESTORE, null);
            if (gameData != null) {
                mGameFragment.putState(gameData);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        String gameData = mGameFragment.getState();
        getPreferences(MODE_PRIVATE).edit()
                .putString(PREF_RESTORE, gameData)
                .commit();
        Log.d("Pseudoku", "state = " + gameData);
    }

    public void selectedTile(View view) {
        ViewParent viewParent = view.getParent().getParent().getParent();
        String viewId = view.getResources().getResourceName(view.getId());
        String viewParentId = view.getResources().getResourceName(((TableRow) viewParent).getId());
        Log.d("GameActivity", "Yo!");
        GameFragment fragment = (GameFragment) getFragmentManager()
                .findFragmentById(R.id.fragment_game);
        int x = viewId.charAt(26) - '1';
        int y = viewParentId.charAt(26) - 'A';
        fragment.showKeypadOrError(x, y);
    }
}
