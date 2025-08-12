#!/usr/bin/env bash

START=81
TOTAL=20
for (( i = 0; i < $TOTAL; i++ )); do
  ID=$((-$START-$i))
  EXTID=$(uuidgen)
  echo "INSERT INTO \"DIRECTORY_ENTRY\" (\"ID\", \"TYPE\", \"EXT_ID\", \"STATUS\", \"NAME\", \"PARENT_ID\") VALUES ($ID, 0, '$EXTID', 0, 'foo$ID', -80);"
  echo "INSERT INTO \"ACCESS\" (\"ID\", \"OWNERSHIP\", \"PERMISSION\", \"ACTOR_ID\", \"ENTRY_ID\") VALUES ($ID, 0, 0, -12, $ID);"
done
