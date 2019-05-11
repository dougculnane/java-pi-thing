package net.culnane.pi.thing.sensor;

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
}
