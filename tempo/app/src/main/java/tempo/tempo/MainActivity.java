package tempo.tempo;

import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;

public class MainActivity extends AppCompatActivity {

    private ImageButton mPrevButton;
    private ImageButton mPlayPauseButton;
    private int mCurrentImageID;
    private ImageButton mNextButton;

    private SeekBar mIntensityBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
}
