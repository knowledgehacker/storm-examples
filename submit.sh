#!/bin/sh
# test on offline remote cluster
PACKAGE_NAME=trident-sample
VERSION=1.0.0

storm jar target/$PACKAGE_NAME-$VERSION-jar-with-dependencies.jar trident.sample.topology.WordCountTopology words.txt
