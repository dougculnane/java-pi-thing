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
import net.culnane.pi.thing.sensor.DS18B20;

public class CommandLineRunner {

	private static final int TEST_LOOP_SIZE = 5;
	
	public static final String JAVA_CMD = "java -jar ./java-pi-thing-" 
			+ getVersion() + "-jar-with-dependencies.jar ";
	
	public static void main(String[] args) throws Exception {
		
		if (Objects.isNull(args) || args.length == 0) {
			System.out.println("Command line usage:");
			System.out.println(JAVA_CMD + "<Relay|Servo|DS18B20> [pinNumber]");
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

	private static void runServoTest(Context pi4jContext, Integer pinNumber) throws InterruptedException, PinConfigurationException {
	    final int delayMills = 500;
	    PIN pin = PIN.PWM18;  // Use hardware clock pin.
		Servo servo = new Servo(pi4jContext, pin);
		
		System.out.println("setting servo to -90");
		servo.setPositionAngle(-90);
		Thread.sleep(delayMills * 10);
		System.out.println("setting servo to 0");
        servo.setPositionAngle(0);
	    Thread.sleep(delayMills * 10);
	    System.out.println("setting servo to 90");
	    servo.setPositionAngle(90);
	    Thread.sleep(delayMills * 10);
	    
		for (int j=0; j < TEST_LOOP_SIZE; j++) {
	        System.out.println("");
			// rotate 180
			for (int i = -9; i <= 9; i++) {
			    System.out.println("setting servo to: " + i*10);
				servo.setPositionAngle(i*10);
				Thread.sleep(delayMills);
			}
			
			// rotate back 180
			for (int i = 9; i >= -9; i--) {
			    System.out.println("setting servo to: " + i*10);
				servo.setPositionAngle(i*10);
				Thread.sleep(delayMills);
			}
		}
	}

	private static void runRelyTest(Context pi4jContext, Integer pinNumber) throws InterruptedException, PinConfigurationException {
		PIN pin;
		if (pinNumber == null) {
			pin = PIN.D6;
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
