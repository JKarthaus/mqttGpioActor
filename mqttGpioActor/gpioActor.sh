#!/bin/sh
# Check the given Parameter should be set or get
####################################################
if [ ! "$1" = "set" ]; then
    if [ ! "$1" = "get" ]; then
    	echo "Give me set or get as first parameter"
    	exit
    fi
fi
# Check the second Parameter
####################################################
if [ -z "$2" ]; then
	echo "Give me an GPIO Pin as second Parameter"
   	exit
fi
# Check the given Parameter should be on or off
####################################################
if [ "$1" = "set" ]; then
	if [ ! "$3" = "on" ]; then
    	if [ ! "$3" = "off" ]; then
    	    if [ ! "$3" = "intervall" ]; then
				echo "Give me on or off as third parameter"
				exit
			fi
    	fi
	fi
fi
####################################################
##        SWITCH ON /OFF THE DEBUG MODE           ##
DEBUG_MODE=OFF
DEBUG_GET_VALUE=off
####################################################
#              Set The Variables                  ##
####################################################
PIN_NUMBER=$2
PIN_NAME=/sys/class/gpio/gpio$PIN_NUMBER
INTERVALL_SCRIPT=~/bin
IS_INTERVAL_RUNNING="true"

####################################################
##            CheckIntervall Function             ##
####################################################
ps -C 16_intervall.sh > /dev/null
status=$?

if [ $status -ne 0 ]; then
    IS_INTERVAL_RUNNING="false"
fi

# Debug Mode - for developement outside the Pi
####################################################
if [ "$1" = "set" ]; then
	if [ $DEBUG_MODE = "ON" ]; then
		echo $3
   		exit
	fi
fi



# Check if the PIN is ready only in Real Mode
######################################################
if [ $DEBUG_MODE = "OFF" ]; then
	if [ ! -d "$PIN_NAME" ]; then
    	echo "PIN NOT READY - Try to Set..."
    	echo $PIN_NUMBER > /sys/class/gpio/export 
    	sleep 1 
    	echo "out" > $PIN_NAME/direction
	fi
fi

# Write the Value to the Pin only in Real Mode
######################################################
if [ $DEBUG_MODE = "OFF" ]; then
	if [ $1 = "set" ]; then
		if [ $3 = "on" ]; then
			killall $PIN_NUMBER"_intervall.sh"
	    	echo "1" > $PIN_NAME/value
	    	echo "Set Relais to ON  - PIN $PIN_NUMBER"
		fi
		if [ $3 = "off" ]; then
			killall $PIN_NUMBER"_intervall.sh"
		    echo "0" > $PIN_NAME/value
		    echo "Set Relais to OFF  - PIN $PIN_NUMBER"
		fi
		if [ $3 = "intervall" ]; then
			if [ $IS_INTERVAL_RUNNING = "false" ]; then
				echo "Starte Intervall Script : $PIN_NUMBER _intervall.sh"
				$INTERVALL_SCRIPT/$PIN_NUMBER"_intervall.sh"  > /dev/null &
			else
				echo "Intervall Script : $INTERVALL_SCRIPT/$PIN_NUMBER _intervall.sh is already running"
			fi
		fi
	fi
fi

# Get Data from the PIN
######################################################
if [ $1 = "get" ]; then
	# Debug Mode	
	if [ $DEBUG_MODE = "ON" ]; then
    	echo $DEBUG_GET_VALUE 
		exit
	fi
	# Real Mode
	if [ "$(cat $PIN_NAME/value)" = "0" ]; then
		echo "off"    
	fi
	if [ "$(cat $PIN_NAME/value)" = "1" ]; then
		echo "on"    
	fi
fi





    
