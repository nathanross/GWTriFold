cat srcShared/markdownHeader.html | tee zip/documentation.html > build/BUILD.html
markdown srcBuild/BUILD.md >> build/BUILD.html
markdown srcZip/documentation.md >> zip/documentation.html
srcZip/./genResponsiveFontStyling.sh zip/responsiveCSS/
echo "</body></html>" | tee -a zip/documentation.html >> build/BUILD.html
