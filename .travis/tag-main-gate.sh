#!/usr/bin/env sh
set -x

if [[ $TRAVIS_TEST_RESULT == 0 ]] && [[ 1 == 0 ]];
then
	.travis/tag-main.sh
	RETURN_VALUE=$?
	if [ "$RETURN_VALUE" != "0" ];
	then
		exit $RETURN_VALUE
	fi
else 
    echo "Tagging from Travis is currently disabled!"
fi
