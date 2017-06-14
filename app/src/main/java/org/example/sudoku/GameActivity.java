package org.example.sudoku;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class GameActivity extends Activity {
    private GameFragment mGameFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        // Restore game here...
        mGameFragment = (GameFragment) getFragmentManager()
                .findFragmentById(R.id.game_fragment);
    }
}
