#!/bin/bash

# Script to remove closing comments from Java files
# This will remove comments like /* constructor */, /* newInstance */, /* class ClassName */

find /home/mburri/git/siard/SiardCmd/src/main/java -name "*.java" | while read file; do
  # Use sed to remove the closing comments
  # This pattern matches comments that appear at the end of a line, typically after a closing brace or semicolon
  sed -i 's/ \/\* \(constructor\|newInstance\|[a-zA-Z0-9_]*\) \*\///' "$file"
  
  # Also match class closing comments which might be on their own line
  sed -i 's/^} \/\* class [a-zA-Z0-9_.]* \*\/$/}/' "$file"
  
  # Match method closing comments
  sed -i 's/} \/\* [a-zA-Z0-9_()]* \*\/$/}/' "$file"
  
  echo "Processed $file"
done

echo "All closing comments have been removed."
