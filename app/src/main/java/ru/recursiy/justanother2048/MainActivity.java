package ru.recursiy.justanother2048;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements GameStateObserver {

    private GestureDetector gestureDetector;
    private Game2048 game = new Game2048();
    private final String GAME_KEY = "Game2048_Parcelable";
    GameAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            game.init(this);
            initAdapter();
            initGesture();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        outState.putParcelable(GAME_KEY, game);
    }

    @Override
    protected void onRestoreInstanceState(Bundle inState)
    {
        super.onRestoreInstanceState(inState);
        game = inState.getParcelable(GAME_KEY);
        if (game == null)
            throw new UnknownError("Failed restore state");

        game.initObserver(this);
        initAdapter();
        initGesture();
        adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem item)
    {
        if(item.getItemId() == R.id.restart)
        {
            restart();
            return true;
        }
        return false;
    }

    public void onScoresChanged(int scores)
    {
        ((TextView)findViewById(R.id.scores_value)).setText(((Integer)scores).toString());
    }
    public void onMove()
    {
        adapter.notifyDataSetChanged();
    }
    public void onStatusChanged(Status newStatus)
    {
        if(newStatus == Status.FINISHED)
        {
            Toast.makeText(this, R.string.game_over_message, Toast.LENGTH_LONG).show();
        }
    }

    private void restart()
    {
        game.init(this);
        initAdapter();
    }

    private void initAdapter()
    {
        adapter = new GameAdapter(this, game.getField());

        GridView field = (GridView) findViewById(R.id.field);
        field.setNumColumns(game.getFieldSize());
        field.setAdapter(adapter);
    }

    private void initGesture()
    {
        gestureDetector = initGestureDetector();

        View.OnTouchListener listener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };

        findViewById(R.id.main_screen).setOnTouchListener(listener);
        findViewById(R.id.field).setOnTouchListener(listener);
        findViewById(R.id.central_frame).setOnTouchListener(listener);
    }

    private GestureDetector initGestureDetector() {
        return new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {

            private SwipeDetector detector = new SwipeDetector();

            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                                   float velocityY) {
                if (detector.isSwipeDown(e1, e2, velocityY)) {
                    game.slideDown();
                } else if (detector.isSwipeUp(e1, e2, velocityY)) {
                    game.slideUp();
                }else if (detector.isSwipeLeft(e1, e2, velocityX)) {
                    game.slideLeft();
                } else if (detector.isSwipeRight(e1, e2, velocityX)) {
                    game.slideRight();
                }
                return false;
            }
        });
    }

}

