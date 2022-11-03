package com.alviano.ugd89_kelas_d_10801

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import java.io.IOException

class MainActivity : AppCompatActivity() {
    private var mCamera: Camera? = null
    private var mCameraView: CameraView? = null
    lateinit var sensorStatusTV: TextView
    lateinit var proximitySensor: Sensor
    lateinit var sensorManager: SensorManager
    private var currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK

    var proximitySensorEventListener: SensorEventListener? = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

        }

        override fun onSensorChanged(event: SensorEvent) {
            if(event.sensor.type == Sensor.TYPE_PROXIMITY){
                if(event.values[0] == 0f){
                    rotateCamera()
                }else{
                    sensorStatusTV.text="<<<Away>>>"
                }
            }
        }
    }

    private fun rotateCamera(){
        mCamera?.release()
        if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }
        else {
            mCamera?.release()
            currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }
        try{
            mCamera = Camera.open(currentCameraId)
        } catch (e: Exception){
            Log.d("Error","Failed to get Camera" + e.message)
        }
        if (mCamera != null) {
            mCameraView = CameraView(this, mCamera!!)
            val camera_view = findViewById<View>(R.id.FLCamera) as FrameLayout
            camera_view.addView(mCameraView)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        try{
            mCamera = Camera.open()
        } catch (e: Exception){
            Log.d("Error","Failed to get Camera" + e.message)
        }
        if (mCamera != null){
            mCameraView = CameraView(this, mCamera!!)
            val camera_view = findViewById<View>(R.id.FLCamera) as FrameLayout
            camera_view.addView(mCameraView)

            sensorStatusTV = findViewById(R.id.idTVSensorStatus)

            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

            proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)

            if(proximitySensor == null) {
                Toast.makeText(this,"No Proximity sensor found in device..", Toast.LENGTH_SHORT).show()
                finish()
            }else{
                sensorManager.registerListener(
                    proximitySensorEventListener,
                    proximitySensor,
                    SensorManager.SENSOR_DELAY_NORMAL
                )
            }
        }
        @SuppressLint("MissingInflatedID", "LocalSuppress") val imageClose =
            findViewById<View>(R.id.imgClose) as ImageButton
        imageClose.setOnClickListener { view: View? -> System.exit(0)}
    }
}