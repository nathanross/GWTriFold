CONTENTS OF THIS FILE.
1. Build GWTriFold or ConfigurationDemo from source
2. Extending or Replacing DefaultPolicy, and altering GWTriFold's behavior
3. Altering Tri-Fold.js' behavior
4. Running scripts in the Tools folder.

1. Build from source

Build requirements:

Software:
	Windows or Linux
		(may build on OS X, not tested)
	GWT
	JDK
	Ant
	Powershell

Environment/shell variables that must be set.
	GWT_HOME (GWT root dir)
	JAVA_HOME (JDK root dir)
	PATH must include ant/bin and java/bin folders
	
Building GWTriFold from Source
	>cd GWTriFold/
	>ant jar
	>cp /war/WEB-INF/lib/GWTriFold.jar <yourPackage>/war/WEB-INF/lib/GWTriFold.jar
	
Building ConfigurationDemo from Source
	#as you would any other project using GWTriFold.
	#put GWTriFold.jar in ConfigurationDemo's /war/WEB-INF/lib/ folder.
	>cd ConfigurationDemo/
	>ant devmode #devmode
	>and war #war

2. Extending or replacing DefaultPolicy or altering GWTriFold's behavior.
	If doing any development, here are some helpful tools.

	1. Enable javascript console debugging.
	
	Tri-Fold has a debug log GWTriFold integrates with - when enabled, both java and js events are logged to the JS console, with keywords indicating their source class. To enable debug logging:
	
	i. Go to GWTriFold/src/io.../gwtrifold, and move /js/Tri-Fold-0.1.js to /public/Tri-Fold-0.1.min.js. 
	ii. Open up TriFold-0.1.js, and edit the parameter "debug=false" to "debug=true";
	iii. Build GWTriFold.jar and during usage, at any point open the javascript console and type window.triFold[0].printDebugQueue() to print messages up to that point.
	
	You can also have messages print live as the events occur, however this will significantly affect performance (and may even lead to out of order messages in specific circumstances of near-synchronous log commands on FF) to do so set "debugQueue" to false in the JS file.
	
	2. Configuration Demo is a test suite:
	
	If you're extending DefaultPolicy.java, editing its' implementation, or editing Tri-Fold's implementation ConfigurationDemo is an extremely helpful test suite.
	
	But if you're implementing the Policy interface in another way, ConfigurationDemo won't be of much use to you (without digging in and changing its configuration calls), and you're likely better off doing testing with your own application. 	
	
	ConfigurationDemo/tools/build/configDemoBuildChain.sh (or for Windows, configDemoBuildChain.ps1), is designed to facilitate the package's usage as a test suite; it can be used by moving it to the same folder as ConfigurationDemo and GWTriFold (the source folders) and calling it. Here's what the script does: build GWTriFold, copy the jar over, clean out the unitCache and temp folder on every fifth call, and run ConfigurationDemo in devmode.
	
3. Altering Tri-Fold.js' behavior

	If you specifically plan on making many changes to the TriFold.js library, and would like to test them or debug them, it's easy to set it so that you can see javascript implementation changes without recompiling GWTriFold.

	The best way do this as you develop is to place a copy of the changed javascript in the ConfigurationDemo (or your project's) WAR folder, and add a "<script src='TriFold-A.B.js' />" line to ConfigurationDemo.html placed after the GWT noCache js is loaded. 
	
	Alternatively, you can remove the minified version from the GWTriFold module and add the in-development to the public folder and the module XML file of Configuration Demo. 
	
4. If you want to rebuild static files in the /public/ directory from changed sources in the /tools/ directory:
	Certain static files used in the public directory are generated from components in the /tools/ directory of each package. These only need to be generated once each release - for your convenience, the built static files included in the public folder of the source tree.
	
	If you'd like to rebuild them, some of them will require extra software (with genResponsiveFonts, you will need cygwin, bash, or a careful porting to PowerShell SED and AWK idiosyncrasies. Minifying the js requires a JS minifier.)	
