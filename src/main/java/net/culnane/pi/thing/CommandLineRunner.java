package net.culnane.pi.thing;

import java.io.IOException;
import java.util.Properties;

import net.culnane.pi.thing.actuator.Relay;
import net.culnane.pi.thing.actuator.Servo;
import net.culnane.pi.thing.sensor.DHT22;
import net.culnane.pi.thing.sensor.DS18B20;

public class CommandLineRunner {

	public static String version = null;
	
	public static final String JAVA_CMD = "java -cp ./java-pi-thing-" + getVersion() + "-jar-with-dependencies.jar ";
	
	public static void main(String[] args) {
		System.out.println("Command line usage:");
		System.out.println(DHT22.getUsage());
		System.out.println(DS18B20.getUsage());
		System.out.println(Relay.getUsage());
		System.out.println(Servo.getUsage());
	}
	
	public static String getVersion() {
		if (version == null) {
			Properties mvnProps = new Properties();
			try {
				mvnProps.load(CommandLineRunner.class.getResourceAsStream("/mvn.properties"));
			} catch (IOException e) {
				version = "?";
			}
			version = mvnProps.getProperty("project.version");
		}
		return version;
		
	}

}
