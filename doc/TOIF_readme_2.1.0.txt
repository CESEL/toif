Readme file for

    * Tool Output Integration Framework version 2.1.0

Contents
========

Tool Output Integration Framework version 2.1.0

   1. What is Tool Output Integration Framework (TOIF)?
   2. TOIF 2.1.0 New Features
   3. Supported Open Source Software (OSS) Static Code Analysis (SCA) Tool Versions
   4. Known Issues
   5. Getting help

KDM Analytics customer support

----------------------------------------------------------------------------
Tool Output Integration Framework version 2.1.0
======================================

1. What is TOIF?

The Tool Output Integration Framework (TOIF) is a powerful open source vulnerability detection platform that provides analysts information of system defects with the ability to:
* Integrate multiple OSS SCA tools as �data feeds� into the repository
* Collate findings from several OSS SCA tools
* Put vulnerability findings into the context of other facts about the system (such as metrics, architecture, design patterns, etc.)

TOIF takes the output of OSS SCA tools and displays the results in the TOIF Findings View that has been installed in Eclipse 4.3.2.

TOIF includes the following components:

* TOIF Adaptor: TOIF Adaptor is used to collect the output from various OSS SCA tools and convert their output into TOIF xml
* TOIF Assimilator: After running the TOIF Adaptor you need to run the Assimilator to merge TOIF findings and/or KDM data into a common fact-orientated repository or file.
* TOIF Findings View: Once you have your TOIF findings assimilated you view the TOIF Findings View to display the results in Eclipse.

2. TOIF 2.1.0 New Features:
* Support for Cppcheck 1.60.1
* Updated SFP-CWE Pairings for C/C++ SCA Tools
* Defect description in TOIF Findings view

3. Supported OSS SCA Tool Versions

TOIF 2.1.0 supports the following OSS SCA tool versions for windows and linux:
* cppcheck-1.60.1
* findbugs-3.0.0
* jlint-3.0
* rats-2.3
* splint-3.1.2

4. Known Issues
 
* Traceback displays numbers as reported by TOIF Adaptors: The traceback currently shows numbers as reported by the TOIF Adaptors unlike the code locations in the view list (which are normalized to kdm locations). 
* Use of the Jlint 3.0 OSS SCA tool with TOIF 2.1.0 is not supported on the Fedora-17.x 64-bit operating system.

5. Getting help
  
For TOIF source resources, please visit GitHub https://github.com/KdmAnalytics/toif/.

For information about installing and using TOIF, consult the TOIF 2.1.0 User's Guide. 
  
-----------------------------------------------------------------------
KDM Analytics customer support
====================================
  
If, after consulting the published information, you still have questions about using this KDM Analytics product, please contact KDM Analytics Customer Support using one of these methods:
  
* E-mail: support@kdmanalytics.com
  

Copyright � 2006-2016 KDM Analytics, Inc. All rights reserved. Information subject to change without notice.
  