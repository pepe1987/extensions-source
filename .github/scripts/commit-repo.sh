#!/bin/bash
set -e

rsync -a --delete --exclude .git --exclude .gitignore --exclude README.md ../main/repo/ .
git config user.name "$(git log -n 1 --pretty=format:%an)"
git config user.email "$(git log -n 1 --pretty=format:%ae)"
git status
if [ -n "$(git status --porcelain)" ]; then
    git add .
    git commit -m "Update extensions repo"
    git push

    // curl https://purge.jsdelivr.net/gh/keiyoushi/extensions@repo/index.min.json
else
    echo "No changes to commit"
fi
