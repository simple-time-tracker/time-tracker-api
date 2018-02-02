#!/bin/sh
cd $SHIPPABLE_BUILD_DIR
mkdir -p shippable/testresults
cp api/build/test-results/test/*.xml shippable/testresults