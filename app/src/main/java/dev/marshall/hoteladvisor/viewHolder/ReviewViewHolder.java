package dev.marshall.hoteladvisor.viewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;
import dev.marshall.hoteladvisor.Interface.ItemClickListener;
import dev.marshall.hoteladvisor.R;
import dev.marshall.hoteladvisor.common.Common;

/**
 * Created by Marshall on 24/03/2018.
 */

public class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener {
   public CircleImageView Reviewerimages;
   public TextView review,Reviewername,postdate,stayed,Asa;
    private ItemClickListener itemClickListener;

    public ReviewViewHolder(View itemView) {
        super(itemView);
        Reviewerimages=(CircleImageView)itemView.findViewById(R.id.reviewerimage);
        review=(TextView)itemView.findViewById(R.id.review_Show);
        Reviewername=(TextView)itemView.findViewById(R.id.reviewername);
        postdate=(TextView)itemView.findViewById(R.id.postdate);
        stayed=(TextView)itemView.findViewById(R.id.txtstayed);
        Asa=(TextView)itemView.findViewById(R.id.txtasa);

        itemView.setOnCreateContextMenuListener(this);

        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
      //  this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
       // itemClickListener.onClick(v,getAdapterPosition(),false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select action");
        menu.add(0,0,getAdapterPosition(), Common.DELETE);
        menu.add(0,1,getAdapterPosition(), Common.EDIT);
    }
}
