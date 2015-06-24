package com.jackwink.tweakabledemo;

import android.app.Activity;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.jackwink.tweakable.Tweakable;
import com.squareup.seismic.ShakeDetector;

public class MainActivity extends Activity implements ShakeDetector.Listener {
    private static final String TAG = MainActivity.class.getSimpleName();

    TextView featureFlagView;
    TextView featureFlag2View;
    TextView featureFlag3View;
    TextView featureFlag4View;

    ShakeDetector shakeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        featureFlagView = (TextView) findViewById(R.id.featureFlagView);
        featureFlag2View = (TextView) findViewById(R.id.featureFlag2View);
        featureFlag3View = (TextView) findViewById(R.id.featureFlag3View);
        featureFlag4View = (TextView) findViewById(R.id.featureFlag4View);

        shakeDetector = new ShakeDetector(this);
        Tweakable.init(this);
    }


    @Override
    public void onPause() {
        super.onPause();
        shakeDetector.stop();
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
    public void onResume() {
        super.onResume();
        featureFlagView.setText("featureFlag: " + Settings.featureFlag);
        featureFlag2View.setText("featureFlag2: " + Settings.featureFlag2);
        featureFlag3View.setText("featureFlag3: " + Settings.featureFlag3);
        featureFlag4View.setText("featureFlag4: " + Settings.featureFlag4);

        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        shakeDetector.start(sensorManager);
    }

    @Override
    public void hearShake() {
        Log.i(TAG, "Shake!");
        shakeDetector.stop();
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

}
