package net.area54labs.jadwalkrl2;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import net.area54labs.jadwalkrl2.utils.SearchSetting;


public class MainActivity extends ActionBarActivity {
    private static final String DETAIL_FRAGMENT_TAG = "DFTAG";
    private final String LOG_TAG = getClass().getSimpleName();
    private boolean mTwoPane;
    private SearchSetting mSearchSetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSearchSetting = new SearchSetting(getApplicationContext());
        setContentView(R.layout.activity_main);

//        if (findViewById(R.id.fragment_left) != null && findViewById(R.id.fragment_right) != null) {
//            mTwoPane = true;
//
//            if (savedInstanceState == null) {
//                getSupportFragmentManager().beginTransaction()
//                        .replace(R.id.fragment_left)
//            }
//        }
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new SearchFragment())
                    .commit();
        }
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
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
