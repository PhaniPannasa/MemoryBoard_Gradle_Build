package com.example.memorygame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

public class MainActivity  extends Activity {

    ImageAdapter image;

    public static GameData gameData;
    public final String TAG = "MainActivity";
    public int RelaytiveHieght = 0;
    private int screenHeight = 0;

    private boolean layoutMeasured = false;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameData = new GameData(this);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenHeight = metrics.heightPixels;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "height::"+screenHeight);
        final View sampleView = findViewById(R.id.relaytive);

        sampleView.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                RelaytiveHieght = sampleView.getHeight();
                if(!layoutMeasured)
                {
                    layoutMeasured = true;
                    Log.d(TAG,"RelaytiveHieght::"+RelaytiveHieght);
                    setAdapter();
                }
            }
        });




        final Button button = (Button) findViewById(R.id.button1);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Perform action on click
                startActivity(new Intent(getApplicationContext(),
                        HighScore.class));

            }
        });
    }

    private void setAdapter()
    {
        int gridHeight = screenHeight - RelaytiveHieght;
        GridView gridview = (GridView) findViewById(R.id.gridview);
        image = new ImageAdapter(this,gridHeight);
        image.createBoard();

        gridview.setAdapter(image);
        image.setScoreID((TextView) findViewById(R.id.scrore));

        gridview.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                image.flipCard(position);
            }
        });

        gridview.setVerticalScrollBarEnabled(false);

        gridview.setOnTouchListener(new OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_MOVE){
                    return true;
                }
                return false;
            }

        });

    }

    // Call Back method to get the Message form other Activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("Phani welcomeeee onActivityResult" + resultCode);

        // check if the request code is same as what is passed here it is 2
        if (resultCode == 5) {
            String name = data.getStringExtra("playerName");
            String rank = data.getStringExtra("playerRank");
            String score = data.getStringExtra("playerScore");
            System.out.println("Phani restart the game name =" + name
                    + "rank = " + rank + "score = " + score);
            new AlertDialog.Builder(this)
                    .setTitle("You Win")
                    .setMessage(
                            "congratulations " + name + " Your Rank is "
                                    + rank + " &  Score is " + score)
                    .setPositiveButton(android.R.string.yes,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // continue with delete
                                }
                            }).setIcon(android.R.drawable.ic_dialog_info)
                    .show();
            image.restartGame();
        }
    }
}
