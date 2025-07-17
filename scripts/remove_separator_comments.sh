#!/bin/bash

# Script to remove separator comments from Java files
# This will remove comments like /*------------------------------------------------------------------*/
# and /*====================================================================*/

find /home/mburri/git/siard/SiardCmd/src/main/java -name "*.java" | while read file; do
  # Use sed to remove the separator comments
  
  # Remove simple separator comments like /*------------------------------------------------------------------*/
  sed -i 's/[[:space:]]*\/\*[-]*\*\/[[:space:]]*//g' "$file"
  
  # Remove complex separator comments with text inside like /*==== text ====*/
  sed -i 's/[[:space:]]*\/\*[=]*[[:space:]]*[a-zA-Z0-9 ()]*[[:space:]]*[=]*\*\/[[:space:]]*//g' "$file"
  
  # Remove multi-line separator comments
  sed -i '/\/\*[=]\{5,\}/,/[=]\{5,\}\*\//d' "$file"
  
  echo "Processed $file"
done

echo "All separator comments have been removed."
