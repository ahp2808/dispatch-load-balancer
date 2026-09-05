#!/usr/bin/env sh
DIR="$(cd "$(dirname "$0")" && pwd)"
if [ -x "$DIR/.tools/apache-maven-3.9.9/bin/mvn" ]; then
    exec "$DIR/.tools/apache-maven-3.9.9/bin/mvn" "$@"
elif command -v mvn >/dev/null 2>&1; then
    exec mvn "$@"
else
    echo "Error: Maven not found in .tools or PATH." >&2
    exit 1
fi
