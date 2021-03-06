# Physical simulations
- Code is written in java 11, but should work with 8+
- src/ contains all source code
- jars/ contain all libraries bundled as jars
    - processing is used as a library

# Compilation (on linux)
- Open a terminal with current directory as the one containing this file
- Use `javac -cp "jars/*" -d build/ src/*.java src/*/*.java` to compile and put all output class files under build/
- Use `java -cp "build/:jars/*" demos.<simulation>` to run any simulation
- Use `java -cp "build/:jars/*" Main` to run main simulation

# Compilation (on windows)
- Use ant for building. Install ant.
- Open a terminal in current directory and run `ant compile` to build the project.
- To execute the **full cloth simulation** run this command next `java -cp "bin/;jars/*" Main`
- To execute any **demo simulation** run this command `java -cp "bin/;jars/*" demos.<simulation>`

# Things used
- Queasy cam
- Cam written by Liam Tyler
- https://stackoverflow.com/a/36425155/4031302 for distance from point to line segment
