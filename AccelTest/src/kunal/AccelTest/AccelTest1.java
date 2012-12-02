package kunal.AccelTest;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

public class AccelTest1 extends Activity {
	
	SensorManager accman,compman;
	TextView txt,txt1,txt2,txt3,txt4;
	double val1,val2,initval,distance,gravity,velocity,prevAccel,preval;
	float prevX,prevY,prevZ;
	long preTime,stepDistance;
	boolean isFirst;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        System.out.println("");
        isFirst = true;
        txt = (TextView) findViewById(R.id.txt);
        txt1 = (TextView) findViewById(R.id.txt1);
        txt2 = (TextView) findViewById(R.id.txt2);
        txt3 = (TextView) findViewById(R.id.txt3);
        txt4 = (TextView) findViewById(R.id.text12);
        accman = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        compman = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        List<Sensor> mySens = accman.getSensorList(Sensor.TYPE_ALL);
        for(Sensor sensu : mySens)
        {
        	if(sensu.getType() == Sensor.TYPE_ACCELEROMETER)
        		accman.registerListener(mySensorEvents, sensu , SensorManager.SENSOR_DELAY_UI);
        	if(sensu.getType() == Sensor.TYPE_PROXIMITY)
        		compman.registerListener(mySensorEvents, sensu , SensorManager.SENSOR_DELAY_NORMAL);
        }
//        if(mySens.size()>0)
//        {
//        	accman.registerListener(mySensorEvents, mySens.get(0), SensorManager.SENSOR_DELAY_NORMAL);
//        }
    }
    
    SensorEventListener mySensorEvents = new SensorEventListener() {
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			if(event.accuracy != SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM)
			{
				if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER)
				{
					// ***** Stride Length Measurment ***************//
					boolean isbounce = bounceStep(event.values[0], event.values[1], event.values[2]);
					if(isbounce)
					{
						System.out.println("User Step up..........:::::::::::::>>>>>>>>>>>>");
						stepDistance += 30;
						txt2.setText(""+(stepDistance * 0.01)+" Meter");
					}
					else
						System.out.println("User Step down..........:::::::::::::>>>>>>>>>>>>");
					prevX = event.values[0];
					prevY = event.values[1];
					prevZ = event.values[2];
					// ************************************************************
					
					// Length Measurment by Acceleration and Velocity....
					if(event.values[2] > (prevAccel+0.612) || event.values[2]< (prevAccel-0.612))
					{
						System.out.println("Accel Data  :: " +(event.values[2]-preval));
						preval = event.values[2];
						double theValue,alpha;
						gravity = 0.0;
						alpha = 0.8;
						gravity = alpha * gravity + (1 - alpha) * event.values[2];
						theValue = event.values[2] - gravity;
						double time = ((double)(event.timestamp-preTime)/1000000000);
						System.out.println("Time Millis :: "+(event.timestamp-preTime));
						System.out.println("The time elapsed :: "+time);						
						if(isFirst)
						{
							initval = (((0)*(0.2*0.2))/2);
							val2 = initval;
							velocity = 0.00000 + theValue * 0;
							distance = velocity * 0;
							preTime = event.timestamp;
							isFirst = false;
						}
						else
						{
							val1 = (((theValue)*(0.2*0.2))/2);
							txt1.setText(""+(theValue));
								velocity = velocity + time * theValue;
								distance = (velocity * time);
	//						if(val1>0)
	//							distance = distance + (val1);
	//						else
	//							distance = distance - (val1);
							txt.setText(""+distance);
							val2 = val1;
						}
					}
					preTime = event.timestamp;
					prevAccel = event.values[2];
				}
				else if(event.sensor.getType() == Sensor.TYPE_PROXIMITY)
				{
					txt4.setText(""+event.values[0]+"::"+event.sensor.getMaximumRange());
				}
			}
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}
	};
	
	public boolean bounceStep(float AccX,float AccY, float AccZ)
	{
		 boolean bounce = false;
		 float dot,a,b;
		 dot = (prevX * AccX) + (prevY * AccY) + (prevZ * AccZ);
		 a = (float) Math.abs(Math.sqrt(prevX * prevX + prevY * prevY + prevZ * prevZ));
		 
		 b = (float) Math.abs(Math.sqrt(AccX * AccX + AccY * AccY + AccZ * AccZ));

		 dot /= (a * b);
		 txt3.setText(""+dot);
		 if (dot  < 0.9969 && dot > 0.90) // bounce
			 bounce = true; 
		 else
			 bounce = false;
		 return bounce;
		 
	}

//	@Override
//	protected void onDestroy() {
//		accman.unregisterListener(mySensorEvents);
//		compman.unregisterListener(mySensorEvents);
//		super.onDestroy();
//	}

//	@Override
//	protected void onPause() {
//		accman.unregisterListener(mySensorEvents);
//		compman.unregisterListener(mySensorEvents);
//		super.onPause();
//	}
//
	@Override
	protected void onStop() {
		accman.unregisterListener(mySensorEvents);
		compman.unregisterListener(mySensorEvents);
		super.onStop();
	}
	
	
}