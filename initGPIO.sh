#!/bin/sh

echo "Set GPIOs to Export"
echo "21" > /sys/class/gpio/export
echo "20" > /sys/class/gpio/export
echo "12" > /sys/class/gpio/export
#echo "16" > /sys/class/gpio/export
echo "8"  > /sys/class/gpio/export
echo "7"  > /sys/class/gpio/export
echo "24" > /sys/class/gpio/export
echo "25" > /sys/class/gpio/export
sleep 3
echo "Set GPIO Direction to out"
echo "out" > /sys/class/gpio/gpio21/direction
echo "out" > /sys/class/gpio/gpio20/direction
echo "out" > /sys/class/gpio/gpio12/direction
#echo "out" > /sys/class/gpio/gpio16/direction
echo "out" > /sys/class/gpio/gpio8/direction
echo "out" > /sys/class/gpio/gpio7/direction
echo "out" > /sys/class/gpio/gpio24/direction
echo "out" > /sys/class/gpio/gpio25/direction
sleep 2

