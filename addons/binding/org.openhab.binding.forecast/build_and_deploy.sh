#!/bin/bash

OPENHAB_ADDON_DIR="/usr/share/openhab2/addons"

set -e

mvn clean package $1

cd target
BINDING=`ls org.openhab.binding.forecast*SNAPSHOT.jar`

echo "Binding: $BINDING"

scp $BINDING pi@raspberrypi:/home/pi
ssh pi@raspberrypi "sudo mv $BINDING $OPENHAB_ADDON_DIR"

echo "Successfully copied to raspi"
