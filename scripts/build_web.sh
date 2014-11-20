#!/bin/bash

WEB=web
ROOT=deploy/web

mkdir -p ${ROOT}
cp -fr ${WEB}/* ${ROOT}
