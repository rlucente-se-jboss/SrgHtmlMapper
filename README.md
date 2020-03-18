# SrgHtmlMapper
The DISA General Purpose Operating System Security Requirements
Guide (SRG) identifies mappings between DISA SRG requirements and
Control Correlation Identifiers (CCIs). The CCIs in turn map to
specific NIST SP 800-53 controls.

The operating system SRG V1R6 was last updated in July 2019. V1R5
was released in January 2019 and V1R4 was released in July 2016.
These are quite stable with only minimal changes between them, none
of which affected requirement mappings. You can easily compare the
current version of the SRG against prior versions
[here](https://vaulted.io/library/disa-stigs-srgs/general_purpose_operating_system_srg).
The CCI list is updated less frequently with the last update occurring
in February 2017.

This project is a quick and dirty effort to generate forward and
reverse mappings between NIST SP 800-53 controls and the DISA SRG
identifiers in the operating system security requirements guide
(SRG).

The following files are in src/main/resources:
* U_CCI_List.xml - the [Control Correlation Identifiers](https://dl.dod.cyber.mil/wp-content/uploads/stigs/zip/u_cci_list.zip)
* U_General_Purpose_Operating_System_SRG_V1R6_Manual-xccdf.xml - the [General Purpose Operating System Security Requirements Guide](https://dl.dod.cyber.mil/wp-content/uploads/stigs/zip/U_General_Purpose_Operating_System_V1R6_SRG.zip)

These files should be updated whenever there are any changes to the
referenced documents.

## Build
Simply run the following maven command,

    mvn clean install

## Run
Simply run the following maven command,

    mvn exec:java

The generated reports are:
* [SP800-53ToCCIToSRG.html](http://people.redhat.com/rlucente/SP800-53ToCCIToSRG.html)
* [SRGToCCIToSP800-53.html](http://people.redhat.com/rlucente/SRGToCCIToSP800-53.html)

