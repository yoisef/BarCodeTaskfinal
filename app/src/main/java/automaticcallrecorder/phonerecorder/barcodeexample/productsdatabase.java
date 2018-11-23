package automaticcallrecorder.phonerecorder.barcodeexample;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import automaticcallrecorder.phonerecorder.barcodeexample.Models.Product;


public class productsdatabase extends SQLiteOpenHelper {

    public static final String Databasename="barcodedata.db";
    public static final String Tablename1="products";



    public static final String columna="ID";
    public static final String columnb="barcodenum";
    public static final String columnc="productname";
    public static final String columnd="category";
    public static final String columne="brand";
    public static final String columnf="color";
    public static final String columng="description";
    public static final String columnh="img";



    public productsdatabase(Context context) {
        super(context, Databasename, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String example= "CREATE TABLE " + Tablename1  + " (ID INTEGER PRIMARY KEY AUTOINCREMENT,barcodenum TEXT ,productname TEXT ,category TEXT ,brand TEXT ,color TEXT ,description TEXT ,img TEXT);";


        db.execSQL(example);




    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+ Tablename1);

        onCreate(db);

    }


    public Boolean insertdataforproduct(String barnumber , String prodname ,String cate,String brandd ,String color , String description ,String myimg)
    {

        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(columnb,barnumber);
        contentValues.put(columnc,prodname);
        contentValues.put(columnd,cate);
        contentValues.put(columne,brandd);
        contentValues.put(columnf,color);
        contentValues.put(columng,description);
        contentValues.put(columnh,myimg);


        long result=  db.insert(Tablename1,null,contentValues);

        if(result == -1)
        {
            return false;

        }
        else {
            return true;
        }
    }


    public List<Product> getdataforproduct()
    {
        SQLiteDatabase db=this.getReadableDatabase();
        String[] mycolumns={
                columnb,
                columnc,
                columnd,
                columne,
                columnf,
                columng,
                columnh
        };

        // Filter results WHERE "title" = 'My Title'


        // How you want the results sorted in the resulting Cursor
        String sortOrder =
                columnf + " ASC";


        Cursor cursor = db.query(
                Tablename1,   // The table to query
                mycolumns,             // The array of columns to return (pass null to get all)
                null ,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null              // The sort order
        );

        List<Product> itemIds = new ArrayList<>();
        while(cursor.moveToNext()) {
            int index=cursor.getColumnIndexOrThrow(columnb);
            String barn=cursor.getString(index);
            int indexx=cursor.getColumnIndexOrThrow(columnc);
            String prn=cursor.getString(indexx);
            int indexxx=cursor.getColumnIndexOrThrow(columnd);
            String cat=cursor.getString(indexxx);
            int indexxxx=cursor.getColumnIndexOrThrow(columne);
            String bra=cursor.getString(indexxxx);
            int indexxxxx=cursor.getColumnIndexOrThrow(columnf);
            String colo=cursor.getString(indexxxxx);
            int a=cursor.getColumnIndexOrThrow(columng);
            String desc=cursor.getString(a);
            int b=cursor.getColumnIndexOrThrow(columnh);
            List<String> img=new ArrayList<>();
            img.add(  cursor.getString(b));
            Product myprod=new Product();
            myprod.setBarcodeNumber(barn);
            myprod.setProductName(prn);
            myprod.setCategory(cat);
            myprod.setBrand(bra);
            myprod.setColor(colo);
            myprod.setDescription(desc);
            myprod.setImages(img);
            itemIds.add(myprod);

        }
        cursor.close();

        return itemIds;
    }


}
