package tempo.tempo;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Scanner;

import com.spotify.sdk.android.player.Spotify;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerNotificationCallback;
import com.spotify.sdk.android.player.PlayerState;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements
        PlayerNotificationCallback, ConnectionStateCallback {

    private ImageButton mPlayPauseButton;
    private ImageButton mNextButton;
    private Button mRestButton;
    private AudioManager myAudioManager;

    private Boolean isPlaying = false;
    private Boolean hasPlayed = false;
    private Boolean isResting = false;

    private SeekBar mIntensityBar;
    private String mSpotifyAccessToken;

    private static final String TAG = "MainActivity.java: ";

    // Request code will be used to verify if result comes from the login activity. Can be set to any integer.
    private static final String CLIENT_ID = "4add93ad663a46ab91b0ef8f00ef68d8"; //ClientId goes here
    private static final int REQUEST_CODE = 6203;
    private static final String REDIRECT_URI = "tempo-app://callback";

    private Player mPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "OnCreate called");

        //Handle Spotify authentication
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

        myAudioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);


        mPlayPauseButton = (ImageButton) findViewById(R.id.play_pause_button);
        mPlayPauseButton.setImageResource(R.drawable.play_button);
        mPlayPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPlaying){
                    mPlayPauseButton.setImageResource(R.drawable.pause_button);
                    if (!isResting){
                        mRestButton.setEnabled(true);
                    }
                    if (hasPlayed) {
                        mPlayer.resume();
                        isPlaying = true;
                    }
                    else{
                        //new getSongID().execute("130");
                        mPlayer.play("spotify:track:2EQhNdnP2LT96NnkkKkm0N");
                        isPlaying = true;
                        hasPlayed = true;
                    }
                }
                else{
                    mPlayPauseButton.setImageResource(R.drawable.play_button);
                    mRestButton.setEnabled(false);
                    mPlayer.pause();
                    isPlaying = false;
                }
            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new getSongID().execute("130");
                mPlayPauseButton.setImageResource(R.drawable.pause_button);
                isPlaying = true;
            }
        });

        mRestButton = (Button) findViewById(R.id.rest_button);
        mRestButton.setEnabled(false);
        mRestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRestButton.setEnabled(false);
                new rest().execute();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, data);

            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    // Handle successful response
                    mSpotifyAccessToken = response.getAccessToken();

                    Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                    mPlayer = Spotify.getPlayer(playerConfig, this, new Player.InitializationObserver() {
                        @Override
                        public void onInitialized(Player player) {
                            mPlayer.addConnectionStateCallback(MainActivity.this);
                            mPlayer.addPlayerNotificationCallback(MainActivity.this);
                        }

                        @Override
                        public void onError(Throwable throwable) {
                            Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                        }
                    });

                    break;

                // Auth flow returned an error
                case ERROR:
                    // Handle error response
                    break;

                // Most likely auth flow was cancelled
                default:
                    // Handle other cases
            }
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Throwable error) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }

    @Override
    public void onPlaybackEvent(EventType eventType, PlayerState playerState) {
        Log.d("MainActivity", "Playback event received: " + eventType.name());
        switch (eventType) {
            // Handle event type as necessary
            default:
                break;
        }
    }

    @Override
    public void onPlaybackError(ErrorType errorType, String errorDetails) {
        Log.d("MainActivity", "Playback error received: " + errorType.name());
        switch (errorType) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        // VERY IMPORTANT! This must always be called or else you will leak resources
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    public class EchoNestWithoutSpotify extends AsyncTask<String, Void, Double> {

        @Override
        protected Double doInBackground(String... params) {
            String artist = params[0];
            String song = params[1];

            String url = "http://developer.echonest.com/api/v4/song/search?api_key=B8YFO8YFTNJITHGWH&artist=";
            String urlDelimiter = "%20";

            Scanner artistScanner = new Scanner(artist);

            while(artistScanner.hasNext()){
                url = url + artistScanner.next() + urlDelimiter;
            }

            url.substring(0, url.length() - 3);

            url = url + "&title=";

            Scanner songScanner = new Scanner(song);

            while(songScanner.hasNext()){
                url = url + songScanner.next() + urlDelimiter;
            }

            url.substring(0, url.length() - 3);

            String songID = null;
            try{
                songID = readJsonFromUrl(url).getJSONObject("response").getJSONArray("songs").optJSONObject(0).getString("id");
            }
            catch(Exception e){
                Log.d(TAG, "Problem getting song info");
            }

            String urlForTempo = "http://developer.echonest.com/api/v4/song/profile?api_key=B8YFO8YFTNJITHGWH&id=" + songID + "&bucket=audio_summary";

            String songTempo = null;
            try{
                songTempo = readJsonFromUrl(urlForTempo).getJSONObject("response").getJSONArray("songs").optJSONObject(0).getJSONObject("audio_summary").getString("id");
            }
            catch(Exception e){
                Log.d(TAG, "Problem getting song tempo");
            }

            return Double.parseDouble(songTempo);

        }

    }

    public class getSongID extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            String heartRate = params[0];
            String response = "";
            try{
                URL url = new URL("http://52.89.129.24:80");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.connect();
                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));

                writer.write(heartRate);
                writer.flush();
                Log.d(TAG, "Example works");

                BufferedReader rd = null;


                InputStream is = connection.getInputStream();
                rd = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = rd.readLine()) != null) {
                    // Process line...
                    response += line;
                }
                Log.d(TAG, "Response: " + response);

                rd.close();
                is.close();
                writer.close();
                os.close();
            }
            catch(Exception e){
                Log.d(TAG, "Buffer Error");
            }

            return response;

        }

        @Override
        protected void onPostExecute(String s) {
            s = "spotify:track:" + s;
            Log.d(TAG,s);
            playSong(s);
        }
    }

    public class rest extends AsyncTask<Void, Void, Integer>{
        @Override
        protected Integer doInBackground(Void... params) {

            int streamVolume = myAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            myAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, streamVolume / 2, AudioManager.FLAG_SHOW_UI);
            isResting = true;
            try {
                Thread.sleep(60000, 0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            myAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, streamVolume/2, 0);

            int iterationVolume = streamVolume / 10;
            if (iterationVolume <= 0){
                iterationVolume = 1;
            }

            for (int i = (streamVolume/2); i < streamVolume; i = i + iterationVolume){
                try {
                    Log.d(TAG, "" + i);
                    myAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, i, 0);
                    Thread.sleep(500, 0);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            return 0;

        }

        @Override
        protected void onPostExecute(Integer integer) {
            mRestButton.setEnabled(true);
            isResting = false;
        }
    }

    public void playSong(String id){
        mPlayer.play(id);
    }

    protected static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
        InputStream is = new URL(url).openStream();
        try {
            Scanner rd = new Scanner(new InputStreamReader(is, Charset.forName("UTF-8")));
            String jsonText = null;
            while(rd.hasNextLine()){
                jsonText = jsonText + rd.nextLine();
            }
            JSONObject json = new JSONObject(jsonText);
            return json;
        } finally {
            is.close();
        }
    }

    public void getBLE(View view) {
        Intent intent = new Intent(this, DeviceScanActivity.class);
        startActivity(intent);
    }

    public void logout(MenuItem menuItem){
        AuthenticationClient.logout(getApplicationContext());

        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);
        hasPlayed = false;
        Log.d(TAG, "Logged Out");
    }

}
