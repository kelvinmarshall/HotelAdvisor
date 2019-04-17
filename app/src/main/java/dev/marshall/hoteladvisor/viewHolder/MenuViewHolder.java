package dev.marshall.hoteladvisor.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import dev.marshall.hoteladvisor.Interface.ItemClickListener;
import dev.marshall.hoteladvisor.R;

/**
 * Created by Marshall on 27/01/2018.
 */

public  class MenuViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    public TextView txtMenuName,txtLocation,txtDistance,txtPrice;
    public ImageView imageView,favorite;
    private ItemClickListener itemClickListener;
    public Button booknow;


    public MenuViewHolder(View itemView) {
        super(itemView);

        txtMenuName = (TextView)itemView.findViewById(R.id.menu_name);
        txtLocation = (TextView)itemView.findViewById(R.id.location_name);
        txtDistance = (TextView)itemView.findViewById(R.id.distance);
        txtPrice = (TextView)itemView.findViewById(R.id.price);
        imageView = (ImageView)itemView.findViewById(R.id.menu_image);
        favorite = (ImageView)itemView.findViewById(R.id.favorite);
        booknow=(Button)itemView.findViewById(R.id.btnbooknow);

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

