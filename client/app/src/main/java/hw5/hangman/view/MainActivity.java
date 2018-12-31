package hw5.hangman.view;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.EditText;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.view.inputmethod.InputMethodManager;
import android.content.Context;
import android.view.KeyEvent;
import android.os.AsyncTask;
import android.content.*;

import hw5.hangman.controller.*;
import hw5.hangman.SafeOutput;

import hw5.hangman.R;

public class MainActivity extends AppCompatActivity {

    public static Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button butt = (Button) findViewById(R.id.button);
        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                letsConnect(view);
            }
        });



        EditText editText = (EditText) findViewById(R.id.editText2);
        editText.setOnKeyListener(new OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if (event.getAction() == KeyEvent.ACTION_DOWN)
                {
                    switch (keyCode)
                    {
                        case KeyEvent.KEYCODE_DPAD_CENTER:
                        case KeyEvent.KEYCODE_ENTER:
                            letsConnect(v);
                            return true;
                        default:
                            break;
                    }
                }
                return false;
            }
        });



    }

    protected void letsConnect(View view)
    {
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        Snackbar.make(view, "Trying to connect...", Snackbar.LENGTH_INDEFINITE)
                .setAction("Action", null).show();

        String ip;
        String port;

        EditText editText1 = (EditText) findViewById(R.id.editText);
        ip = editText1.getText().toString();

        EditText editText2 = (EditText) findViewById(R.id.editText2);
        port = editText2.getText().toString();


        new connectionTask().execute(ip,port);
    }


    private class connectionTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {


            try {

                MainActivity.controller.connectServer(params[0], params[1]);

            }
            catch(Exception ex)
            {
                return ex.getMessage();
            }

            return "Successfully connected.";
        }

        @Override
        protected void onPostExecute(String result) {

            View view = findViewById(android.R.id.content);

            Snackbar.make(view, result, Snackbar.LENGTH_INDEFINITE)
                    .setAction("Action", null).show();

            Intent myIntent = new Intent(MainActivity.this, GameActivity.class);
            MainActivity.this.startActivity(myIntent);

        }

        @Override
        protected void onPreExecute()
        {
            MainOutputHandler outputHandler = new MainOutputHandler();
            MainActivity.controller = new Controller(outputHandler);
        }

        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    public class MainOutputHandler implements SafeOutput {
        public MainOutputHandler()
        {

        }

        @Override
        public synchronized void printResult(String str) {
            System.out.println(str);
        }

        @Override
        public synchronized void terminatedConnection() {
            System.out.println("Connection terminated.");
        }

        @Override
        public synchronized void statusMessage(String msg)
        {
            System.out.println(msg);

        }
        @Override
        public synchronized void resultMessage(String word,String remaining, String score)
        {
            System.out.println(word + " " + remaining + " " + score);
        }
    }



}
