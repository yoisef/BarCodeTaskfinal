package automaticcallrecorder.phonerecorder.barcodeexample;


import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.agilie.swipe2delete.SwipeToDeleteDelegate;
import com.agilie.swipe2delete.interfaces.ISwipeToDeleteAdapter;
import com.bumptech.glide.Glide;
import com.daimajia.swipe.SwipeLayout;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class Recycleadapter extends RecyclerView.Adapter<Recycleadapter.viewholder> {

    Context con;



    List<productmodel> mylist;
    DatabaseReference reference= FirebaseDatabase.getInstance().getReference("products");
    List<String> keys;




    List<String> prices;




    public Recycleadapter(Context context)
    {
        this.con=context;
        mylist=new ArrayList<>();
        keys=new ArrayList<>();
        prices=new ArrayList<>();




        notifyDataSetChanged();

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                productmodel model=dataSnapshot.getValue(productmodel.class);
                String key=dataSnapshot.getKey();
                String price=model.getPriceproduct();
                prices.add(price);
                keys.add(key);
                mylist.add(model);
                notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }


    @NonNull
    @Override
    public viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(con.getApplicationContext()).inflate(R.layout.rowrecycle,parent,false);
        viewholder vholder=new viewholder(view);
        return vholder;
    }

    @Override
    public void onBindViewHolder(@NonNull final viewholder holder,  int position) {

        holder.rowrecycle.setShowMode(SwipeLayout.ShowMode.LayDown);
        holder.rowrecycle.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onStartOpen(SwipeLayout layout) {



            }

            @Override
            public void onOpen(SwipeLayout layout) {

                holder.deleterowww.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int i=holder.getAdapterPosition();
                        reference.child(keys.get(i)).removeValue();
                        mylist.remove(i);
                        notifyItemRemoved(i);

                    }
                });
                Animation animation=AnimationUtils.loadAnimation(con,R.anim.notify);
                holder.xremove.startAnimation(animation);

            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onClose(final SwipeLayout layout) {



            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

            }
        });


        holder.productdetailss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                con.startActivity(new Intent(con , Productdetails.class));


            }
        });
        holder.removeimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.rowrecycle.open();
            }
        });


        holder.numberr.setText(mylist.get(position).numbermyproduct);


        if (mylist.get(position).namemyproduct != null) {
            holder.namee.setText(mylist.get(position).getNamemyproduct());
        }
        else
        {
            holder.namee.setText(con.getString(R.string.name));
        }
        if (mylist.get(position).imgmyproduct!=null)
        {
            Glide.with(con)
                    .load(mylist.get(position).getImgmyproduct())
                    .into(holder.productimage);
        }
        else {
            Glide.with(con)
                    .load(con.getResources().getDrawable(R.drawable.lipton))
                    .into(holder.productimage);
        }


    }

    @Override
    public int getItemCount() {


        return mylist.size();
    }




    class viewholder extends RecyclerView.ViewHolder{

        TextView namee , numberr , pricee ,deleterowww;
        ImageView productimage,removeimg,xremove;
        RelativeLayout removerow,productdetailss;
        SwipeLayout rowrecycle;


        public viewholder(View itemView) {
            super(itemView);

            namee=itemView.findViewById(R.id.nameproduct);
            numberr=itemView.findViewById(R.id.numberproduct);
            pricee=itemView.findViewById(R.id.itempricee);
            productimage=itemView.findViewById(R.id.productimg);
            removeimg=itemView.findViewById(R.id.remove);
            rowrecycle=itemView.findViewById(R.id.myrow);
            productdetailss=itemView.findViewById(R.id.productdetails);
            removerow=itemView.findViewById(R.id.background);
            deleterowww=itemView.findViewById(R.id.deleterow);
            xremove=itemView.findViewById(R.id.xsign);


        }
    }



    public List<productmodel> getMylist() {
        return mylist;
    }
    public List<String> getPrices() {
        return prices;
    }


    public void removeItem(int position) {
        mylist.remove(position);
        notifyItemRemoved(position);
    }
    public List<String> getKeys() {
        return keys;
    }



}
