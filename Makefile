.PHONY: clean build distInstall

WRP=./gradlew

build: ${WRP}
	${WRP} build

installDist: ${WRP}
	${WRP} installDist 

jpackage: ${WRP} icons
	${WRP} jpackage

icons: macIcon winIcon linuxIcon

macIcon:
	mkdir -p /tmp/graphedit-macicon.iconset
	inkscape -o /tmp/graphedit-macicon.iconset/icon_16x16.png -w 16 -h 16 Graphedit/src/main/resources/icon/master.svg
	inkscape -o /tmp/graphedit-macicon.iconset/icon_16x16@2x.png -w 32 -h 32 Graphedit/src/main/resources/icon/master.svg
	inkscape -o /tmp/graphedit-macicon.iconset/icon_32x32.png -w 32 -h 32 Graphedit/src/main/resources/icon/master.svg
	inkscape -o /tmp/graphedit-macicon.iconset/icon_32x32@2x.png -w 64 -h 64 Graphedit/src/main/resources/icon/master.svg
	inkscape -o /tmp/graphedit-macicon.iconset/icon_64x64.png -w 64 -h 64 Graphedit/src/main/resources/icon/master.svg
	inkscape -o /tmp/graphedit-macicon.iconset/icon_64x64@2x.png -w 128 -h 128 Graphedit/src/main/resources/icon/master.svg
	inkscape -o /tmp/graphedit-macicon.iconset/icon_128x128.png -w 128 -h 128 Graphedit/src/main/resources/icon/master.svg
	inkscape -o /tmp/graphedit-macicon.iconset/icon_128x128@2x.png -w 256 -h 256 Graphedit/src/main/resources/icon/master.svg
	inkscape -o /tmp/graphedit-macicon.iconset/icon_256x256.png -w 256 -h 256 Graphedit/src/main/resources/icon/master.svg
	inkscape -o /tmp/graphedit-macicon.iconset/icon_256x256@2x.png -w 512 -h 512 Graphedit/src/main/resources/icon/master.svg
	inkscape -o /tmp/graphedit-macicon.iconset/icon_512x512@2x.png -w 512 -h 512 Graphedit/src/main/resources/icon/master.svg
	inkscape -o /tmp/graphedit-macicon.iconset/icon_512x512@2x.png -w 1024 -h 1024 Graphedit/src/main/resources/icon/master.svg
	iconutil --convert icns --output Graphedit/src/main/resources/icon/graphedit.icns /tmp/graphedit-macicon.iconset
	rm -rf /tmp/graphedit-macicon.iconset

winIcon:
	mkdir -p /tmp/graphedit-winicon
	inkscape -o /tmp/graphedit-winicon/16.png -w 16 -h 16 Graphedit/src/main/resources/icon/master.svg
	inkscape -o /tmp/graphedit-winicon/32.png -w 32 -h 32 Graphedit/src/main/resources/icon/master.svg
	inkscape -o /tmp/graphedit-winicon/64.png -w 64 -h 64 Graphedit/src/main/resources/icon/master.svg
	inkscape -o /tmp/graphedit-winicon/128.png -w 128 -h 128 Graphedit/src/main/resources/icon/master.svg
	inkscape -o /tmp/graphedit-winicon/256.png -w 256 -h 256 Graphedit/src/main/resources/icon/master.svg
	inkscape -o /tmp/graphedit-winicon/512.png -w 512 -h 512 Graphedit/src/main/resources/icon/master.svg
	convert /tmp/graphedit-winicon/16.png /tmp/graphedit-winicon/32.png /tmp/graphedit-winicon/64.png /tmp/graphedit-winicon/128.png /tmp/graphedit-winicon/256.png /tmp/graphedit-winicon/512.png Graphedit/src/main/resources/icon/graphedit.ico
	rm -rf /tmp/graphedit-winicon

linuxIcon:
	inkscape -o Graphedit/src/main/resources/icon/graphedit.png -w 512 -h 512 Graphedit/src/main/resources/icon/master.svg

# NOTE: if you wish to run with arguments, use gradlew run --args="arg1 arg2" instead of this makefile
run: ${WRP}
	${WRP} run --args="-v TRACE"

clean: ${WRP}
	${WRP} clean

${WRP}:
	gradle wrapper

nvimclean: # useful when adding new subprojects, or when the java plugin gets confused
	rm -rf ${WRP}
	rm -rf .project
	rm -rf .classpath

