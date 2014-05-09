# Build Requirements

##Command Line

Ensure you have:

* JDK >= 6 installed and on your PATH
* ANT >= 1.90 installed and on your PATH
* a downloaded, unzipped copy of the GWT SDK >= 2.6.0.

Path variables:

* Ensure the ant/bin folder is on your PATH
* Set the environment variable JAVA_HOME to the location of your 
JDK/bin folder. Also ensure that this folder is on your PATH
* Set the environment variable GWT_HOME to the location of your GWT SDK folder (e.g. gwt-2.6.0)

Instructions:
1. navigate to this folder
2. run "ant build" or "ant devmode"
    
    to see the results of "ant build" open the Sample.html file in the "war/" directory.

##Eclipse:
Ensure you have:
* Eclipse >= 4.3
* Google Plugin and GWT SDK for eclipse
``` 
    (in eclipse: "help" > "install new software" > "add")
	https://dl.google.com/eclipse/plugin/4.3
    double check that you install the Google Web Toolkit SDK
    and not just the plugin.
```

Instructions:
1. In the eclipse
	'packages' section, right click and select 
	"import" > "general" > "existing projects" and select this folder.
		
	Eclipse will temporarily display an error because GWTriFold does
    not ship with a copy of GWT-Servlet.
		
2. Right click the "build.xml" ANT file and click "Run as.." and select the second "Ant build" option. Click the tab "JRE" and click "run in the same runtime as this project." Click "apply" and "run." 

    ANT will automatically retrieve Eclipse's GWT folder's copy of 
    GWT-Servlet and copy it to the project. 
		
3. Click the "War/WEB-INF/lib" folder in this view, and press F5 to refresh.
		
    Now you will be able to build the project through eclipse's interface.
		
    Running the ant build script will have also compiled SampleApp.
    To see it working, open the Sample.html file in the /war directory of this project.
		NV