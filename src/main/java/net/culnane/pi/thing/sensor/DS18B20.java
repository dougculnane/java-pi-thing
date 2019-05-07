package net.culnane.pi.thing.sensor;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import net.culnane.pi.thing.CommandLineRunner;

/**
 * Implements the 1-Wire DS18B20 waterproof temperature sensor.
 * 
 * See https://myhydropi.com/ds18b20-temperature-sensor-on-a-raspberry-pi
 *
 * @author Doug Culnane
 */
public class DS18B20 {

	private static final String  ONE_WIRE_DEVICE_FOLDER = "/sys/bus/w1/devices/";
	
    /**
     * Value of last successful temperature reading.
     */
    private Double temperature = null;
    
    
    /**
     * Constructor with pin used for signal.  See PI4J and WiringPI for
     * pin numbering systems.....
     *
     * @param pin
     */
    public DS18B20() {
    }

    /**
     * Make a new sensor reading.
     *
     * @throws Exception
     */
	public boolean read() throws Exception {
		// TODO....
		//sudo modprobe w1-gpio
		//sudo modprobe w1-therm
		// list and read files in ONE_WIRE_DEVICE_FOLDER.
    	return true;
    }
	
	public Double getTemperature() {
		return temperature;
	}
	
	/**
	 * Run from command line to loop and make readings.
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
    	
        System.out.println("Starting DS18B20");
        DS18B20 ds18b20 = new DS18B20();
    	int LOOP_SIZE = 10;
    	int countSuccess = 0;
    	for (int i=0; i < LOOP_SIZE; i++) {
    		try {
				Thread.sleep(3000);
				System.out.println();

				try {
					ds18b20.read();
	    	        System.out.println("Temperature=" + ds18b20.getTemperature() + "*C");
	    	        countSuccess++;
	    		} catch (TimeoutException e) {
	    			System.out.println("ERROR: " + e);
				} catch (Exception e) {
					System.out.println("ERROR: " + e);
				}
	    		
    		} catch (InterruptedException e1) {
				System.out.println("ERROR: " + e1);
			}
    	}
    	System.out.println("Read success rate: "+ countSuccess + " / " + LOOP_SIZE);
    	System.out.println("Ending DS18B20");
	}

	public static String getUsage() {
		return CommandLineRunner.JAVA_CMD + "net.culnane.pi.thing.sensor.DS18B20";
	}

}
