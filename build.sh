#!/bin/bash

echo "Compiling Java files..."
javac -cp "libs/pdfbox-app-3.0.4.jar" -d bin src/*.java

if [ $? -eq 0 ]; then
    echo "Compilation successful. Running program..."
    java -cp "bin:libs/pdfbox-app-3.0.4.jar" Main
else
    echo "Compilation failed!"
fi

