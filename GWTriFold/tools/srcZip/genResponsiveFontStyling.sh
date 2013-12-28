#!/bin/bash
#copyright 2013 nathan ross, licensed under apache2
#Even FF25 has trouble up scaling non-percentage, non-em fonts. Converted here.

#WORKS FINE ON CYGWIN.

#path to the gwt-user packages's /com/google/gwt/user/theme/ directory 

mkdir -p /tmp/gwt-user
unzip -qq -n "$GWT_HOME/gwt-user.jar" -d /tmp/gwt-user/
pathToThemeDir="/tmp/gwt-user/com/google/gwt/user/theme/"

for themename in dark standard clean chrome
do 
cssSrc="${pathToThemeDir}/${themename}/public/gwt/${themename}/${themename}.css"
grep -iE  "^(\*|\.?[a-zA-Z]|\s*\}|\s*font-size)" ${cssSrc} |  sed -r "s/:\s*x-small;/:62.5%;/" | sed -r "s/:\s*small;/:80%;/" | sed -r "s/:\s*medium;/:100%;/" | sed -r "s/:\s*large;/:112.5%;/" | sed -r "s/:\s*x-large;/:150%;/" | sed -r "s/:\s*xx-large;/:200%;/" | gawk '{ if (/font-size.*px/) { print gensub(/([^0-9]*?)([0-9]*)px(.*)/, "\\1", "g") int(gensub(/([^0-9]*?)([0-9]*)px(.*)/,"\\2","g",$0))*(100/16) "%;"; } else { print } }' | gawk '{ if (/\{\s*$/) {printf "%s 00000", $0 } else if (/,$/) { printf "%s 11111", $0 } else { print }}' | gawk '{ if (!(/\{[ 0]+\}/)) { print } }' | sed -r "s/(00000|11111)/\n/g" > "$1/responsive_fontsize_${themename}.css"
done
