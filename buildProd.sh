#!/bin/sh
mvn clean package install -P "$1" -DskipTests=true
