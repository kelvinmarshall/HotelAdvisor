package dev.marshall.hoteladvisor.viewHolder;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

import dev.marshall.hoteladvisor.BookingSite;
import dev.marshall.hoteladvisor.Home;
import dev.marshall.hoteladvisor.HotelDetails;
import dev.marshall.hoteladvisor.Interface.ItemClickListener;
import dev.marshall.hoteladvisor.R;
import dev.marshall.hoteladvisor.model.Favourites;

/**
 * Created by Marshall on 08/04/2018.
 */

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteViewHolder> {

    private Context context;
    private List<Favourites>favouritesList;

    public FavouriteAdapter(Context context, List<Favourites> favouritesList) {
        this.context = context;
        this.favouritesList = favouritesList;
    }

    @NonNull
    @Override
    public FavouriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context)
                .inflate(R.layout.favourite_layout,parent,false);
        return new FavouriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteViewHolder holder, final int position) {
        holder.txtMenuName.setText(favouritesList.get(position).getHotelName());
        holder.txtLocation.setText(favouritesList.get(position).getHotelLocation());
        holder.txtPrice.setText(String.format("KES %s",favouritesList.get(position).getHotelPrice()));
        Picasso.with(context).load(favouritesList.get(position).getHotelImage())
                .into(holder.imageView);
       // Quick booking

        holder.booknow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent booksite=new Intent(context,BookingSite.class);
                booksite.putExtra("hotelId",favouritesList.get(position).getHotelId());//send hotel Id to new activity
                context.startActivity(booksite);
            }
        });
        final Favourites local=favouritesList.get(position);

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent hoteldetail=new Intent(context,HotelDetails.class);
                hoteldetail.putExtra("hotelId",favouritesList.get(position).getHotelId());//send hotel Id to new activity
                context.startActivity(hoteldetail);
            }
        });
    }
    @Override
    public int getItemCount() {
        return favouritesList.size();
    }
    public void removeItem(int position)
    {
        favouritesList.remove(position);
        notifyItemRemoved(position);
    }
    public void restoreItem(Favourites item,int position)
    {
        favouritesList.add(position,item);
        notifyItemInserted(position);
    }

    public Favourites getItem(int position)
    {
        return  favouritesList.get(position);
    }
}
