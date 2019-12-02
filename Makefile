SRC_DIR=src/
BASE_PKG=com/imperative/

imperative:
	@javac $(SRC_DIR)$(BASE_PKG)*.java -d build/

jar: imperative
	@cd build ; jar cfm ../imperative.jar ../Manifest.txt $(BASE_PKG)*.class

.PHONY: clean

clean:
	@rm -r build/ imperative.jar
