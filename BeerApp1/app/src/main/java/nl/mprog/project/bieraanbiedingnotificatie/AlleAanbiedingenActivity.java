package nl.mprog.project.bieraanbiedingnotificatie;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import android.os.AsyncTask;

public class AlleAanbiedingenActivity extends AppCompatActivity implements FilterFragment.OnFragmentInteractionListener {

    private static final String tag = "*C_AllDisc";
    private List<DiscountObject> discountArray = new ArrayList<>();
    private List<DiscountObject> sortedAndFilteredList = new ArrayList<>();
//    private HtmlParser htmlParser = new HtmlParser();
    private DataBaseHandler dataBaseHandler = new DataBaseHandler(this);
    private FilterAndSorter filterAndSorter = new FilterAndSorter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alle_aanbiedingen);

        // Start up the filter fragment
        initializeFragment();

        // Show loading spinner
        findViewById(R.id.loadSpinnerAllDisc).setVisibility(View.VISIBLE);
        // TODO: fixen dat de spinner in beeld komt tijdens het laden.  populateListVIew stond hier om te testen

        // Get the discounts from the database and sort the discounts on price
        discountArray = dataBaseHandler.getAllDiscounts();
        discountArray = filterAndSorter.sortOnPrice(discountArray);
        populateListView(discountArray);

        // Hide loading spinner
        findViewById(R.id.loadSpinnerAllDisc).setVisibility(View.GONE);

        // parse HTML
//        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
//        jsoupAsyncTask.execute();
        Log.d(tag, "Eind oncreate alldisc.");
    }

    private void populateListView(List<DiscountObject> discountArray) {
        // Fill the listview list
        ArrayAdapter<DiscountObject> adapter = new MyListAdapter(discountArray);
        ListView list = (ListView) findViewById(R.id.AllDiscountsList);
        list.setAdapter(adapter);
    }

    // this returns the resource integer id of a supermaket image
    private int getSuperImageResource(String superMarket) {
        int resId = getApplicationContext().getResources().getIdentifier(superMarket, "drawable", "nl.mprog.project.bieraanbiedingnotificatie");
        if (resId != 0) {
            return resId;
        }
        // If image is not found:
        return R.drawable.hertogjan;
    }

    // this returns the resource integer id of a brand image
    private int getBeerImageResource(String brand) {
        int resId = getApplicationContext().getResources().getIdentifier(brand, "drawable", "nl.mprog.project.bieraanbiedingnotificatie");
        if (resId != 0) {
            return resId;
        }
        // If image is not found:
        return R.drawable.imagenotfound;
    }

    // This custom adapter class is made to fill the listview with discount information
    private class MyListAdapter extends ArrayAdapter<DiscountObject> {

        private List<DiscountObject> discountArray;

        // constructor
        public MyListAdapter(List<DiscountObject> discountArray) {
            super(AlleAanbiedingenActivity.this, R.layout.listview, discountArray);
            this.discountArray = discountArray;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Make sure we have a view to work with (may have been given null)
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.listview, parent, false);
            }

            // Get the discount object
            DiscountObject discountObject = discountArray.get(position);

            // Fill the views

            // Fill the brand
            TextView brand = (TextView) itemView.findViewById(R.id.brand);
            brand.setText(discountObject.brandPrint);

            // Fill the format
            TextView format = (TextView) itemView.findViewById(R.id.format);
            format.setText(discountObject.format);

            // Fill the period
            TextView period = (TextView) itemView.findViewById(R.id.period);
            period.setText(discountObject.discountPeriod);

            // Fill the price
            TextView price = (TextView) itemView.findViewById(R.id.price);
            price.setText("€" + discountObject.price);

            // Fill the literPrice
            TextView literPrice = (TextView) itemView.findViewById(R.id.literPrice);
            literPrice.setText("€" + discountObject.pricePerLiter + " p/lr");

            // Set the supermarkt image
            ImageView superMarkt = (ImageView) itemView.findViewById(R.id.imgSuper);
            superMarkt.setImageResource(getSuperImageResource(discountObject.superMarkt));


            // Set the item image
            ImageView itemImg = (ImageView) itemView.findViewById(R.id.img);
            itemImg.setImageResource(getBeerImageResource(discountObject.brand));
            return itemView;
        }
    }

//    // It should be run every night or something, to update the database
//    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected Void doInBackground(Void... params) {
//
//            discountArray = dataBaseHandler.getAllDiscounts();
//            if (discountArray.size() == 0) {
//                // The database has not been filled yet
//                Log.d(tag, "the database has not been filled yet");
//                discountArray = htmlParser.getDiscountsArray();
//                dataBaseHandler.storeDiscounts(discountArray);
//            } else {
//                Log.d(tag, "the database was already filled");
//            }
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void result) {
//            // Hide the loading spinner
//            findViewById(R.id.loadSpinnerAllDisc).setVisibility(View.GONE);
//            // Fill the listView
//            populateListView();
//        }
//    }

    // This method receives filter and sort information from the fragment:
    // And filters the discounts and then updates the listview.
    @Override
    public void onFragmentInteraction(String sortOption, Double maxPrice, List<String> checkedBeerOptions, List<String> checkedSuperMarkets) {
        // TODO: Zorgen dat de fragment nice opzij swiped
        // Hide the fragment to show loading spinner
        toggleFragment();

        // Show loading spinner:
        findViewById(R.id.loadSpinnerAllDisc).setVisibility(View.VISIBLE);

        // Filter and sort
        sortedAndFilteredList = filterAndSorter.filterAndSort(sortOption, maxPrice, checkedBeerOptions, checkedSuperMarkets, discountArray);

        Log.d(tag, "Lengte resultlist " + sortedAndFilteredList.size());

        // Show the filtered list on screen
        populateListView(sortedAndFilteredList);

        // Hide the loading spinner:
        findViewById(R.id.loadSpinnerAllDisc).setVisibility(View.GONE);
    }

    public void initializeFragment() {
        // Replace the empty holder in the layout file with the fragment, and hide it
        FilterFragment filterFragment = new FilterFragment();
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction
                .replace(R.id.filterFragment, filterFragment, "filter")
                .hide(filterFragment)
                .commit();
        // This brings the fragment on top of everything
        FrameLayout container = (FrameLayout) findViewById(R.id.filterFragment);
        container.bringToFront();
    }

    public void toggleFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        Fragment fragment = fragmentManager.findFragmentByTag("filter");
        if (fragment.isVisible()) {
            transaction
                    .hide(fragment)
                    .commit();
            getSupportFragmentManager().popBackStack();
        } else {
            transaction
                    .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                    .show(fragment)
                    .addToBackStack("filter")
                    .commit();
        }
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
            NavUtils.navigateUpFromSameTask(this);
        } else {
            getFragmentManager().popBackStack();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alle_aanbiedingen, menu);
        return true;
    }

    // Open ore close the fragment by a click on a menu bar field
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.filter) {
            toggleFragment();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

