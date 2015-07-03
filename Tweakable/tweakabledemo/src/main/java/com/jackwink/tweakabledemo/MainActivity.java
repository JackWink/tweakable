package com.jackwink.tweakabledemo;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.jackwink.tweakable.Tweakable;

public class MainActivity extends Activity {
    private static final String TAG = MainActivity.class.getSimpleName();

    TextView featureFlagView;
    TextView featureFlag2View;
    TextView featureFlag3View;
    TextView featureFlag4View;

    TextView strOptions1View;
    TextView strOptions2View;


    TextView intOptions1View;
    TextView intOptions2View;

    TextView floatOptions1View;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        featureFlagView = (TextView) findViewById(R.id.featureFlagView);
        featureFlag2View = (TextView) findViewById(R.id.featureFlag2View);
        featureFlag3View = (TextView) findViewById(R.id.featureFlag3View);
        featureFlag4View = (TextView) findViewById(R.id.featureFlag4View);

        strOptions1View = (TextView) findViewById(R.id.stringOptions1View);
        strOptions2View = (TextView) findViewById(R.id.stringOptions2View);

        intOptions1View = (TextView) findViewById(R.id.intOptions1View);
        intOptions2View = (TextView) findViewById(R.id.intOptions2View);


        floatOptions1View = (TextView) findViewById(R.id.floatOptions1View);

        Tweakable.init(this);
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

        strOptions1View.setText("Selected option: " + Settings.stringOptions1);
        strOptions2View.setText("Written Value: " + Settings.stringOptions2);

        intOptions1View.setText("Int 1: " + Settings.intOptions1);
        intOptions2View.setText("Int 2: " + Settings.intOptions2);

        floatOptions1View.setText("Float 1: " + Settings.floatOption1);
    }

}
