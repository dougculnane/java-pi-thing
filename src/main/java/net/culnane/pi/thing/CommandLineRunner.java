package net.culnane.pi.thing;

import java.io.IOException;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.TimeoutException;

import com.pi4j.Pi4J;
import com.pi4j.context.Context;

import net.culnane.pi.exceptions.PinConfigurationException;
import net.culnane.pi.helper.PIN;
import net.culnane.pi.thing.actuator.Relay;
import net.culnane.pi.thing.actuator.Servo;
import net.culnane.pi.thing.sensor.DHT22;
import net.culnane.pi.thing.sensor.DS18B20;

public class CommandLineRunner {

	private static final int TEST_LOOP_SIZE = 10;
	
	public static final String JAVA_CMD = "java -jar ./java-pi-thing-" 
			+ getVersion() + "-jar-with-dependencies.jar ";
	
	public static void main(String[] args) throws Exception {
		
		if (Objects.isNull(args) || args.length == 0) {
			System.out.println("Command line usage:");
			System.out.println(JAVA_CMD + "<Relay|Servo|DHT22|DS18B20> [pinNumber]");
			return;
		}
		
		
		
		Integer pinNumber = null;
		if (args.length > 1) {
			pinNumber = Integer.valueOf(args[1]);
		}
		
		Context pi4jContext = Pi4J.newAutoContext();
		switch (args[0]) {
		case "Relay":
			runRelyTest(pi4jContext, pinNumber);
			break;
		case "Servo":
			runServoTest(pi4jContext, pinNumber);
			break;
		case "DHT22":
			runDHT22Test(pi4jContext, pinNumber);
			break;
		case "DS18B20":
			runDS18B20Test();
	    	break;
		default:
			System.err.println("Can not find thing called \"" + args[0] + "\"");
			return;
		}
	}
	
	private static void runDS18B20Test() throws Exception {
		String[] devices = DS18B20.getDevices();
		for (String device : devices) {
			DS18B20 ds18b20 = new DS18B20(device);
			ds18b20.read();
			System.out.println("Temperature on device " + ds18b20.getName() + 
			        " ("+ device + ") is: " + ds18b20.getTemperature() + "°C");
		}
	}

	private static void runDHT22Test(Context pi4jContext, Integer pinNumber) throws InterruptedException, PinConfigurationException {
		PIN pin;
		if (pinNumber == null) {
			pin = PIN.D5;
		} else {
			pin = PIN.getDigitalPin(pinNumber);
		}
    	DHT22 dht22 = new DHT22(pi4jContext, pin);
    	int countSuccess = 0;
    	for (int i=0; i < TEST_LOOP_SIZE; i++) {
			System.out.println();
			try {
				dht22.read();
    	        System.out.println("Humidity=" + dht22.getHumidity() + 
    	        		"%, Temperature=" + dht22.getTemperature() + "*C");
    	        countSuccess++;
    		} catch (TimeoutException e) {
    			System.err.println("ERROR: " + e);
			} catch (Exception e) {
				System.err.println("ERROR: " + e);
			}
			Thread.sleep(DHT22.MIN_MILLISECS_BETWEEN_READS);
    	}
    	System.out.println("Read success rate: "+ countSuccess + " / " + TEST_LOOP_SIZE);
    	
    	// use the provided read loop.
    	try {
    	    System.out.println();
    	    System.out.println("Running read loop method");
            dht22.doReadLoop();
            System.out.println("Humidity=" + dht22.getHumidity() + 
                    "%, Temperature=" + dht22.getTemperature() + "*C");
        } catch (IOException e) {
            System.err.println("ERROR: " + e);
        }
	}

	private static void runServoTest(Context pi4jContext, Integer pinNumber) throws InterruptedException, PinConfigurationException {
		PIN pin;
		if (pinNumber == null) {
			pin = PIN.PWM12;
		} else {
			pin = PIN.getPwmPin(pinNumber);
		}
		Servo servo = new Servo(pi4jContext, pin);
		for (int j=0; j < TEST_LOOP_SIZE; j++) {

			// rotate 180 in 1 second.
			for (int i = 0; i <= 180; i++) {
				servo.setPositionAngle(i);
				Thread.sleep(1000/181);
			}
			
			// rotate back 180 in 1 second.
			for (int i = 180; i >= 0; i--) {
				servo.setPositionAngle(i);
				Thread.sleep(1000/181);
			}
		}
	}

	private static void runRelyTest(Context pi4jContext, Integer pinNumber) throws InterruptedException, PinConfigurationException {
		PIN pin;
		if (pinNumber == null) {
			pin = PIN.D5;
		} else {
			pin = PIN.getDigitalPin(pinNumber);
		}
		Relay relay = new Relay(pi4jContext, pin, true);
		for (int i=0; i < TEST_LOOP_SIZE; i++) {
			Thread.sleep(1000);
			relay.on();
			Thread.sleep(1000);
			relay.off();
    	}
	}
	
	public static String getVersion() {
		Properties mvnProps = new Properties();
		try {
			mvnProps.load(CommandLineRunner.class.getResourceAsStream("/mvn.properties"));
		} catch (IOException e) {
			return "[VERSION]";
		}
		return mvnProps.getProperty("project.version");
	}
}
