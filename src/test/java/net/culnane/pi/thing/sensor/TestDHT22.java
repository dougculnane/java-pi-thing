package net.culnane.pi.thing.sensor;


import org.junit.Assert;
import org.junit.jupiter.api.Test;


class TestDHT22 {

	@Test
	void testGetReadingValueFromBytes() {
		
		Assert.assertEquals(0.1d, DHT22.getReadingValueFromBytes((byte)0, (byte)1), 0);
		Assert.assertEquals(0.4d, DHT22.getReadingValueFromBytes((byte)0, (byte)4), 0);
		Assert.assertEquals(25.7d, DHT22.getReadingValueFromBytes((byte)1, (byte)1), 0);
		Assert.assertEquals(25.5d, DHT22.getReadingValueFromBytes((byte)0x00, (byte)0xFF), 0);
		Assert.assertEquals(25.6d, DHT22.getReadingValueFromBytes((byte)1, (byte)0x00), 0);
		Assert.assertEquals(51.2d, DHT22.getReadingValueFromBytes((byte)2, (byte)0x00), 0);
		Assert.assertEquals(102.4d, DHT22.getReadingValueFromBytes((byte)4, (byte)0x00), 0);
		Assert.assertEquals(204.8d, DHT22.getReadingValueFromBytes((byte)8, (byte)0x00), 0);
		Assert.assertEquals(409.6d, DHT22.getReadingValueFromBytes((byte)16, (byte)0x00), 0);
		Assert.assertEquals(819.2d, DHT22.getReadingValueFromBytes((byte)32, (byte)0x00), 0);
		Assert.assertEquals(1638.4d, DHT22.getReadingValueFromBytes((byte)64, (byte)0x00), 0);
		Assert.assertEquals(-0d, DHT22.getReadingValueFromBytes((byte)128, (byte)0x00), 0);
		Assert.assertEquals(-0.1d, DHT22.getReadingValueFromBytes((byte)128, (byte)0x01), 0.0001);
		Assert.assertEquals(-102.5d, DHT22.getReadingValueFromBytes((byte)132, (byte)0x01), 0.0001);
		Assert.assertEquals(-128.1d, DHT22.getReadingValueFromBytes((byte)133, (byte)0x01), 0.0001);
		
	}

}
