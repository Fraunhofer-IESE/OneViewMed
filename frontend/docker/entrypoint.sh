#!/usr/bin/env bash

constants=(
    BACKEND_URL #
)

config_file='{}'

for i in ${constants[@]}; do
    value="$i"
    if [ ! ${!value} ]; then
        echo "Config variable $i not defined!"
        exit 1
    fi
    echo "Settings $value to ${!value}"
    property=$(echo $i | sed 's/./\L&/g' | sed -re "s~(_)(.)~\U\2~g")
    config_file=$(echo $config_file | jq --arg property "$property" --arg value "${!value}" '.[$property] = $value')
done

echo $config_file > /var/www/html/config.json

exec "$@"
