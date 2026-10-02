#!/bin/sh
set -e

# Substitute PORT in Tomcat server.xml for Render/Cloud environments (default 8080)
PORT="${PORT:-8080}"
sed -i "s/port=\"8080\"/port=\"${PORT}\"/g" /usr/local/tomcat/conf/server.xml

exec "$@"
