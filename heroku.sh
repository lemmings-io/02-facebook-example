#!/bin/bash
set -eou pipefail

git add -A
git commit -m "heroku deploy"
git push -f heroku master
