package dev.marshall.hoteladvisor;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dev.marshall.hoteladvisor.model.Rooms;
import dev.marshall.hoteladvisor.viewHolder.RoomViewHolder;

import static dev.marshall.hoteladvisor.HotelDetails.HotelId;

public class Rooms_Fragment extends Fragment {

    int color;

    public Rooms_Fragment() {
    }

    @SuppressLint("ValidFragment")
    public Rooms_Fragment(int color) {
        this.color = color;
    }

    RecyclerView recyclerrooms;
    RecyclerView.LayoutManager layoutManager;



    FirebaseDatabase database;
    DatabaseReference rooms;

    FirebaseRecyclerAdapter<Rooms, RoomViewHolder> room_adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_rooms__fragment, container, false);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        database = FirebaseDatabase.getInstance();
        rooms = database.getReference("Rooms");

        recyclerrooms = (RecyclerView) view.findViewById(R.id.recycler_room);
        recyclerrooms.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerrooms.setLayoutManager(layoutManager);

        String HotelId = HotelDetails.HotelId;

        Loadrooms();

    }

    private void Loadrooms() {
        room_adapter = new FirebaseRecyclerAdapter<Rooms, RoomViewHolder>(
                Rooms.class,
                R.layout.rooms,
                RoomViewHolder.class
                , rooms.child(HotelId)
        ) {
            @Override
            protected void populateViewHolder(RoomViewHolder viewHolder, Rooms model, int position) {

                viewHolder.roomprice.setText("KES "+model.getRooprice());
                viewHolder.roomtype.setText(model.getRoomtype());
                viewHolder.capacity.setText(model.getCapacity());
                GlideApp.with(getActivity()).load(model.getImage())
                        .placeholder(R.drawable.ic_local_hotel_black_24dp)
                        .into(viewHolder.imageroom);
                if (!model.getBreakfast().equals("true")) {
                    viewHolder.breakfast.setVisibility(View.GONE);
                }
                if (!model.getLunch().equals("true")) {
                    viewHolder.lunch.setVisibility(View.GONE);
                }
                if (!model.getDinner().equals("true")) {
                    viewHolder.dinner.setVisibility(View.GONE);
                }
                if (!model.getBooknow().equals("true")) {
                    viewHolder.book.setVisibility(View.GONE);
                }
                if (!model.getCancelation().equals("true")) {
                    viewHolder.cancel.setVisibility(View.GONE);
                }
                if (!model.getHotshower().equals("true")) {
                    viewHolder.hot.setVisibility(View.GONE);
                }
                if (!model.getBathtub().equals("true")) {
                    viewHolder.bath.setVisibility(View.GONE);
                }
                if (!model.getTv().equals("true")) {
                    viewHolder.tv.setVisibility(View.GONE);
                }


                final Rooms roomitem = model;

            }
        };
        recyclerrooms.setAdapter(room_adapter);
        room_adapter.notifyDataSetChanged();
    }
}

