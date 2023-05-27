.PHONY: clean build distInstall

WRP=./gradlew

build: ${WRP}
	${WRP} build

installDist: ${WRP}
	${WRP} installDist 

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

