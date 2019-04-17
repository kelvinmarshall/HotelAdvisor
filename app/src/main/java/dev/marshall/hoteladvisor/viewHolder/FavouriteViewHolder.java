package dev.marshall.hoteladvisor.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import dev.marshall.hoteladvisor.Interface.ItemClickListener;
import dev.marshall.hoteladvisor.R;

/**
 * Created by Marshall on 08/04/2018.
 */

public class FavouriteViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txtMenuName,txtLocation,txtDistance,txtPrice;
    public ImageView imageView,favorite;
    private ItemClickListener itemClickListener;
    public Button booknow;

    public RelativeLayout view_background;
    public LinearLayout view_foreground;


    public FavouriteViewHolder(View itemView) {
        super(itemView);

        txtMenuName = (TextView)itemView.findViewById(R.id.menu_name);
        txtLocation = (TextView)itemView.findViewById(R.id.location_name);
        txtPrice = (TextView)itemView.findViewById(R.id.price);
        imageView = (ImageView)itemView.findViewById(R.id.hotelfavourite);
        booknow=(Button)itemView.findViewById(R.id.btnbooknow);
        view_background=(RelativeLayout)itemView.findViewById(R.id.view_background);
        view_foreground=(LinearLayout) itemView.findViewById(R.id.View_Foreground);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);

    }
}

