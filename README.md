# java-pi-thing
Various Raspberry PI GPIO connected things implemented in Java.

Standing on the sholders of Pi4J and WiringPi here are some easy to use sensor and device things that I use in my projects.

```bash
git clone https://github.com/dougculnane/java-pi-thing.git
cd java-pi-thing
mvn install
scp target/java-pi-thing-VERSION-jar-with-dependencies.jar pi@my.raspberry.net:
ssh pi@my.raspberry.net
java -jar java-pi-thing-VERSION-jar-with-dependencies.jar
```
