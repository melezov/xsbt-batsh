#!/bin/bash

echo Formatting code via scalariform ...
echo Firing up the Scala REPL ...
`dirname $0`/sbt.sh --no-jrebel "$@" scalariform-format
