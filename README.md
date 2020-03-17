# SrgHtmlMapper
This is a quick and dirty effort to generate forward and reverse
mappings between NIST SP 800-53 controls and the DISA SRG identifiers
in the operating system security requirements guide (SRG).

The following files are in src/main/resources:
* U_CCI_List.xml - the [Control Correlation Identifiers](https://dl.dod.cyber.mil/wp-content/uploads/stigs/zip/u_cci_list.zip)
* U_General_Purpose_Operating_System_SRG_V1R6_Manual-xccdf.xml - the [General Purpose Operating System Security Requirements Guide](https://dl.dod.cyber.mil/wp-content/uploads/stigs/zip/U_General_Purpose_Operating_System_V1R6_SRG.zip)

These should be updated with any changes to the referenced documents.

## Build
Simply run the following maven command,

    mvn clean install

## Run
Simply run the following maven command,

    mvn exec:java

The generated reports are:
* [SP800-53ToCCIToSRG.html](http://people.redhat.com/rlucente/SP800-53ToCCIToSRG.html)
* [SRGToCCIToSP800-53.html](http://people.redhat.com/rlucente/SRGToCCIToSP800-53.html)

