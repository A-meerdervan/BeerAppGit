package nl.mprog.project.bieraanbiedingnotificatie;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NotificatieRegelActivity extends AppCompatActivity {

    private static final String tag = "NotifDisc";
    private List<DiscountObject> discountArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificatie_regel);

        // Set the task to fill the listview
        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
        jsoupAsyncTask.execute();
    }


    private void populateListView() {
        // TODO: Dit is alleen voor het prototype en moet nog dynamish van de database
        DiscountObject currentDiscount = new DiscountObject("Hertog Jan","24 flesjes 0.33 per stuk",8.10,1.12,"albertheijn","Geldig tm zondag 10 januari","krat");
        DiscountObject currentDiscount2 = new DiscountObject("brand","24 flesjes 0.33 per stuk",9.20,1.34,"albertheijn","Geldig tm zondag 10 januari","krat");
        discountArray.add(currentDiscount);
        discountArray.add(currentDiscount2);


        // Fill the listview list
        ArrayAdapter<DiscountObject> adapter = new MyListAdapter();
        ListView list = (ListView) findViewById(R.id.notifcationsItemsList);
        list.setAdapter(adapter);
    }

    // This custom adapter class is made to fill the listview with discount information
    private class MyListAdapter extends ArrayAdapter<DiscountObject> {

        // constructor
        public MyListAdapter() {
            super(NotificatieRegelActivity.this, R.layout.listview, discountArray);
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
            brand.setText(discountObject.brand);

            // Fill the format
            TextView format = (TextView) itemView.findViewById(R.id.format);
            format.setText(discountObject.format);

            // Fill the period
            TextView period = (TextView) itemView.findViewById(R.id.period);
            period.setText(discountObject.discountPeriod);

            // Fill the price
            TextView price = (TextView) itemView.findViewById(R.id.price);
            price.setText("$" + discountObject.price);

            // Fill the literPrice
            TextView literPrice = (TextView) itemView.findViewById(R.id.literPrice);
            literPrice.setText("$" + discountObject.pricePerLiter + " p/lr");

            // Set the supermarkt image
            ImageView superMarkt = (ImageView) itemView.findViewById(R.id.imgSuper);
            superMarkt.setImageResource(R.drawable.albertheijn);

            // Set the item image
            ImageView itemImg = (ImageView) itemView.findViewById(R.id.img);
            itemImg.setImageResource(R.drawable.hertogjan);
            return itemView;
        }
    }

    // TODO: this class is currently run from the oncreate function.
    // It should be run every night or something, to update the database
    private class JsoupAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            // TODO: Get discountArray from the database
//            discountArray = htmlParser.getDiscountsArray();

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            // Fill the listView
            populateListView();
        }
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_notificatie_regel, menu);
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