.PHONY: clean build distInstall

WRP=./gradlew

build: ${WRP}
	${WRP} build

installDist: ${WRP}
	${WRP} installDist 

run: ${WRP}
	${WRP} run

clean: ${WRP}
	${WRP} clean

${WRP}:
	gradle wrapper

nvimclean: # useful when adding new subprojects, or when the java plugin gets confused
	rm -rf ${WRP}
	rm -rf .project
	rm -rf .classpath

