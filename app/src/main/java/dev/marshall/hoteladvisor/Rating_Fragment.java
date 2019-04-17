package dev.marshall.hoteladvisor;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import dev.marshall.hoteladvisor.common.Common;
import dev.marshall.hoteladvisor.model.Rating;

import static dev.marshall.hoteladvisor.HotelDetails.HotelId;

public class Rating_Fragment extends Fragment {

    int color;

    public Rating_Fragment() {
    }

    @SuppressLint("ValidFragment")
    public Rating_Fragment(int color) {
        this.color = color;
    }

    CardView rootLayout;

    RatingBar security,staff,cleanliness,accessbility,amenities,comfort,value;
    Button writereview,submitrating;

    FirebaseDatabase database;
    DatabaseReference rate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.activity_rating__fragment, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final String HotelId = HotelDetails.HotelId;
        database=FirebaseDatabase.getInstance();
        rate=database.getReference("Ratings").child(HotelId);

        rootLayout=(CardView)view.findViewById(R.id.container);
        security=(RatingBar)view.findViewById(R.id.security);
        staff=(RatingBar)view.findViewById(R.id.staff);
        cleanliness=(RatingBar)view.findViewById(R.id.cleanlimess);
        accessbility=(RatingBar)view.findViewById(R.id.accessbility);
        amenities=(RatingBar)view.findViewById(R.id.amenities);
        comfort=(RatingBar)view.findViewById(R.id.comfort);
        value=(RatingBar)view.findViewById(R.id.value);

        writereview=(Button)view.findViewById(R.id.writereview);
        submitrating=(Button)view.findViewById(R.id.submitrate);
        final LoadFragment loadFragmentObj = new LoadFragment(getFragmentManager());

        writereview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                loadFragmentObj.initializeFragment(new Reviews_fragment());
            }
        });
        submitrating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Submitrating();
            }
        });

    }

    private void Submitrating() {

        final Rating rating = new Rating(
                HotelId,
                String.valueOf(security.getRating()),
                String.valueOf(staff.getRating()),
                String.valueOf(accessbility.getRating()),
                String.valueOf(amenities.getRating()),
                String.valueOf(comfort.getRating()),
                String.valueOf(cleanliness.getRating()),
                String.valueOf(value.getRating())
        );
        rate.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                rate.child(Common.currentUser.getPhone()).setValue(rating).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Snackbar.make(rootLayout,"Hotel ratings successfully submited",Snackbar.LENGTH_SHORT)
                                .show();
                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
