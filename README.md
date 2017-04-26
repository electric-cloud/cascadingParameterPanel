# Cascading Parameter Panel README

## Overview

This is a plugin project for eclipse that builds against Commander SDK 6.0. The
resulting plugin provides a parameter panel that illustrates how to write a GWT
parameter panel so choice in the first parameter (project) affects the content
of the second parameter

## Quick Installation

* Download or pull the project from github
* Import as a project into eclipse
* Install Commander tools 6.0 or greater
* Install and configure Commander SDK 6.0 or greater
* Create your .classpath file based on the provided sample-classpath file
* Modify the build.xml to point to the right SDK directory
* Build the project under eclipse or with ant.
  * For demo purpose, the ready to use plugin is available at the top level
* Deploy the plugin to Commander via eclipse
  * $SDK_LOCATION/tools/ant/bin/ant build
  * $SDK_LOCATION/tools/ant/bin/ant deploy

* Install the DSL file to create the procedure that will invoke the parameter panel
  * ectool evalDsl --dslFile project.groovy
* run the procedure testCascadingParameterPanel in the project cascadingParameterPanel

* For production builds of this plugin:
  Edit *.gwt.xml files and delete browser and locale limits.  These limits
  have been specified in order to maximize development build speed.
  Files to edit:
    src/ecplugins/WorkflowDashboard/WorkflowDashboardMain.gwt.xml
    src/ecplugins/WorkflowDashboard/ConfigureDashboard.gwt.xml


## Obtaining Help

Additional help is available through the http://ask.electric-cloud.com forum.
