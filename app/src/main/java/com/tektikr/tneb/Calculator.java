package com.tektikr.tneb;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;


public class Calculator extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private int mSection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caclulator);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        mSection = number;
        switch (number) {
            case 1:
                mTitle = getString(R.string.calc2016_section);
                break;
            case 2:
                mTitle = getString(R.string.calc2015_section);
                break;
            case 3:
                mTitle = getString(R.string.calc2012_section);
                break;
            case 4:
                mTitle = getString(R.string.onlinepayment_section);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.caclulator, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
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

    public void buttonOnClick(View v) {
        // do something when the button is clicked
        computeTariff();
        hideKeyboard();
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void computeTariff() {
        //TextView myTariffView = (TextView) findViewById(R.id.textView2);
        TextView myTextView = (TextView) findViewById(R.id.textView);
        EditText myUnitsText = (EditText) findViewById(R.id.editText);
        String unitsStr = myUnitsText.getText().toString();
        if (unitsStr != null && !unitsStr.isEmpty()) if (unitsStr.length() <= 6) {
            int ebUnits = Integer.parseInt(unitsStr);
            //myTextView.setText("Units Consumed:" + unitsConsumed);
            try {
                DecimalFormat df = new DecimalFormat(getString(R.string.tariff_decimal_format));
                float tariff = 0;
                switch (mSection) {
                    case 1:
                        tariff = getTariff2016(ebUnits);
                        break;
                    case 2:
                        tariff = getTariff2015(ebUnits);
                        break;
                    case 3:
                        tariff = getTariff2012(ebUnits);
                        break;
                }
                myTextView.setText("\u20B9 " + df.format(tariff));

            } catch (Exception exp) {
                myTextView.setText(getString(R.string.error_1000));

            }
        } else {
            myTextView.setText(getString(R.string.info_large_units));

        }
    }

    private float getTariff2016(int ebUnits) {
        float ebAmount = 0;
        float chargedUnits = 0;
        chargedUnits = ebUnits - 100;
        if (ebUnits > 0 && ebUnits <= 100) {
            ebAmount = 0;
        } else if (ebUnits <= 200) {
            ebAmount = 20 + 1.5f * chargedUnits;
        } else if (ebUnits <= 500) {
            ebAmount = 30 + 100 * 2 + (chargedUnits - 100) * 3;
        } else if (ebUnits > 500) {
            ebAmount = 50 + 100 * 3.5f + 300 * 4.6f + (chargedUnits - 400) * 6.6f;
        }
        return ebAmount;
    }

    private float getTariff2015(int ebUnits) {
        float oldEbAmount;
        float subsidy;
        float ebAmount;
        float tariff = 0;

        if (ebUnits > 0 && ebUnits <= 100) {
            oldEbAmount = 20 + ebUnits;
            ebAmount = 20 +  ebUnits*3;
            subsidy = ebAmount - oldEbAmount;
            tariff = oldEbAmount;
        }
        else if (ebUnits <= 200) {
            oldEbAmount = 20 + 1.5f * ebUnits;
            ebAmount = 20 + 3.25f * ebUnits;
            subsidy = ebAmount - oldEbAmount;
            tariff = oldEbAmount;
        }
        else if (ebUnits <= 500) {
            oldEbAmount = 30 + 200 * 2f + (ebUnits - 200) * 3f;
            ebAmount = 30 + 200 * 3.5f + (ebUnits - 200) * 4.6f;
            subsidy = ebAmount - oldEbAmount;
            tariff = oldEbAmount;
        }
        else if (ebUnits > 500) {
            oldEbAmount = 50 + 200 * 3f + 300 * 4f + (ebUnits - 500) * 5.75f;
            ebAmount = 50 + 200 * 3.5f + 300 * 4.6f + (ebUnits - 500) * 6.6f;
            tariff = ebAmount;
        }
        return tariff;
    }

    private float getTariff2012(int ebUnits) {
        float ebAmount = 0;
        if (ebUnits > 0 && ebUnits <= 100) {
            ebAmount = 20 + ebUnits;
        } else if (ebUnits <= 200) {
            ebAmount = 20 + 1.5f * ebUnits;
        } else if (ebUnits <= 500) {
            ebAmount = 30 + 200 * 2 + (ebUnits - 200) * 3;
        } else if (ebUnits > 500) {
            ebAmount = 40 + 200 * 3 + 300 * 4 + (ebUnits - 500) * 5.75f;
        }
        return ebAmount;
    }

    public void unitsTextClicked(View v) {
        TextView myTextView = (TextView) findViewById(R.id.textView);
        //TextView myTariffView = (TextView) findViewById(R.id.textView2);
        EditText myUnitsText = (EditText) findViewById(R.id.editText);
        //myTariffView.setText(R.string.empty_string);
        myTextView.setText(R.string.enter_units);
        myUnitsText.setText(R.string.empty_string);
    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static int mSection;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            mSection = sectionNumber;
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }
        private Handler handler = new Handler(){
            @Override
            public void handleMessage(Message message) {
                switch (message.what) {
                    case 1:{
                        webViewGoBack();
                    }break;
                }
            }
        };

        WebView myWebView;

        private void webViewGoBack(){
            myWebView.goBack();
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView;
            if (mSection == 4) {
                rootView = inflater.inflate(R.layout.fragment_payment, container, false);
                myWebView = (WebView) rootView.findViewById(R.id.webView);
                myWebView.setWebViewClient(new WebViewClient());

                // Enable Javascript
                WebSettings webSettings = myWebView.getSettings();
                webSettings.setJavaScriptEnabled(true);

                myWebView.loadUrl("https://www.tnebnet.org/awp/tneb");
                myWebView.setOnKeyListener(new View.OnKeyListener(){

                    public boolean onKey(View v, int keyCode, KeyEvent event) {
                        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
                            handler.sendEmptyMessage(1);
                            return true;
                        }
                        return false;
                    }

                });
            }else {
                rootView = inflater.inflate(R.layout.fragment_caclulator, container, false);

            }

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((Calculator) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
