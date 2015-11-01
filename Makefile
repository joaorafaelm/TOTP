# JAVAC VERSION: 1.6.0_65
BUILD_DIR = build/
SOURCE_DIR = source/

FILES = Client.java Server.java
		
CLASS_FILES = $(addprefix $(BUILD_DIR), $(FILES:.java=.class))

all: $(CLASS_FILES)

$(BUILD_DIR)%.class: $(SOURCE_DIR)%.java
	javac -d $(BUILD_DIR) -classpath $(SOURCE_DIR) $<

clean:
	rm -f $(BUILD_DIR)*.class
	rm -f $(SOURCE_DIR)*.class	
