CONTENTS OF THIS FILE.

	1. Enable javascript console debugging.
	
	Tri-Fold has a debug log GWTriFold integrates with - when enabled, both java and js events are logged to the JS console, with keywords indicating their source class. To enable debug logging:
	
	i. Go to GWTriFold/src/io.../gwtrifold, and move /js/Tri-Fold-0.1.js to /public/Tri-Fold-0.1.min.js. 
	ii. Open up TriFold-0.1.js, and edit the parameter "debug=false" to "debug=true";
	iii. Build GWTriFold.jar and during usage, at any point open the javascript console and type window.triFold[0].printDebugQueue() to print messages up to that point.
	
	You can also have messages print live as the events occur, however this will significantly affect performance (and may even lead to out of order messages in specific circumstances of near-synchronous log commands on FF) to do so set "debugQueue" to false in the JS file.
	
	2. Configuration Demo is a test suite:
	
	If you're extending DefaultPolicy.java, editing its' implementation, or editing Tri-Fold's implementation ConfigurationDemo is an extremely helpful test suite.
	
	But if you're implementing the Policy interface in another way, ConfigurationDemo won't be of much use to you (without digging in and changing its configuration calls), and you're likely better off doing testing with your own application. 	
	
	ConfigurationDemo/tools/build/configDemoBuildChain.sh (or for Windows, configDemoBuildChain.ps1), is designed to facilitate the package's usage as a test suite; it can be used by moving it to the same folder as ConfigurationDemo and GWTriFold (the source folders) and calling it. Here's what the script does: build GWTriFold, copy the jar over, clean out the unitCache and temp folder on every fifth call, and compile ConfigurationDemo. For regular development (instead
	of compatibility testing), it's usually best to set
	the ConfigurationDemo.gwt.xml file to compile for
	a single permutation.
	
	ConfigurationDemo does not work in devmode. This
	is because one of ConfigurationDemo's abilities
	is to switch and alter stylesheets on user request
	(altering occurs when MinColDimensions are changed).
	
	Devmode runs the application HTML file, as well as 
	all loaded resources (e.g. stylesheets) on a server
	on the loopback address. However, devmode loads
	the application's GWT nocache js as a local 
	file. As a result, when js calls originating from 
	GWT nocache try to access or alter local resource's
	presentation to the user, some browsers will block
	these calls as a potential XSS attack.
	
	In any production environment within which ConfigurationDemo appears, both the js and css will
	be run on the same server and this problem will not occur.
	To simulate this during testing, we can use 
	either Super Dev Mode (which treats the 
	nocache.js as also on a server) or, the method 
	of choice for the build scripts,
	simply compile to our chosen permutation, resulting
	in all files being accessed as local files.
	
3. Altering Tri-Fold.js' behavior

