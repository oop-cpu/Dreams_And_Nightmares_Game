#!/bin/bash

# Exit immediately if a command exits with a non-zero status
set -e

APP_NAME="Dreams and Nightmares"
SRC_DIR="swing-cli/src"
BUILD_DIR="build"
DIST_DIR="dist"

echo "=== 1. Cleaning previous builds ==="
rm -rf "$BUILD_DIR" "$DIST_DIR"
mkdir -p "$BUILD_DIR" "$DIST_DIR"

echo "=== 2. Compiling Java source files ==="
# Find all java files and compile them into the build directory
find "$SRC_DIR" -name "*.java" > "$BUILD_DIR/sources.txt"
javac -d "$BUILD_DIR" "@$BUILD_DIR/sources.txt"

echo "=== 3. Copying game assets (images, etc.) ==="
# Copy png files into the build directory so the classloader can find them
find "$SRC_DIR" -name "*.png" -exec cp {} "$BUILD_DIR/" \;

echo "=== 4. Creating executable JAR ==="
# Assuming 'run.class' contains your main method (run.java)
jar cfe "$DIST_DIR/DreamsAndNightmares.jar" -C "$BUILD_DIR" .

echo "=== 5. Creating Launch4j Configuration XML ==="
cat << EOF > "$BUILD_DIR/launch4j.xml"
<launch4jConfig>
  <dontWrapJar>false</dontWrapJar>
  <headerType>gui</headerType>
  <jar>../$DIST_DIR/DreamsAndNightmares.jar</jar>
  <outfile>../$DIST_DIR/Dreams and Nightmares.exe</outfile>
  <errTitle>Dreams and Nightmares Error</errTitle>
  <cmdLine></cmdLine>
  <chdir>.</chdir>
  <priority>normal</priority>
  <downloadUrl>https://java.com/download</downloadUrl>
  <supportUrl></supportUrl>
  <stayAlive>false</stayAlive>
  <restartOnCrash>false</restartOnCrash>
  <manifest></manifest>
  <icon></icon>
  <jre>
    <path></path>
    <requiresJdk>false</requiresJdk>
    <requiresReboot>false</requiresReboot>
    <minVersion>11</minVersion>
    <maxVersion></maxVersion>
    <opt></opt>
  </jre>
</launch4jConfig>
EOF

echo "=== 6. Generating .exe using Launch4j ==="
cd "$BUILD_DIR"
launch4j launch4j.xml
cd ..

echo "=== Build Complete! ==="
echo "Your Windows executable is located at: $DIST_DIR/Dreams and Nightmares.exe"
