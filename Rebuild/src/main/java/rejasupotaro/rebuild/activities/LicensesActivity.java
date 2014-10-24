package rejasupotaro.rebuild.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.webkit.WebView;

import javax.inject.Inject;

import rejasupotaro.rebuild.R;
import rejasupotaro.rebuild.tools.MenuDelegate;
import roboguice.inject.InjectView;

public class LicensesActivity extends RoboActionBarActivity {

    private static final String LICENSES_FILE_PATH = "file:///android_asset/licenses.html";

    @InjectView(R.id.licenses_view)
    private WebView licensesView;

    @Inject
    private MenuDelegate menuDelegate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_licenses);
        setupActionBar();
        setupLicensesView();
    }

    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    private void setupLicensesView() {
        licensesView.getSettings().setJavaScriptEnabled(false);
        licensesView.loadUrl(LICENSES_FILE_PATH);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                menuDelegate.pressHome();
                return true;
            case R.id.action_settings:
                menuDelegate.pressSettings();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
