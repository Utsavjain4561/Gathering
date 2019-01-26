package project.hack36.hack362k19;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class CleanerMainActivity extends AppCompatActivity implements OnMapReadyCallback{

    GoogleMap map = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cleaner_main);

        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);

        if(RequestProcessStatus.isProcessed()==false){

            mapFragment.getView().setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map=googleMap;
    }
}
