#!/bin/bash
BASEDIR=$(cd ${0%/*}; pwd)
cd ${BASEDIR}

# EasyMargin Startup script.

../app/java/bin/java -Xms4g -Xmx8g -jar easymargining.eurex-0.0.1.jar -vd 2015-11-27 -m /S1DUATTT001/home/esmuat01/data/eurex/marketdata