package net.culnane.pi.thing.sensor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;

/**
 * Implements the 1-Wire DS18B20 waterproof temperature sensor.
 * 
 * See https://myhydropi.com/ds18b20-temperature-sensor-on-a-raspberry-pi
 *
 * @author Doug Culnane
 */
public class DS18B20 {

	private static final String  ONE_WIRE_DEVICE_FOLDER = "/sys/bus/w1/devices";
	
	private String registeredDeviceMacAddress = "28-xxxxxxxxxxxx";
	
	/**
	 * Name of the sensor.
	 */
	private String name = "MyTemperatureSensor";
	
    /**
     * Value of last successful temperature reading.
     */
    private Double temperature = null;
    
    public DS18B20(String registeredDeviceMacAddress) {
        this.registeredDeviceMacAddress = registeredDeviceMacAddress;
    }
    
    public DS18B20(String registeredDeviceMacAddress, String name) {
        this(registeredDeviceMacAddress);
    	this.name = name;
    }

    /**
     * Make a new sensor reading.
     *
     * @throws Exception
     */
	public boolean read() throws Exception {

		boolean success = false;
		File file = new File(ONE_WIRE_DEVICE_FOLDER + "/" + registeredDeviceMacAddress + "/w1_slave");
		if (!file.exists()) {
			throw new Exception("Device: " + registeredDeviceMacAddress + " not found not file at: " + file.getAbsolutePath());
		}
		try (FileInputStream fis = new FileInputStream(file);
		     InputStreamReader isr = new InputStreamReader(fis);
		      BufferedReader br = new BufferedReader(isr) ) {
	        
		    String line1 = br.readLine();
	        if (line1 != null && line1.endsWith(" YES")) {
	            String line2 = br.readLine();
	            if (line2 != null)  {
	                int startOfTemp = line2.indexOf(" t=");
	                if (startOfTemp > 0 ) {
	                    String strTemp = line2.substring(startOfTemp + 3);
	                    int intTemp = Integer.valueOf(strTemp);
	                    this.temperature = Double.valueOf(intTemp) / 1000;
	                    success = true;
	                }
	            }
	        }
		} catch (Exception e) {
            throw e;
        }
    	return success;
    }
	
	public static String[] getDevices() {
		File folder = new File(ONE_WIRE_DEVICE_FOLDER);
		String[] registeredDevices = new String[0];
		if (folder.exists() && folder.isDirectory()) {
			registeredDevices = folder.list(new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					return name.startsWith("28-");
				}
			});
		}
		return registeredDevices;
	}
	
	public Double getTemperature() {
		return temperature;
	}
	
	public String getName() {
	    return name;
	}
}
