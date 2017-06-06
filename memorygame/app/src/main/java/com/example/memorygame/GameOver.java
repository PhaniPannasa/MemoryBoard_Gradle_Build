package com.example.memorygame;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
@SuppressLint("NewApi")
public class GameOver extends Activity {

    // UI references.
    private EditText mUserName;

    private int userScore;

    private boolean submitted = true;

    private Context mContext;
    private Button mSubmitOnline;
    public final int SUBMIT_SCORE = 1;
	public final int ENTER_LEADERBOARD = 2;
	public final List<String> PERMISSIONS = Arrays.asList("publish_actions");

    private String rank;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
        setContentView(R.layout.activity_login);
        // Set up the login form.
        mUserName = (EditText)findViewById(R.id.email);

        userScore = getIntent().getIntExtra("score", 500);
        // Swarm.init(this, 15601, "1a0895333b8e80a8d0a6849e615a4471");
        findViewById(R.id.sign_in_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                // Swarm.showDashboard();
                if (submitted) {
                    submitted = false;
                    if ( mUserName.getText().length()==0)// check user name validataion
                    {
                        Toast.makeText(getApplicationContext(),
                                "Please enter name ",
                                Toast.LENGTH_SHORT).show();
                        submitted = true;
                        return;

                    }

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            System.out.println("Phani Saving data");
                            MainActivity.gameData.open();
                            // TODO Auto-generated method stub
                            // check if user exits in db
                            if (MainActivity.gameData.checkUserExits(mUserName.getText() + "")) {
                             if (userScore > MainActivity.gameData.readUserScore(mUserName
                                        .getText() + "")) {
                                 // find current rank and update db if score is more than previous score and notify him
                                    // update
                                    ContentValues values = new ContentValues();
                                    values.put(MySQLiteHelper.COLUMN_SCORE, userScore);
                                    System.out.println("Phani updatedddd ="
                                            + MainActivity.gameData.updateExistingData(values,
                                                    mUserName.getText() + ""));

                                } else {

                                    // just notify by getting current rank and
                                    // score
                               

                                }

                            } else {
                            	// if new user add new db
                                ContentValues values = new ContentValues();
                                values.put(MySQLiteHelper.COLUMN_USERNAME, mUserName.getText() + "");
                                values.put(MySQLiteHelper.COLUMN_SCORE, userScore);
                                values.put(MySQLiteHelper.COLUMN_RANK, 0);
                                MainActivity.gameData.saveUserData(values);
     

                            }
                            // Get UserRank
                            HashMap<String , Integer> map = getUserCurrentScore(mUserName.getText().toString());
                            map.get(mUserName.getText().toString());
                            rank = map.get(mUserName.getText().toString())+"";
                            System.out.println("User Rank is  "+ rank);

                            MainActivity.gameData.close();
                            submitted = true;
                            finish(5);
                        }
                    }).start();

                }
            }
        });
        
    }


	private boolean isSubsetOf(Collection<String> subset, Collection<String> superset) {
		for (String string : subset) {
			if (!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}

	
	
    public HashMap<String, Integer> getUserCurrentScore(String mUserName)
    {

        HashMap<String,Integer> map = new HashMap<String,Integer>();
        String[] allColumns = { MySQLiteHelper.COLUMN_USERNAME,
                MySQLiteHelper.COLUMN_SCORE,MySQLiteHelper.COLUMN_RANK };
        Cursor c = MainActivity.gameData.readSavedData();
        int i=1;
        if (c.moveToFirst())
        {
            do
            {
                String name = null;
                int score =0;
                for (int j = 0; j < allColumns.length; j++) {
                    if(allColumns[j].equals(MySQLiteHelper.COLUMN_SCORE))
                    {
                        score = c.getInt(c.getColumnIndex(allColumns[j]));
                    }
                    if(allColumns[j].equals(MySQLiteHelper.COLUMN_USERNAME))
                    {
                        name = c.getString(c.getColumnIndex(allColumns[j]));
                        if(name.equalsIgnoreCase(mUserName))
                        {
                           name = name+"PreviousRank";
/*                                                     map.put(name+"PreviousHighScore", i);
                         continue;*/
                        }
                    }

                }
                System.out.println("Phani --------Name-----"+name);
                map.put(name, score);
                i++;
            } while (c.moveToNext());
        }
        map.put(mUserName,userScore);
        map = sortPlayerByScore(map);
        System.out.println("Phani --------Map-----"+map);

        return map;


    }


    @SuppressWarnings("unchecked")
    LinkedHashMap sortPlayerByScore(HashMap passedMap) {
        List mapKeys = new ArrayList(passedMap.keySet());
        List mapValues = new ArrayList(passedMap.values());
       Collections.sort(mapValues);
       Collections.reverse(mapValues);
        LinkedHashMap sortedMap = new LinkedHashMap();

        Iterator valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            Object val = valueIt.next();
            Iterator keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                Object key = keyIt.next();
                String comp1 = passedMap.get(key).toString();
                String comp2 = val.toString();

                if (comp1.equals(comp2)){
                    passedMap.remove(key);
                    mapKeys.remove(key);
                    sortedMap.put((String)key, (Integer)val);
                    break;
                }

            }

        }


        int rank = 0;
        int lastVotes = -500;
        LinkedHashMap finalsortedMap = new LinkedHashMap();

        Iterator it = sortedMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pairs = (Map.Entry)it.next();
            System.out.println("Phaniiiiiiiiiiiii"+pairs.getKey() + " = " + pairs.getValue());
            if ( Integer.parseInt(pairs.getValue().toString()) != lastVotes)
            {
              rank += 1;
            }
            finalsortedMap.put(pairs.getKey(),rank);
            lastVotes =Integer.parseInt(pairs.getValue().toString());
            it.remove(); // avoids a ConcurrentModificationException
        }
        System.out.println("PhaniiiiiiiiiiiiifinalsortedMap ="+finalsortedMap);

        return finalsortedMap;
     }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }



    public void finish(int returncode) {
    	Intent returnIntent = new Intent();
        returnIntent.putExtra("playerName", mUserName.getText().toString());
        returnIntent.putExtra("playerRank", rank);
        returnIntent.putExtra("playerScore", userScore + "");

        setResult(returncode, returnIntent);
        super.finish();
    }

}
