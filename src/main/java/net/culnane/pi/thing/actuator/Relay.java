package net.culnane.pi.thing.actuator;

import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;

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

}
