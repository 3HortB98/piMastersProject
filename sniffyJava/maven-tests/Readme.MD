# Java solution

## This is the folders and files for the java solution used in this project

Eclipse IDE was used to create this

### MQTT Set up
this is for mosquitto MQTT
Instructions can be found [here](https://mosquitto.org/blog/2013/01/mosquitto-debian-repository/)
For an updated version of debian use:
 ```
 sudo wget http://repo.mosquitto.org/debian/mosquitto-stretch.list
 ```
instead of:
```
sudo wget http://repo.mosquitto.org/debian/mosquitto-wheezy.list
sudo wget http://repo.mosquitto.org/debian/mosquitto-jessie.list
```
 To test:
 
 use mosquitto_sub to subscribe to a topic:

```mosquitto mosquitto_sub -t 'test/topic' -v```

And to publish a message:

```mosquitto mosquitto_pub -t 'test/topic' -m 'hello world'```

## main file
The main java file is [SensorsSniffy.java](piMastersProject/sniffyJava/maven-tests/src/main/java/com/masters/sniffy/SensorsSniffy.java )

this works but does contain some irrelvant commented code
