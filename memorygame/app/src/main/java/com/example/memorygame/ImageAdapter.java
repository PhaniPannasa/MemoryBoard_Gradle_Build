package com.example.memorygame;

import android.app.Activity;
import android.bluetooth.BluetoothClass.Device;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Matrix;
//import android.graphics.mipmap.Drawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private int score =0;
    private int flipCount = 0;
    int gridHieght = 0;
    TextView textViewScrore = (TextView) findViewById(R.id.scrore);
    ArrayList<Integer> selectedCards = new ArrayList<Integer>();

    ArrayList<Integer> selectedPositions = new ArrayList<Integer>();

    final Handler mHandler = new Handler();

    Boolean nextRound = true;

    final Runnable sleepDevice = new Runnable() {
        public void run() {
            System.out.println("Phani in flipCount " + flipCount);
            if (selectedCards.get(0) != selectedCards.get(1)) {
                System.out.println("Phani Unflip them" + mThumbIds.toString());
                System.out.println("Phani Unflip selectedPositions.get(0)"
                        + selectedPositions.get(0));
                System.out.println("Phani Unflip selectedPositions.get(1)"
                        + selectedPositions.get(1));
                score = score-1; //wrong selection
                // Unflip them
                mThumbIds.set(selectedPositions.get(0), R.mipmap.card_bg);
                mThumbIds.set(selectedPositions.get(1), R.mipmap.card_bg);
                System.out.println("Phani Unflip them" + mThumbIds.toString());
                // reset flags
                selectedCards.clear();
                selectedPositions.clear();
                flipCount = 0;
            } else {
                // Equal then remove the cards
                System.out.println("Equal then remove the cards");
                score = score+2; //Correct selection
                mThumbIds.set(selectedPositions.get(0), R.mipmap.whitebg);
                mThumbIds.set(selectedPositions.get(1), R.mipmap.whitebg);
                flipCount = 0;

            }
            textViewScrore.setText(score+"");
            notifyDataSetChanged();
         if(checkGameOver())
            {
                Intent myIntent = new Intent(mContext,GameOver.class);
                myIntent.putExtra("score", score); //sending user score data
                ((Activity)mContext).startActivityForResult(myIntent,5);
            }
            nextRound = true;
        }
    };

    public ImageAdapter(Context c,int heigth) {
        mContext = c;
        gridHieght = heigth;
        Log.d("MainActivity", "gridHieght::"+gridHieght);
    }

    public void restartGame()
    {
        //Reset the GameBoard and score for new level
        // reset flags
        selectedCards.clear();
        selectedPositions.clear();
        flipCount = 0;
        score = 0;
        createBoard();
        textViewScrore.setText(score+"");
        mThumbIds = new ArrayList<Integer>(masterboardmThumbIds);
        System.out.println("Phani Gave over score"+score);
        System.out.println("Phani thumbniles "+mThumbIds);
        notifyDataSetChanged();


    }

    protected boolean checkGameOver() {
        // TODO Auto-generated method stub
        boolean gaveOver =true;
        for (int i = 0; i < mThumbIds.size(); i++) {
if(mThumbIds.get(i)!=R.mipmap.whitebg)
{
    System.out.println("Phani Game Not over");
    gaveOver=false;
    break;

}
        }
        return gaveOver;
    }

    private TextView findViewById(int scrore) {
        // TODO Auto-generated method stub
        return null;
    }

    public int getCount() {
        return mThumbIds.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    HashMap<Integer, Matrix> mImageTransforms = new HashMap<Integer, Matrix>();

    Matrix mIdentityMatrix = new Matrix();

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
    	 View grid;
         LayoutInflater inflater = (LayoutInflater) mContext
           .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
             if (convertView == null) {
               grid = new View(mContext);
           grid = inflater.inflate(R.layout.cardview, null);
           grid.setMinimumHeight(gridHieght/4);
           
             } else {
               grid = (View) convertView;
             //  grid.setMinimumHeight(gridHieght/4);
            //   grid.setPadding(8, 8, 8, 8);
             }
             ImageView imageView = (ImageView)grid.findViewById(R.id.playCardView);  
             imageView.setImageResource(mThumbIds.get(position));
         return grid;
    }

    void flipCard(int position) {
        if (!nextRound)//For maintaining Round properly
            return;

        if(R.mipmap.whitebg==mThumbIds.get(position))
        {
            System.out.println("Phani Already removed");
            return;

        }
        //For avoiding multiply selection of same card in each round
        if (!selectedPositions.isEmpty() && selectedPositions.get(0) != null
                && selectedPositions.get(0) == position) {
            System.out.println("Phani HITT 0");
            return;
        }
        if (selectedPositions.size() == 2 && selectedPositions.get(1) != null
                && selectedPositions.get(1) == position) {
            System.out.println("Phani HITT 1");

            return;
        }
        mThumbIds.set(position, colorInBoard.get(position));
        selectedCards.add(flipCount, colorInBoard.get(position));
        selectedPositions.add(flipCount, position);
        flipCount++;
        notifyDataSetChanged();
        if (flipCount > 1) {
            nextRound = false;
            System.out.println("sleep for 1 sec flipcount " + flipCount);
            mHandler.postDelayed(sleepDevice, 1000);
        }

    }
    HashMap<Integer, Integer> colorInBoard = new HashMap<Integer, Integer>();

    void createBoard() {
        Integer[] arr = new Integer[16];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = i;
        }
        Collections.shuffle(Arrays.asList(arr));
        for (int i = 0, j = 0; i < arr.length; i++) {

            if (i >= boardColorItems.length) {
                colorInBoard.put(arr[i], boardColorItems[j]);
                j++;
            } else {
                colorInBoard.put(arr[i], boardColorItems[i]);
            }

        }
        System.out.println("Phani" + colorInBoard.toString());

    }

    // references to our images
    List<Integer> mThumbIds = Arrays.asList(R.mipmap.card_bg, R.mipmap.card_bg, R.mipmap.card_bg,
            R.mipmap.card_bg, R.mipmap.card_bg, R.mipmap.card_bg, R.mipmap.card_bg,
            R.mipmap.card_bg, R.mipmap.card_bg, R.mipmap.card_bg, R.mipmap.card_bg,
            R.mipmap.card_bg, R.mipmap.card_bg, R.mipmap.card_bg, R.mipmap.card_bg,
            R.mipmap.card_bg);

    // references to our images
    private Integer[] boardColorItems = {
            R.mipmap.colour1, R.mipmap.colour2, R.mipmap.colour3, R.mipmap.colour4,
            R.mipmap.colour5, R.mipmap.colour6, R.mipmap.colour7, R.mipmap.colour8,
    };

    private final   List<Integer> masterboardmThumbIds = Arrays.asList(R.mipmap.card_bg, R.mipmap.card_bg, R.mipmap.card_bg,
            R.mipmap.card_bg, R.mipmap.card_bg, R.mipmap.card_bg, R.mipmap.card_bg,
            R.mipmap.card_bg, R.mipmap.card_bg, R.mipmap.card_bg, R.mipmap.card_bg,
            R.mipmap.card_bg, R.mipmap.card_bg, R.mipmap.card_bg, R.mipmap.card_bg,
            R.mipmap.card_bg);


    public void setScoreID(TextView findViewById) {
        // TODO Auto-generated method stub

        textViewScrore=findViewById;
    }
}
