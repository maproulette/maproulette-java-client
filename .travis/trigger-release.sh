#!/usr/bin/env sh
set -x

# Use Travis to trigger a release from Main

GITHUB_ORGANIZATION=maproulette
GITHUB_REPOSITORY_NAME=maproulette-java-client

# Assumptions
# - This is called from the root of the project
# - The travis client is installed: gem install travis
# - travis login --org has been called to authenticate

# To run manually you can either set your token here instead of the input parameter, or uncomment the line below
#TRAVIS_PERSONAL_TOKEN=$(travis token)
TRAVIS_PERSONAL_TOKEN="$1"

: ${TRAVIS_PERSONAL_TOKEN:?"TRAVIS_PERSONAL_TOKEN needs to be set to access the Travis API to trigger the build"}

body='
{
	"request":
	{
		"branch": "main",
		"config":
		{
			"before_script": "export MANUAL_RELEASE_TRIGGERED=true"
		}
	}
}'

curl -s -X POST \
	-H "Content-Type: application/json" \
	-H "Accept: application/json" \
	-H "Travis-API-Version: 3" \
	-H "Authorization: token $TRAVIS_PERSONAL_TOKEN" \
	-d "$body" \
	https://api.travis-ci.com/repo/$GITHUB_ORGANIZATION%2F$GITHUB_REPOSITORY_NAME/requests
