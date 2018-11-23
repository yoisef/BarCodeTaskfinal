package automaticcallrecorder.phonerecorder.barcodeexample;

import automaticcallrecorder.phonerecorder.barcodeexample.Models.Rootmodel;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Endpoints {

    @GET("products?formatted=y&key=0bzvrusikq1rzpclz0o34s6dczd6f8")
    Call<Rootmodel> getbarcodedetails(@Query("barcode") String number);


}
