package city.shadow.org.livemapstage4;

import android.Manifest;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;

import com.google.vr.sdk.base.AndroidCompat;
import com.google.vr.sdk.base.GvrActivity;
import com.google.vr.sdk.base.GvrView;

import city.shadow.org.livemap.location.PositionManager;
import city.shadow.org.livemap.vr.impl.DemoRender;
import city.shadow.org.livemap.vr.intf.CityRender;

public class MainActivity extends GvrActivity {

    public static final String TAG = "GvrActivity";

    private static final int PERMISSIONS_REQUEST_READ_POSITION = 120;

    private GvrView gvrView = null;
    private PositionManager positionManager = null;
    private boolean intialized = false;

    @Override
    public GvrView getGvrView() {
        return gvrView;
    }

    public PositionManager getPositionManager() {
        return positionManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //TODO:Settings
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        initManagers();

    }

    private void initManagers() {

        initVR();

       /*
        if (checkPermissions()) {
            initPositionManager();

            setPaused(false);
        } else {
            //initDummyVR();
        }
        */
    }

    private void initDummyVR() {
        setContentView(R.layout.dummy_vr);
    }

    private boolean checkPermissions() {

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                ||
                ContextCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    PERMISSIONS_REQUEST_READ_POSITION);

            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_POSITION:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                        ) {

                    initManagers();

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
        }

    }

    private void initPositionManager() {

        try {
            positionManager = new PositionManager(getApplicationContext());
        } catch (Exception e) {
            Log.e(TAG, "Position Manager error", e);
        }
    }

    private void initVR() {
        gvrView = (GvrView) findViewById(R.id.gvr_view);
        this.setGvrView(gvrView);

        //TODO:Settings switch
        gvrView.setStereoModeEnabled(true);
        gvrView.setTransitionViewEnabled(false);

        //8 bite per color, 16 bit depth
        gvrView.setEGLConfigChooser(8, 8, 8, 8, 16, 8);
        gvrView.setEGLContextClientVersion(2);

        //Init render
        CityRender cityRender = new DemoRender(this);

        gvrView.setRenderer(cityRender);

        //Dayream improvement
        if (gvrView.setAsyncReprojectionEnabled(true)) {
            // Async reprojection decouples the app framerate from the display framerate,
            // allowing immersive interaction even at the throttled clockrates set by
            // sustained performance mode.
            AndroidCompat.setSustainedPerformanceMode(this, true);
        }

    }


    @Override
    protected void onPause() {
        super.onPause();

        if (!isPaused()) {
            if (getPositionManager() != null) {
                getPositionManager().disableListeners();
            }
            setPaused(true);
        }
//        if(gvrView != null)
//        {
//            gvrView.shutdown();
//        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        if (isPaused()) {
            if (getPositionManager() != null) {
                getPositionManager().enableListeners();
            }
            setPaused(false);
        }
    }

    public boolean isPaused() {
        return intialized;
    }

    public void setPaused(boolean intialized) {
        this.intialized = intialized;
    }
}
