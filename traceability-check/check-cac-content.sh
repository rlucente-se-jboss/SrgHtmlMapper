#!/usr/bin/env bash

WORKDIR=$(pushd $(dirname $0) &> /dev/null && pwd && popd &> /dev/null)

pushd $WORKDIR/.. &> /dev/null

mvn clean install exec:java
cd $WORKDIR

rm -fr content
git clone https://github.com/ComplianceAsCode/content.git

# find all unique SRG- references in ComplianceAsCode
#
# this pipeline does the following:
#
#   find all instances of the string 'SRG-'
#   insert a newline before each occurrence of 'SRG-'
#   remove all characters after the SRG requirement where the requirement
#     matches the regex 'SRG-[0-9A-Z\-]+'
#   sort results and throw out duplicates
#   save list to the cac-srg-list.txt file

grep -rn SRG- content | \
    sed $'s/SRG-/\\\nSRG-/g' | \
    grep '^SRG-' | \
    sed 's/^\(SRG-[0-9A-Z\-]*\)..*/\1/g' | \
    sort -u \
    > cac-srg-list.txt

# find all unique SRG references from DISA source
#
# repeat the above pipeline on the mapping report

grep -rn SRG- ../SRGToCCIToSP800-53.html | \
    sed $'s/SRG-/\\\nSRG-/g' | \
    grep '^SRG-' | \
    sed 's/^\(SRG-[0-9A-Z\-]*\)..*/\1/g' | \
    sort -u \
    > disa-srg-list.txt

diff -uw disa-srg-list.txt cac-srg-list.txt | \
    grep -v '^+++' | grep -v '^---' | \
    grep -v 'SRG-APP' \
    > srg-diff-list.txt

{
  echo
  echo "************************************************************************"
  echo
  echo The following compares the requirements in the DISA General Purpose
  echo 'Operating System (GPOS) Security Requirements Guide (SRG) against the'
  echo SRG references in the Compliance as Code project.
  echo
  echo "************************************************************************"
  echo
  echo The following DISA SRG GPOS requirements were not found in the
  echo ComplianceAsCode content:
  echo
  
  if [[ "$(grep '^-' srg-diff-list.txt | wc -l)" -eq 0 ]]
  then
      echo "None"
  else
      grep '^-' srg-diff-list.txt
  fi
  
  echo
  echo The following string occurrences in Compliance As Code do not match a
  echo requirement in the DISA GPOS SRG:
  echo
  if [[ "$(grep '^+' srg-diff-list.txt | wc -l)" -eq 0 ]]
  then
      echo "None"
  else
      grep '^+' srg-diff-list.txt
  fi
  
  echo
} > ../disa-srg-cac-traceability.txt

echo
echo Results are in the file $WORKDIR/../disa-srg-cac-traceability.txt
echo

popd &> /dev/null

