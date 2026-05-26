package net.culnane.pi.thing.actuator;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestServo {

    public TestServo() {

    }

    @Test
    public void testAngleCalc() {
        assertEquals(Servo.DEFAULT_MIN_DUTY_CYCLE, Servo.mapToDutyCycle(Servo.DEFAULT_MIN_ANGLE, Servo.DEFAULT_MIN_ANGLE, Servo.DEFAULT_MAX_ANGLE));
        assertEquals(Servo.DEFAULT_MAX_DUTY_CYCLE, Servo.mapToDutyCycle(Servo.DEFAULT_MAX_ANGLE, Servo.DEFAULT_MIN_ANGLE, Servo.DEFAULT_MAX_ANGLE));
        
        assertEquals(12, Servo.mapToDutyCycle(90, Servo.DEFAULT_MIN_ANGLE, Servo.DEFAULT_MAX_ANGLE));
        assertEquals(10, Servo.mapToDutyCycle(45, Servo.DEFAULT_MIN_ANGLE, Servo.DEFAULT_MAX_ANGLE));
        assertEquals(7, Servo.mapToDutyCycle(0, Servo.DEFAULT_MIN_ANGLE, Servo.DEFAULT_MAX_ANGLE));
        assertEquals(2, Servo.mapToDutyCycle(-90, Servo.DEFAULT_MIN_ANGLE, Servo.DEFAULT_MAX_ANGLE));
    }
}
