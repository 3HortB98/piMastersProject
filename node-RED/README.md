# Node-RED Solution

The text file contains the JSON text that is needed to import the flows into your Node-RED.

## Raspberry Pi pre-requirements 
 For the solution to work make sure the following is set up:
 ### Raspbery Pi Configuration
 
 ### Serial port
 On the Raspberry Pi 3. There are 2 serial ports: `/dev/ttyAMA0` and `/dev/ttyS0`. 
 
 `/dev/ttyAMA0` is used for bluetooth.
 
 `/dev/ttyS0` is used for GPIO Serial port.
 
 `/dev/ttyAMA0` has high performance and as bluetooth was not being used. serial ports were swapped. This was done by adding a line to the /boot/config.txt.
 ```
 $ sudo nano /boot/config.txt
 ```
 and add
 `dtoverlay=pi3-miniuart-bt`
 
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

## Node-Red Set Up
Below is instructions on how to set up this solution.

Node-Red should already be installed on Raspberry Pi. If not instructions to download can be found [here](https://nodered.org/docs/hardware/raspberrypi)

## running node-Red on Start up.
use command:
```sudo systemctl enable nodered.service```

## Starting node-red
  1. Run Node-Red.
  2. Node-RED Console window should appear
  3. Following insturctions on there, you can open their web UI on the internet.
  
## Adding the Dashboard Libray
  [dashboard library](https://flows.nodered.org/node/node-red-dashboard)
  To install the stable version: 
  `Menu -> Manage palette` option and search for node-red-dashboard.
  
  you can change the style of the dashboard in the dashboard tab.
  **DO NOT** change the groups unless you know what you are doing as it will mess up the flow. 
  
## Importing the flow
on the web UI:

Menu > Import > Clipboard

copy the text from the ` 	nodeREDflow.txt` file on here
paste into that clipboard.
click ok
click anwhere on the canvas to place done.
click deploy to run

## Configuring the flow
 check serial port
 check MQTT

