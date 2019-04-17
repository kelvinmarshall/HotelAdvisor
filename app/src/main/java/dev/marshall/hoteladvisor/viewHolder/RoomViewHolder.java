package dev.marshall.hoteladvisor.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import dev.marshall.hoteladvisor.Interface.ItemClickListener;
import dev.marshall.hoteladvisor.R;

/**
 * Created by Marshall on 02/04/2018.
 */

public class RoomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView dinner,lunch,breakfast,bath,tv,hot,book,cancel,roomtype,roomprice,capacity;
    public ImageView imageroom;


    public RoomViewHolder(View itemView) {
        super(itemView);

        imageroom=(ImageView)itemView.findViewById(R.id.room_image);
        roomprice=(TextView)itemView.findViewById(R.id.room_price) ;
        roomtype=(TextView)itemView.findViewById(R.id.room_type) ;
        capacity=(TextView)itemView.findViewById(R.id.capacity) ;

        dinner=(TextView)itemView.findViewById(R.id.dinner);
        lunch=(TextView)itemView.findViewById(R.id.lunch);
        breakfast=(TextView)itemView.findViewById(R.id.breakfast);
        bath=(TextView)itemView.findViewById(R.id.bath);
        tv=(TextView)itemView.findViewById(R.id.tv);
        hot=(TextView)itemView.findViewById(R.id.hot);
        book=(TextView)itemView.findViewById(R.id.book);
        cancel=(TextView)itemView.findViewById(R.id.cancel);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        //  this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        // itemClickListener.onClick(v,getAdapterPosition(),false);
    }

}
