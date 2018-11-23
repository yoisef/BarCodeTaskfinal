package automaticcallrecorder.phonerecorder.barcodeexample;


import android.content.Context;
import android.content.Intent;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class Recycleadapter extends RecyclerView.Adapter<Recycleadapter.viewholder> {

    Context con;
    productsdatabase database;
    List<productmodel> mylist;
    DatabaseReference reference= FirebaseDatabase.getInstance().getReference("products");
    List<String> keys;




    public Recycleadapter(Context context)
    {
        this.con=context;
        mylist=new ArrayList<>();
        database=new productsdatabase(context);
        keys=new ArrayList<>();

        notifyDataSetChanged();

        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                productmodel model=dataSnapshot.getValue(productmodel.class);
                String key=dataSnapshot.getKey();
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
    public List<String> getKeys() {
        return keys;
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


        holder.rowrecycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                con.startActivity(new Intent(con , Productdetails.class));


            }
        });
        holder.deleterowww.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i=holder.getAdapterPosition();
                reference.child(keys.get(i)).removeValue();
                mylist.remove(i);
                notifyItemRemoved(i);

            }
        });

        holder.numberr.setText(mylist.get(position).numbermyproduct);


        if (mylist.get(position).namemyproduct != null) {
            holder.namee.setText(mylist.get(position).namemyproduct);
        }
        else
        {
            holder.namee.setText(con.getString(R.string.name));
        }
        if (mylist.get(position).imgmyproduct!=null)
        {
            Glide.with(con)
                    .load(mylist.get(position).imgmyproduct)
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
        ImageView productimage,removeimg;
        RelativeLayout rowrecycle,removerow;


        public viewholder(View itemView) {
            super(itemView);

            namee=itemView.findViewById(R.id.nameproduct);
            numberr=itemView.findViewById(R.id.numberproduct);
            pricee=itemView.findViewById(R.id.productprice);
            productimage=itemView.findViewById(R.id.productimg);
            removeimg=itemView.findViewById(R.id.remove);
            rowrecycle=itemView.findViewById(R.id.myrow);
            removerow=itemView.findViewById(R.id.background);
            deleterowww=itemView.findViewById(R.id.deleterow);

        }
    }


    public void removeItem(int position) {
        mylist.remove(position);
        notifyItemRemoved(position);
    }


}
