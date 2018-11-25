package automaticcallrecorder.phonerecorder.barcodeexample;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import automaticcallrecorder.phonerecorder.barcodeexample.Models.Product;
import automaticcallrecorder.phonerecorder.barcodeexample.Models.Rootmodel;
import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.media.MediaRecorder.VideoSource.CAMERA;
import static android.support.v7.widget.helper.ItemTouchHelper.Callback.getDefaultUIUtil;

public class MainActivity extends AppCompatActivity implements RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    TextView txtView, paybutton;
    ImageView myImageView;
    BarcodeDetector detector;
    Bitmap myBitmap;
    SurfaceView cameraView;
    CameraSource cameraSource;
    RecyclerView myrecycle;
    ImageView scan, barcodimg;
    Toolbar mytoolbar;
    Button enterbarcode;
    android.app.AlertDialog.Builder builder,builder1;
    android.app.AlertDialog alertDialog,alertDialog1;
    Call<Rootmodel> mcall;
    Recycleadapter mAdapter;
    List<Product> dbList;
    FirebaseDatabase database;
    DatabaseReference myRef;
    productmodel mymodel;
    TextView pricetotal;
    LinearLayout paylinear;
    int total;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbList = new ArrayList<>();

        requestPermission();


        // Write a message to the database
        pricetotal=findViewById(R.id.totalprice);
        paylinear=findViewById(R.id.paylayout);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("products");

        paybutton=findViewById(R.id.paybut);
        paylinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder1 = new android.app.AlertDialog.Builder(MainActivity.this);


                View view = LayoutInflater.from(MainActivity.this.getApplicationContext()).inflate(R.layout.payayout, null);
             TextView transtext=view.findViewById(R.id.transfer);
                builder1.setView(view);
                alertDialog1= builder1.create();
                alertDialog1.show();
                transtext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog1.cancel();
                        Toast toast=Toast.makeText(MainActivity.this,"Verifiying$Transfering the cart item... ",Toast.LENGTH_LONG);
                        toast.setGravity(Gravity.TOP|Gravity.CENTER,0,20);
                        toast.show();
                    }
                });

            }
        });



        mAdapter = new Recycleadapter(this);


        mytoolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mytoolbar);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        getSupportActionBar().setCustomView(R.layout.cutom_action_bar);
        View view = getSupportActionBar().getCustomView();

        enterbarcode = (Button) findViewById(R.id.barcodenumber);
        myrecycle = (RecyclerView) findViewById(R.id.productrecycle);
        myrecycle.setHasFixedSize(true);
        myrecycle.setLayoutManager(new LinearLayoutManager(this));
        myrecycle.setItemAnimator(new DefaultItemAnimator());
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(myrecycle);
        myrecycle.setAdapter(mAdapter);
        //add remove when swipe from recycle


        scan = view.findViewById(R.id.camerascan);
        barcodimg = view.findViewById(R.id.aboutus);

        barcodimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Aboutactivity.class));
            }
        });


        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MainActivity.this, Camera_activity.class);
                startActivityForResult(intent1, 0);

            }
        });

        enterbarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText myedit;
                TextView ok, cancel;

                builder = new android.app.AlertDialog.Builder(MainActivity.this);

                View myview = LayoutInflater.from(MainActivity.this.getApplicationContext()).inflate(R.layout.layoutenterbar, null);
                myedit = myview.findViewById(R.id.barcodedittext);
                ok = myview.findViewById(R.id.okk);
                cancel = myview.findViewById(R.id.cancell);
                builder.setView(myview);
                alertDialog = builder.create();
                alertDialog.show();


                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        OkHttpClient.Builder builderr = new OkHttpClient.Builder();

                        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                        builderr.addInterceptor(loggingInterceptor);


                        Retrofit retrofitt = new Retrofit.Builder()
                                .baseUrl("https://api.barcodelookup.com/v2/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .client(builderr.build())
                                .build();

                        final Endpoints myendpoints = retrofitt.create(Endpoints.class);

                        mcall = myendpoints.getbarcodedetails(myedit.getText().toString());
                        mcall.enqueue(new Callback<Rootmodel>() {
                            @Override
                            public void onResponse(Call<Rootmodel> call, Response<Rootmodel> response) {

                                if (response.isSuccessful()) {
                                    Product newproduct = response.body().getProducts().get(0);
                                    mymodel = new productmodel(newproduct.getProductName(), newproduct.getBarcodeNumber(), newproduct.getDescription(), newproduct.getImages().get(0), newproduct.getColor(), newproduct.getCategory());
                                    myRef.push().setValue(mymodel);

                                } else {
                                    mymodel = new productmodel(null, myedit.getText().toString(), null, null, null, null);
                                    myRef.push().setValue(mymodel);


                                }


                            }

                            @Override
                            public void onFailure(Call<Rootmodel> call, Throwable t) {
                                mymodel = new productmodel(null, myedit.getText().toString(), null, null, null, null);
                                myRef.push().setValue(mymodel);


                            }
                        });

                        alertDialog.cancel();
                    }

                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.cancel();

                    }
                });

            }
        });

        cameraView = (SurfaceView) findViewById(R.id.camera_view);





        }

    @Override
    protected void onResume() {
        super.onResume();


        overridePendingTransition(R.anim.downtocenter, R.anim.centertoup);
    }


    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.uptocenter,R.anim.centertodown);

    }


    private void requestPermission() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.CAMERA},
                1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_SHORT).show();

                    // main logic
                } else {
                    Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_SHORT).show();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                                != PackageManager.PERMISSION_GRANTED) {
                            showMessageOKCancel("You need to allow access permissions",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermission();
                                            }
                                        }
                                    });
                        }
                    }
                }
                break;
        }
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }



        @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Call<Rootmodel> mycall;

        if (requestCode == 0) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    final String barcodedata = String.valueOf(data.getStringExtra("open"));

                    OkHttpClient.Builder builderr = new OkHttpClient.Builder();

                    HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
                    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

                    builderr.addInterceptor(loggingInterceptor);


                    Retrofit retrofitt = new Retrofit.Builder()
                            .baseUrl("https://api.barcodelookup.com/v2/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .client(builderr.build())
                            .build();

                    final Endpoints myendpoints = retrofitt.create(Endpoints.class);

                    mycall = myendpoints.getbarcodedetails(barcodedata);
                    mycall.enqueue(new Callback<Rootmodel>() {
                        @Override
                        public void onResponse(Call<Rootmodel> call, Response<Rootmodel> response) {

                            if (response.isSuccessful()) {
                                Product newproduct = response.body().getProducts().get(0);
                                mymodel = new productmodel(newproduct.getProductName(), newproduct.getBarcodeNumber(), newproduct.getDescription(), newproduct.getImages().get(0), newproduct.getColor(), newproduct.getCategory());
                                myRef.push().setValue(mymodel);

                            } else {
                                mymodel = new productmodel(null, barcodedata, null, null, null, null);
                                myRef.push().setValue(mymodel);

                            }


                        }

                        @Override
                        public void onFailure(Call<Rootmodel> call, Throwable t) {

                            mymodel = new productmodel(null, barcodedata, null, null, null, null);
                            myRef.push().setValue(mymodel);


                        }
                    });
                    builder = new android.app.AlertDialog.Builder(MainActivity.this);
                    TextView doneit, scananotherr, cardinformation;

                    View myview = LayoutInflater.from(MainActivity.this.getApplicationContext()).inflate(R.layout.additemdialog, null);
                    doneit = myview.findViewById(R.id.Donee);
                    scananotherr = myview.findViewById(R.id.other);
                    cardinformation = myview.findViewById(R.id.cardinfo);
                    cardinformation.setText(String.valueOf(barcodedata));
                    doneit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.cancel();

                        }
                    });
                    scananotherr.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent1 = new Intent(MainActivity.this, Camera_activity.class);
                            startActivityForResult(intent1, 0);

                        }
                    });

                    builder.setView(myview);
                    alertDialog = builder.create();
                    alertDialog.show();


                } else {
                    Toast.makeText(MainActivity.this, "No barcode found", Toast.LENGTH_LONG).show();
                }


            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {


        if (viewHolder instanceof Recycleadapter.viewholder) {
            // get the removed item name to display it in snack bar
            List<String> myres=mAdapter.getKeys();
            myRef.child(myres.get(position)).removeValue();
            mAdapter.removeItem(position);







            }
        }


    }

















