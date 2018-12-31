package hw5.hangman.view;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.R.drawable.*;
import android.content.Intent;

import hw5.hangman.SafeOutput;
import hw5.hangman.R;

public class GameActivity extends AppCompatActivity {

    protected GameOutputHandler outputHandler;
    protected boolean status = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        new changeOutputHandlerTask().execute();
        new startGameTask().execute();

        Button quit = findViewById(R.id.button3);
        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new quitGameTask().execute();

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                finish();
            }
        });

        Button butt = findViewById(R.id.button2);
        butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText editText3i = findViewById(R.id.editText3);

                if(editText3i.getText().equals(""))
                    return;

                if(!status) {

                    EditText editText3 = findViewById(R.id.editText3);
                    String guess = "";
                    if (editText3.getText() != null)
                        guess = editText3.getText().toString();
                    new guessGameTask().execute(guess);
                }
                else
                {
                    status = true;

                    EditText editText3 = findViewById(R.id.editText3);
                    editText3.setEnabled(true);

                    Button button2 = findViewById(R.id.button2);
                    button2.setText("Send");

                    new startGameTask().execute();


                    InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.showSoftInput(editText3, 0);

                }

                EditText editText3 = findViewById(R.id.editText3);
                editText3.setText("");
            }
        });


    }

    private class quitGameTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            try
            {
                MainActivity.controller.disconnectServer();
            }
            catch(Exception ex)
            {
                System.out.println(ex.getMessage());
            }
            return "done";

        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected void onPreExecute()
        {

        }
        @Override
        protected void onProgressUpdate(Void... values) {}
    }


    private class startGameTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {

            try
            {
                MainActivity.controller.startGame();
            }
            catch(Exception ex)
            {
                System.out.println(ex.getMessage());
            }
            return "done";

        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected void onPreExecute()
        {

        }
        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    private class guessGameTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {

            try
            {
                MainActivity.controller.guessGame(params[0]);
            }
            catch(Exception ex)
            {
                System.out.println(ex.getMessage());
            }
            return "done";

        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected void onPreExecute()
        {

        }
        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    private class changeOutputHandlerTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {

            try
            {
                outputHandler = new GameOutputHandler();
                MainActivity.controller.setNewSafeOutput(outputHandler);
            }
            catch(Exception ex)
            {
                System.out.println(ex.getMessage());
            }
            return "done";
        }

        @Override
        protected void onPostExecute(String result) {

        }

        @Override
        protected void onPreExecute()
        {

        }
        @Override
        protected void onProgressUpdate(Void... values) {}
    }

    public class GameOutputHandler implements SafeOutput {
        public GameOutputHandler()
        {

        }

        @Override
        public synchronized void printResult(String str) {

        }

        @Override
        public synchronized void terminatedConnection() {

        }

        @Override
        public synchronized void statusMessage(String msg)
        {

        }
        @Override
        public synchronized void resultMessage(String iword,String iremaining, String iscore)
        {


            final String wordRes = iword;

            final TextView wordView = findViewById(R.id.textView3);
            wordView.post(new Runnable() {
                public void run() {
                    wordView.setText(wordRes);
                }
            });


            if(!iword.contains("_"))
            {
                status = true;

                final EditText editText3 = findViewById(R.id.editText3);
                editText3.post(new Runnable() {
                    public void run() {
                        editText3.setEnabled(false);
                    }
                });

                final Button button2 = findViewById(R.id.button2);
                button2.post(new Runnable() {
                    public void run() {
                        button2.setText("Start");
                    }
                });


            }


            final TextView remView = (TextView) findViewById(R.id.textView4);
            final String remRes = iremaining;
            remView.post(new Runnable() {
                public void run() {
                    remView.setText(remRes);
                }
            });



            final TextView scoreView = (TextView) findViewById(R.id.textView5);
            final String scoreRes = iscore;
            scoreView.post(new Runnable() {
                public void run() {
                    scoreView.setText(scoreRes);
                }
            });


        }
    }

}
