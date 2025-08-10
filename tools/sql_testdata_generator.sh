#!/usr/bin/env bash

START=20
TOTAL=50
for (( i = 0; i < $TOTAL; i++ )); do
  ID=$((-20-$i))
  EXTID=$(uuidgen)
  echo "INSERT INTO \"DIRECTORY_ENTRY\" (\"ID\", \"TYPE\", \"EXT_ID\", \"STATUS\", \"NAME\") VALUES ($ID, 0, '$EXTID', 0, 'foo$ID');"
  echo "INSERT INTO \"ACCESS\" (\"ID\", \"ROLE\", \"PERMISSION\", \"ACTOR_ID\", \"ENTRY_ID\") VALUES ($ID, 0, 0, -11, $ID);"
done
