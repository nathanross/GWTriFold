#!/bin/bash
#
# GWTriFold\tools\karst.sh
# simple organizational/build system for tertiary package resources
# (documentation, non-primary-programming-language assets)
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

# karst is a build-system independent concept for concertedly preparing 
# relatively static tertiary resources, which are later used in a build. 
# It's domain of use and good ROI is exclusively small projects.

# On small projects the complexity cost of using a new small script in 
# addition, and performance cost of no dynamic update in automation
# is less than the complexity cost adapting what otherwise would be simple 
# scripts into build directives.

# important note:
# re-calling every script is nec. consequence of a tool like this not keeping 
# a cache file and not assuming anything about the products of your build 
# scripts. eg your script's mtime may be the same, but it may read input 
# from the FS that has changed since the tool was last called

D="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
for i in `ls src`
	do
	mkdir -p "${D}/out/${i}"
	# doesn't work for some reason. probably need to change -e to -f
	if [ -d "${D}/src/${i}/copy/" ] && [ `ls -1 "${D}/src/${i}/copy/" | wc -l` -gt 0 ]
		then cp -r ${D}/src/${i}/copy/* ${D}/out/${i}/
	fi
	
	if [ -e  ${D}/src/${i}/*.md ]
	then
		for j in `ls ${D}/src/${i}/*.md`
		do 
			of=${D}/out/$i/`basename $j .md`.html
			cat ${D}/src/shared/markdownHeader.html > $of
			markdown $j >> $of 
			echo "</body></html>" >> $of
			
		done
	fi
	
	if [ -e  ${D}/src/${i}/*.sh ]
	then
	
		mkdir -p "${D}/src/${i}/tmp/"
		for j in `ls ${D}/src/${i}/*.sh`
		do 
			${D}/src/${i}/./`basename $j` "${D}/src/${i}/tmp"
		done
		cp -r ${D}/src/${i}/tmp/* "${D}/out/${i}/" && rm -r "${D}/src/${i}/tmp/"
	fi
done
