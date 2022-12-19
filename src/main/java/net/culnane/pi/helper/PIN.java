package net.culnane.pi.helper;

import net.culnane.pi.exceptions.PinConfigurationException;

/**
 * https://github.com/Pi4J/pi4j-example-components/blob/main/src/main/java/com/pi4j/catalog/components/helpers/PIN.java
 */
public enum PIN {
	SDA1(2), SCL1(2), D4(4), TXD(14), RXD(15), D17(17), PWM18(18), D27(27), D22(22), D23(23), D24(24), MOSI(10),
	MISO(9), D25(25), D11(11), CEO(8), CE1(7), 
	D5(5), // GPIO_05 https://pinout.xyz/pinout/pin29_gpio5#;
	D6(6), D16(16), D26(26), D20(20), D21(21), 
	PWM12(12),  // https://pinout.xyz/pinout/pin32_gpio12# 
	PWM13(13),
	PWM19(19);
	
	private final int pin;

	PIN(int pin) {
		this.pin = pin;
	}

	public int getPin() {
		return pin;
	}

	public static PIN getDigitalPin(Integer pinNumber) throws PinConfigurationException {
		switch (pinNumber) {
		case 4:
			return PIN.D4;
		case 17:
			return PIN.D17;
		case 27:
			return PIN.D27;
		case 22:
			return PIN.D22;
		case 23:
			return PIN.D23;
		case 24:
			return PIN.D24;
		case 25:
			return PIN.D25;
		case 11:
			return PIN.D11;
		case 5:
			return PIN.D5;
		case 6:
			return PIN.D6;
		case 16:
			return PIN.D16;
		case 26:
			return PIN.D26;
		case 20:
			return PIN.D20;
		case 21:
			return PIN.D21;
		default:
			throw new PinConfigurationException("Invalid digital pin number: " + pinNumber);
		}
	}

	public static PIN getPwmPin(Integer pinNumber) throws PinConfigurationException {
		switch (pinNumber) {
		case 12:
			return PIN.PWM12;
		case 13:
			return PIN.PWM13;
		case 19:
			return PIN.PWM19;
		default:
			throw new PinConfigurationException("Invalid digital pin number: " + pinNumber);
		}
	}
}
