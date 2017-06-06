package com.example.memorygame;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.HashMap;

@SuppressLint("NewApi")
public class HighScore extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_high_score);
        setContentView(R.layout.highscore);


     // reference the table layout
        TableLayout tbl = (TableLayout)findViewById(R.id.tableLayout);
        // delcare a new row
        TableRow mainRow = new TableRow(this);
        // add views to the row
        TextView username= new TextView(this);
        username.setText("NAME");
        username.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        username.setPadding(15, 15, 15, 15);
        username.setAllCaps(true);
        username.setGravity(Gravity.CENTER);

        username.setTypeface(null, Typeface.BOLD);
        mainRow.addView(username); // you would actually want to set properties on this before adding it

        TextView score= new TextView(this);
        score.setText("SCORE");
        score.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        score.setPadding(15, 15, 15, 15);
        score.setAllCaps(true);
        score.setTypeface(null, Typeface.BOLD);
        mainRow.addView(score);

        TextView rank= new TextView(this);
        rank.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
        rank.setPadding(15, 15, 15, 15);
        rank.setAllCaps(true);
        rank.setTypeface(null, Typeface.BOLD);
        rank.setText("RANK");
        mainRow.addView(rank);
        System.out.println("Phani tbl"+tbl);
        // add the row to the table layout
        tbl.addView(mainRow);

        String[] allColumns = { MySQLiteHelper.COLUMN_USERNAME,
                MySQLiteHelper.COLUMN_SCORE,MySQLiteHelper.COLUMN_RANK };


        MainActivity.gameData.open();
        Cursor c = MainActivity.gameData.readSavedData();
        int i=1;
        if (c.moveToFirst())
        {
            do
            {
                TableRow tempRow = new TableRow(this);
                String name=null;
                for (int j = 0; j < allColumns.length; j++) {
                    TextView temp= new TextView(this);
                    if(allColumns[j].equals(MySQLiteHelper.COLUMN_USERNAME))
                        name = c.getString(c.getColumnIndex(allColumns[j]));
                    if(allColumns[j].equals(MySQLiteHelper.COLUMN_RANK))
                    {
                        HashMap<String , Integer> map =  MainActivity.gameData.getAllRanks();
                        temp.setText(map.get(name)+"");
                    }
                    else
                    temp.setText(c.getString(c.getColumnIndex(allColumns[j])));
                    temp.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
                    temp.setGravity(Gravity.CENTER);

                    temp.setPadding(15, 15, 15, 15);
                    tempRow.addView(temp);
                }
                tbl.addView(tempRow);

                i++;
            } while (c.moveToNext());
        }
        MainActivity.gameData.close();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }


}
