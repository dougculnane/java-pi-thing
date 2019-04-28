package net.culnane.pi.thing;

import net.culnane.pi.thing.actuator.Relay;
import net.culnane.pi.thing.sensor.DHT22;

public class CommandLineRunner {

	public static final String JAVA_CMD = "java -cp ./java-pi-thing-VERSION-jar-with-dependencies.jar ";
	
	public static void main(String[] args) {
		System.out.println("Command line usage:");
		System.out.println(DHT22.getUsage());
		System.out.println(Relay.getUsage());
	}

}
