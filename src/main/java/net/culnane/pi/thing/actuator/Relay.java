package net.culnane.pi.thing.actuator;

import java.util.Objects;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.wiringpi.Gpio;

import net.culnane.pi.thing.CommandLineRunner;

/**
 * Relay switch.
 *
 * @author Doug Culnane
 */
public class Relay {
	
	private boolean wiredOffWithNoPower = false;
	private GpioPinDigitalOutput pin = null;
	
	public Relay(Pin pin, boolean wiredOffWithNoPower) {
		this.pin = GpioFactory.getInstance().provisionDigitalOutputPin(pin, PinState.LOW);
		this.wiredOffWithNoPower = wiredOffWithNoPower;
	}
	
	public void on() {
		if (wiredOffWithNoPower) {
			pin.setState(PinState.HIGH);
		} else {
			pin.setState(PinState.LOW);
		}
	}
	
	public void off() {
		if (wiredOffWithNoPower) {
			pin.setState(PinState.LOW);
		} else {
			pin.setState(PinState.HIGH);
		}
	}

	public void main(String[] args) {
		
		if (Objects.isNull(args) || args.length == 0) {
			System.out.println("Enter GPIO PIN as parameter");
			System.out.println(getUsage());
			return;
		}
		
		int pinNumber = Integer.valueOf(args[0]);
		Pin pin = RaspiPin.getPinByAddress(pinNumber);
    	if (Objects.isNull(pin)) {
    		System.out.println("Can not find pin number: " + args[0]);
    		return;
    	}
		
    	if (Gpio.wiringPiSetup() == -1) {
            System.out.println("GPIO wiringPiSetup Failed!");
            return;
        }
    	
    	System.out.println("Starting Relay on PIN: " + pin.getName());
    	Relay relay = new Relay(pin, true);
        int LOOP_SIZE = 10;
    	for (int i=0; i < LOOP_SIZE; i++) {
    		try {
				Thread.sleep(1000);
				relay.on();
				Thread.sleep(1000);
				relay.off();
    		} catch (InterruptedException e1) {
				System.out.println("ERROR: " + e1);
			}
    	}
    	System.out.println("Ending Relay");
	}

	public static String getUsage() {
		return CommandLineRunner.JAVA_CMD + "net.culnane.pi.thing.actuator.Relay 5";
	}
}
