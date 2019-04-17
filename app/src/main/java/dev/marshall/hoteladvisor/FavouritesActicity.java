package dev.marshall.hoteladvisor;

import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dev.marshall.hoteladvisor.Database.Database;
import dev.marshall.hoteladvisor.Interface.RecyclerTouchHelperListener;
import dev.marshall.hoteladvisor.common.Common;
import dev.marshall.hoteladvisor.model.Favourites;
import dev.marshall.hoteladvisor.viewHolder.FavouriteAdapter;

public class FavouritesActicity extends AppCompatActivity implements RecyclerTouchHelperListener {

    RecyclerView recycler_favourites;
    RecyclerView.LayoutManager layoutManager;

    ConstraintLayout rootlayout;

    FavouriteAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);

        rootlayout=(ConstraintLayout)findViewById(R.id.root_favourite);
        recycler_favourites=(RecyclerView)findViewById(R.id.recycler_favourites);
        recycler_favourites.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(this);
        recycler_favourites.setLayoutManager(layoutManager);

        //swipe to delete
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback=new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recycler_favourites);

        loadfavourites();
    }

    private void loadfavourites() {

        adapter=new FavouriteAdapter(this,new Database(this).getAllFavouritesa(Common.currentUser.getPhone()));
        recycler_favourites.setAdapter(adapter);

    }

    @Override
    public void onSwipe(RecyclerView.ViewHolder viewHolder, int direction, int position) {

        String name=((FavouriteAdapter)recycler_favourites.getAdapter()).getItem(position).getHotelName();

        final Favourites deleteItem =((FavouriteAdapter)recycler_favourites.getAdapter()).getItem(viewHolder.getAdapterPosition());
        final int deleteIndex =viewHolder.getAdapterPosition();

        adapter.removeItem(viewHolder.getAdapterPosition());
        new Database(getBaseContext()).removeFromFavourites(deleteItem.getHotelId(), Common.currentUser.getPhone());


        Snackbar snackbar=Snackbar.make(rootlayout,name+"removed from favourites!",Snackbar.LENGTH_LONG);
        snackbar.setAction("UNDO", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                adapter.restoreItem(deleteItem,deleteIndex);
                new Database(getBaseContext()).addToFavourites(deleteItem);
            }
        });
        snackbar.setActionTextColor(Color.YELLOW);
        snackbar.show();
    }
}
