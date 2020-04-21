#!/usr/bin/env bash

#
# This lists all the SRG IDs in the ComplianceAsCode/content project
# that do not match any requirements in the DISA SRG for General
# Purpose Operating Systems. Please run this after running the
# check-cac-content.sh script. The output is CSV formatted.
#

for item in $(grep '^+' srg-diff-list.txt | sed 's/+//g')
do
    echo -n "$item,"
    result=$(grep -rn $item'[^0-9A-Z\-]' content | cut -d: -f1,2)
    if [[ "$result" == "" ]]
    then
        result=$(grep -rn $item content | cut -d: -f1,2)
    fi
    echo $result | sed -E 's/([0-9]) (content)/\1\
,\2/g'
done | sed 's/content\///g'
