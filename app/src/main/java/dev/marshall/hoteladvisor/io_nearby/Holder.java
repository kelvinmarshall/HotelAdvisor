/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.marshall.hoteladvisor.io_nearby;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import dev.marshall.hoteladvisor.Interface.ItemClickListener;
import dev.marshall.hoteladvisor.R;
import dev.marshall.hoteladvisor.model.HotelSearch;

import static android.widget.Toast.LENGTH_SHORT;


public class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public TextView txtMenuName,txtLocation,txtDistance,txtPrice;
    public ImageView imageView,favorite;
    private ItemClickListener itemClickListener;
    public Button booknow;


    public Holder(View itemView) {
        super(itemView);

        txtMenuName = (TextView)itemView.findViewById(R.id.menu_name);
        txtLocation = (TextView)itemView.findViewById(R.id.location_name);
        txtPrice = (TextView)itemView.findViewById(R.id.price);
        imageView = (ImageView)itemView.findViewById(R.id.menu_image);
        favorite = (ImageView)itemView.findViewById(R.id.favorite);
        booknow=(Button)itemView.findViewById(R.id.btnbooknow);

        itemView.setOnClickListener(Holder.this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);

    }
}
