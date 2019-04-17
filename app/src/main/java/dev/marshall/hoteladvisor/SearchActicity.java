package dev.marshall.hoteladvisor;


import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.algolia.instantsearch.events.ErrorEvent;
import com.algolia.instantsearch.helpers.InstantSearch;
import com.algolia.instantsearch.helpers.Searcher;
import com.algolia.instantsearch.ui.views.Hits;
import com.algolia.instantsearch.ui.views.SearchBox;
import com.algolia.instantsearch.utils.ItemClickSupport;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONException;
import org.json.JSONObject;

import dev.marshall.hoteladvisor.common.Common;

public class SearchActicity extends AppCompatActivity {

    private Searcher searcher;

    private FilterResultsFragment filterResultsFragment;
    private SearchView searchBox;
    Hits hits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        EventBus.getDefault().register(this);
        Toolbar toolbar=(Toolbar)findViewById(R.id.tool);
        setSupportActionBar(toolbar);

        hits=(Hits)findViewById(R.id.hits);
        hits.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, int position, View v) {
                JSONObject hit = hits.get(position);
                // Do something with the hit
                Intent details=new Intent(SearchActicity.this,HotelDetails.class);
                try {
                    details.putExtra("hotelId",hit.getString("objectID"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                startActivity(details);
            }
        });

        // Initialize a Searcher with your credentials and an index name
        searcher = Searcher.create(Common.APPLICATION_ID, Common.API_KEY, Common.ALGOLIA_INDEX_NAME);
        // Create the FilterResultsFragment here so it can set the appropriate facets on the Searcher
        filterResultsFragment = FilterResultsFragment.get(searcher)
                .addSeekBar("Price", 100)
                .addSeekBar("Rating", "stars", 100)
                .addCheckBox("Amenities.food:true", "Restuarant", true)
                .addCheckBox("Amenities.casino:true", "Golf Course", true)
                .addCheckBox("Amenities.internet:true", "Internet(Free WiFi", true)
                .addCheckBox("Amenities.rservice:true", "Room Service", true)
                .addCheckBox("Amenities.security:true", "Security", true)
                .addCheckBox("Amenities.laundry:true", "Laundry(ironing,dry-Cleaning)", true)
                .addCheckBox("Amenities.pool:true", "Swimming Pool", true)
                .addCheckBox("Amenities.beach:true", "Beach Access", true)
                .addCheckBox("Amenities.bar:true", "Bar and Lounge", true)
                .addCheckBox("Amenities.airportshuttle:true", "Airport Shuttle", true)
                .addCheckBox("Amenities.gym:true", "Gym and Fitness services", true)
                .addCheckBox("Amenities.airconditioning:true", "Air Conditioning", true)
                .addCheckBox("Amenities.spa:true", "Spa", true)
                .addCheckBox("Amenities.child:true", "Child Friendly", true);



        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SearchActicity.this,Home.class));
            }
        });


    }
    @Override protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        searcher.search(intent); // Show results for voice query (from intent)
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_hotel, menu);

        new InstantSearch(this, menu, R.id.action_search, searcher); // link the Searcher to the UI

        searcher.search(getIntent()); // Show results for empty query (on app launch) / voice query (from intent)

        final MenuItem itemSearch = menu.findItem(R.id.action_search);
        searchBox = (SearchBox) itemSearch.getActionView();
            itemSearch.expandActionView(); //open SearchBar on startup

        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            final FragmentManager fragmentManager = getSupportFragmentManager();
            if (fragmentManager.findFragmentByTag(FilterResultsFragment.TAG) == null) {
                filterResultsFragment.show(fragmentManager, FilterResultsFragment.TAG);
                hideKeyboard();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Subscribe
    public void onErrorEvent(ErrorEvent event) {
        Toast.makeText(this, "Error searching" + event.query.getQuery() + ":" + event.error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
    }

    private void hideKeyboard() {
        searchBox.clearFocus();
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}

