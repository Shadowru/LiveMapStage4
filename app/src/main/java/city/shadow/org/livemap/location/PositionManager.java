package city.shadow.org.livemap.location;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import static android.content.Context.LOCATION_SERVICE;

public class PositionManager implements LocationListener, SensorEventListener {

    private static final String TAG = "POSITIONMANAGER";
    private static final int AZIMUTH = 0;

    final private int intervalInSecs = 10;
    final private float deltaInMeters = 1;

    private final Context context;

    private LocationManager locationManager;

    private LocationListener locationListener;


    private Location sovietPalace;

    private final Object sensorMutex = new Object();

    private SensorManager sensorManager;

    private Sensor accelerometerSensor;
    private Sensor magneticFieldSensor;

    //TODO:Settings
    final private int sensorDelay = SensorManager.SENSOR_DELAY_NORMAL;
    final private float[] accelerometerValues = new float[3];
    final private float[] magneticFieldValues = new float[3];

    private final float[] rotationMatrix = new float[9];
    private final float[] orientationAngles = new float[3];

    public PositionManager(Context applicationContext) throws Exception {
        this.context = applicationContext;

        setLocationListener(this);

        initServices();

        enableListeners();

        sovietPalace = new Location("providername");
        sovietPalace.setLatitude(55.7446375d);
        sovietPalace.setLongitude(37.6033052d);

    }

    public Context getContext() {
        return context;
    }

    public void setLocationListener(LocationListener locationListener) {
        this.locationListener = locationListener;
    }

    public LocationListener getLocationListener() {
        return locationListener;
    }

    public LocationManager getLocationManager() {
        return locationManager;
    }

    private void initServices() throws Exception {

        locationManager = (LocationManager) getContext().getSystemService(LOCATION_SERVICE);

        sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);

        accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        if (accelerometerSensor == null || magneticFieldSensor == null) {
            throw new Exception("Bad sensor");
        }

    }

    public void enableListeners() {

        getLocationManager().requestLocationUpdates(LocationManager.GPS_PROVIDER,
                1000 * getIntervalInSecs(), getDeltaInMeters(), getLocationListener());

        getLocationManager().requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 1000 * getIntervalInSecs(), 1,
                getLocationListener());

        getSensorManager().registerListener(
                getSensorListener(),
                getAccelerometerSensor(),
                getSensorDelay()
        );

        getSensorManager().registerListener(
                getSensorListener(),
                getMagneticFieldSensor(),
                getSensorDelay()
        );
    }

    private SensorEventListener getSensorListener() {
        return this;
    }

    public void disableListeners() {
        getLocationManager().removeUpdates(getLocationListener());
        getSensorManager().unregisterListener(getSensorListener());
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location == null) {
            return;
        }
        Log.i(TAG, "Location : " + location.toString());

        calcBearing(location);

    }

    private void calcBearing(Location location) {

        final float bearingTo = location.bearingTo(sovietPalace);

        final float distanceTo = location.distanceTo(sovietPalace);

        Log.i(TAG, "BearingTo : " + bearingTo);
        Log.i(TAG, "DistanceTo : " + distanceTo);

        double bearing = Math.toDegrees(getAzimuth());

        Log.i(TAG, "Bearing :" + bearing);

        bearing = bearingTo - bearing;

        Log.i(TAG, "Azimuth :" + bearing);

    }

    private float getAzimuth() {
        synchronized (sensorMutex) {

            boolean isOk = SensorManager.getRotationMatrix(
                    rotationMatrix,
                    null,
                    accelerometerValues,
                    magneticFieldValues);

            if (!isOk) {
                return 0;
            }

            float[] outR = new float[9];
            SensorManager.remapCoordinateSystem(rotationMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, outR);

            SensorManager.getOrientation(outR, orientationAngles);

        }

        return orientationAngles[AZIMUTH];
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    public int getIntervalInSecs() {
        return intervalInSecs;
    }

    public float getDeltaInMeters() {
        return deltaInMeters;
    }

    public Sensor getAccelerometerSensor() {
        return accelerometerSensor;
    }

    public Sensor getMagneticFieldSensor() {
        return magneticFieldSensor;
    }

    public SensorManager getSensorManager() {
        return sensorManager;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event == null) {
            return;
        }

        if (event.sensor == getAccelerometerSensor()) {
            synchronized (sensorMutex) {
                setAccelerometerValues(event.values);
            }
        }
        if (event.sensor == getMagneticFieldSensor()) {
            synchronized (sensorMutex) {
                setMagneticFieldValues(event.values);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public int getSensorDelay() {
        return sensorDelay;
    }

    public void setAccelerometerValues(float[] accelerometerValues) {
        System.arraycopy(accelerometerValues, 0, this.accelerometerValues,
                0, this.accelerometerValues.length);
    }

    public void setMagneticFieldValues(float[] magneticFieldValues) {
        System.arraycopy(magneticFieldValues, 0, this.magneticFieldValues,
                0, this.magneticFieldValues.length);
    }
}
