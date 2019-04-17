package dev.marshall.hoteladvisor.io_nearby;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.algolia.search.saas.CompletionHandler;
import com.squareup.picasso.Picasso;

import java.util.List;

import dev.marshall.hoteladvisor.BookingSite;
import dev.marshall.hoteladvisor.HotelDetails;
import dev.marshall.hoteladvisor.Interface.ItemClickListener;
import dev.marshall.hoteladvisor.R;
import dev.marshall.hoteladvisor.model.HotelSearch;

/**
 * Created by Marshall on 12/04/2018.
 */

public class NearbyAdapter extends RecyclerView.Adapter<Holder>  {
    private Context context;
    private List<HotelSearch> hotels;

    public NearbyAdapter(Context context, List<HotelSearch> hotels) {
        this.context = context;
        this.hotels = hotels;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context)
                .inflate(R.layout.nearby_list,parent,false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, final int position) {

        holder.txtMenuName.setText(hotels.get(position).getName());
        holder.txtLocation.setText(hotels.get(position).getLocation());
        holder.txtPrice.setText(String.format("KES %s",hotels.get(position).getPrice()));
        Picasso.with(context).load(hotels.get(position).getImage())
                .into(holder.imageView);
        // Quick booking

        holder.booknow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent booksite=new Intent(context,BookingSite.class);
                booksite.putExtra("hotelId",hotels.get(position).getObjectID());//send hotel Id to new activity
                context.startActivity(booksite);
            }
        });
        final HotelSearch local=hotels.get(position);

        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                Intent hoteldetail=new Intent(context,HotelDetails.class);
                hoteldetail.putExtra("hotelId",hotels.get(position).getObjectID());//send hotel Id to new activity
                context.startActivity(hoteldetail);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hotels.size();
    }

}
