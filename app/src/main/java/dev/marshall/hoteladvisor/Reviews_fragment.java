package dev.marshall.hoteladvisor;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.Constraints;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.rengwuxian.materialedittext.MaterialEditText;


import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import dev.marshall.hoteladvisor.common.Common;
import dev.marshall.hoteladvisor.model.Reviews;
import dev.marshall.hoteladvisor.viewHolder.ReviewViewHolder;

public class Reviews_fragment extends Fragment {

    int color;

    public Reviews_fragment() {
    }

    @SuppressLint("ValidFragment")
    public Reviews_fragment(int color) {
        this.color = color;
    }

    RecyclerView recycler_review;
    RecyclerView.LayoutManager layoutManager;
    String HotelId="";

    TextView username,availablecomment,postdate,dislike,like;
    CircleImageView Userimage;
    MaterialEditText Edtcomment;
    MaterialSpinner stayed,asa;
    Button Addcomment;

    CardView reviewlayout;
    FirebaseDatabase database;
    DatabaseReference reviews;

    FirebaseRecyclerAdapter<Reviews,ReviewViewHolder> review_adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_reviews_fragment, container, false);

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //firebase
        database=FirebaseDatabase.getInstance();
        reviews=database.getReference("Reviews");

        HotelId=HotelDetails.HotelId;
        //load reviews
        recycler_review=(RecyclerView)view.findViewById(R.id.recycler_reviews);
        recycler_review.setHasFixedSize(true);
        layoutManager= new LinearLayoutManager(getActivity());
        recycler_review.setLayoutManager(layoutManager);
        reviewlayout=(CardView) view.findViewById(R.id.reviewlayout);

        //add review view
        stayed=(MaterialSpinner) view.findViewById(R.id.spinnerstayed);
        stayed.setItems("1 Night","Days","1 Week","Weeks","1 Month","Months");
        asa=(MaterialSpinner)view.findViewById(R.id.asa);
        asa.setItems("Individual","Family","Partner","Committee","Business Person");
        postdate=(TextView)view.findViewById(R.id.postdate);
        availablecomment=(TextView)view.findViewById(R.id.available);
        Edtcomment=(MaterialEditText)view.findViewById(R.id.edtreview) ;
        username=(TextView)view.findViewById(R.id.username);
        username.setText(Common.currentUser.getName());
        Addcomment=(Button)view.findViewById(R.id.addreview);
        Userimage=(CircleImageView)view.findViewById(R.id.userimage);
        dislike=(TextView)view.findViewById(R.id.dislike);
        like=(TextView)view.findViewById(R.id.like);

        //set image
        GlideApp.with(this).load(Common.currentUser.getImage())
                .placeholder(R.drawable.ic_person_outline_black_24dp)
                .into(Userimage);


        Addcomment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(Edtcomment.getText().toString()))
                {
                    Uploadreview();
                }else {
                    new SweetAlertDialog(getActivity(),SweetAlertDialog.NORMAL_TYPE)
                            .setContentText("Please you cannot send a blank review.Please write something")
                            .show();
                }
            }
        });

        if (HotelId!=null) {
            loadreviews(HotelId);
            reviews.child(HotelId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    availablecomment.setText(dataSnapshot.getChildrenCount() + " available");
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
        }

    }

    private void Uploadreview() {
        Reviews review=new Reviews(
                Common.currentUser.getPhone(),
                Common.currentUser.getName(),
                Edtcomment.getText().toString(),
                Common.currentUser.getImage(),
                Common.convertStayed(String.valueOf(stayed.getSelectedIndex())),
                Common.convertAsal(String.valueOf(asa.getSelectedIndex()))

        );
        String hotelname_reviewed= String.valueOf(System.currentTimeMillis());
        reviews.child(HotelId).child(hotelname_reviewed).setValue(review)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Edtcomment.clearComposingText();
                Snackbar.make(reviewlayout,"Your review was submitted successfuly",Snackbar.LENGTH_SHORT)
                        .show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Error sending", Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void loadreviews(String hotelId) {
        review_adapter= new FirebaseRecyclerAdapter<Reviews, ReviewViewHolder>(
                Reviews.class,
                R.layout.user_review_layout,
                ReviewViewHolder.class
                ,reviews.child(HotelId)
        ) {
            @Override
            protected void populateViewHolder(ReviewViewHolder viewHolder, Reviews model, int position) {
                viewHolder.Reviewername.setText(model.getUsername());
                viewHolder.review.setText(model.getReview());
                viewHolder.stayed.setText(model.getStayed());
                viewHolder.Asa.setText(model.getAs_a());
                viewHolder.postdate.setText(Common.getdate(Long.parseLong(review_adapter.getRef(position).getKey())));
                GlideApp.with(getActivity()).load(model.getImage())
                        .placeholder(R.drawable.ic_person_outline_black_24dp)
                        .into(viewHolder.Reviewerimages);
                final Reviews reviewitem=model;
            }
        };
        recycler_review.setAdapter(review_adapter);
        review_adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.EDIT))
        {
            showEdithotelDialog(review_adapter.getRef(item.getOrder()).getKey(),review_adapter.getItem(item.getOrder()));
        }
        else if (item.getTitle().equals(Common.DELETE))
        {
            deletereview(review_adapter.getRef(item.getOrder()).getKey());
        }

        return true;
    }


    private void showEdithotelDialog(String key, Reviews item) {

    }

    private void deletereview(String key) {
        reviews.child(HotelId).child(key).removeValue();
    }

}
