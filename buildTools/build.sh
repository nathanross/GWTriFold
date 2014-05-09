#!/bin/bash
#
#(GWTriFold) buildTools/build.sh
# copy trifold src to gwtrifold, compile, mv jar to sampleapp, 
#	zip or run sampleapp
# Copyright 2013-2014 Nathan Ross (nrossit2@gmail.com)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

#./build.sh rungwt - build the GWTriFold and SampleApp from TriFold repo's js.
#./build.sh rungwt debug - from the TriFold's repo's unminified js
#./build.sh demozips - build the demozips avail for customization and 
#												download through configdemo
#./build.sh b64		 - build them as base-64 encoded in a javascript variable assignment
#								in individual .js files instead of as zip files.
#./build.sh demozips debug - packs the demozips using unminified TriFold js
#							will likely never need to call this. GWT testing
#							is of course with the 'rungwt debug' command.
cd `dirname ${BASH_SOURCE[0]}`
REPO_SAMPLEAPP=`readlink -m ../SampleApp`
REPO_GWTRIFOLD=`readlink -m ../GWTriFold`
REPO_TRIFOLD=`readlink -m ../../TriFold`
if [[ "$#" -gt 1 && "$2" = "debug" ]]
then
	debugtext="debug"
	cp $REPO_TRIFOLD/src/TriFold-*.js $REPO_GWTRIFOLD/src/io/github/nathanross/gwtrifold/public/TriFold.js
	cp $REPO_TRIFOLD/src/TriFold*.css $REPO_GWTRIFOLD/src/io/github/nathanross/gwtrifold/public/TriFold.css
else
	debugtext=""
	cp $REPO_TRIFOLD/TriFold-*.min.js $REPO_GWTRIFOLD/src/io/github/nathanross/gwtrifold/public/TriFold.js
	cp $REPO_TRIFOLD/TriFold*.min.css $REPO_GWTRIFOLD/src/io/github/nathanross/gwtrifold/public/TriFold.css
fi
cd $REPO_TRIFOLD/tools/
./getDemoStylesFromCfgDemo.sh
cp ../debug/style/*.css $REPO_SAMPLEAPP/src/com/website/sample/public/
cd $REPO_GWTRIFOLD
ant jar
if [ -f $REPO_SAMPLEAPP/gwt-unitCache/gwt* ] 
	then rm $REPO_SAMPLEAPP/gwt-unitCache/gwt*
fi
cp $REPO_GWTRIFOLD/war/WEB-INF/lib/GWTriFold.jar $REPO_SAMPLEAPP/war/WEB-INF/lib/ 

if [[ "$#" -gt 0 && "$1" = "rungwt" ]]
then
	cd $REPO_SAMPLEAPP
	ant gwtc
else
	base=""
	if [[ "$#" -gt 0 && "$1" = "b64" ]]
	then
		base="b64"
	fi
	cd $REPO_GWTRIFOLD/../buildTools/
	./genDemoZip.sh $base
	cd $REPO_TRIFOLD/tools/
	./genDemoZip.sh $debugtext $base
	if [[ "$#" -gt 0 && "$1" = "b64" ]]
	then
		mv /tmp/GWTriFold*.js /tmp/TriFold*.js $REPO_TRIFOLD/configdemo/libs/zipTools/
	else
		mv /tmp/GWTriFold*.zip /tmp/TriFold*.zip $REPO_TRIFOLD/configdemo/resources/
	fi
fi
