
# To run MQTT Tests

# use external moquette java MQTT broker

https://github.com/andsel/moquette
 
download  from here
https://bintray.com/artifact/download/andsel/generic/moquette-distribution-0.11.tar

unpack moquette-distribution-0.11.tar
tar zxf moquette-distribution-0.11.tar

in unpacked directory, add an additional config directory and an empty moquette.conf file
moquette-distribution-0.11/bin/config/moquette.conf

run server using
./moquette.sh

This creates an mqtt broker at tcp://localhost:1883

# in eclipse, right click MQTTClientConnectionTests and run as unit test