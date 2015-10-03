package tempo.tempo;

import android.content.Intent;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;

public class MainActivity extends AppCompatActivity {

    private ImageButton mPrevButton;
    private ImageButton mPlayPauseButton;
    private int mCurrentImageID;
    private ImageButton mNextButton;
    private Button mRestButton;

    private Button mHamButton;

    private SeekBar mIntensityBar;
    private String mSpotifyAccessToken;

    private static final String TAG = "MainActivity.java: ";

    // Request code will be used to verify if result comes from the login activity. Can be set to any integer.
    private static final String CLIENT_ID = "4add93ad663a46ab91b0ef8f00ef68d8"; //ClientId goes here
    private static final int REQUEST_CODE = 6203;
    private static final String REDIRECT_URI = "tempo-app://callback";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("MainActivity", "Authenticating..");
        //Handle Spotify authentication
        AuthenticationRequest.Builder builder =
                new AuthenticationRequest.Builder(CLIENT_ID, AuthenticationResponse.Type.TOKEN, REDIRECT_URI);

        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

        //Button click callbacks
        mPrevButton = (ImageButton) findViewById(R.id.prev_button);
        mPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mPlayPauseButton = (ImageButton) findViewById(R.id.play_pause_button);
        mPlayPauseButton.setImageResource(R.drawable.play_button);
        mCurrentImageID = R.drawable.play_button;
        mPlayPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrentImageID == R.drawable.play_button){
                    mPlayPauseButton.setImageResource(R.drawable.pause_button);
                    mCurrentImageID = R.drawable.pause_button;
                }
                else{
                    mPlayPauseButton.setImageResource(R.drawable.play_button);
                    mCurrentImageID = R.drawable.play_button;
                }
            }
        });

        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mRestButton = (Button) findViewById(R.id.rest_button);
        mHamButton = (Button) findViewById(R.id.ham_button);
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
                    Log.d(TAG, mSpotifyAccessToken);
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
}
