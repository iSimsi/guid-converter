# Guid Converter
GUID (globally unique identifier) is a synonym for UUID (universally 
unique identifier).
GUIDs are used by many applications as unique identifier for objects. 
In Oracle GUIDs ar represented using a RAW(16) datatype. Unfortunately, 
the byte order is different from the standard hyphen-separated GUIDs 
used in applications.

This small guid converter app, written in kotlin converts the standard 
hyphen-separated format into RAW(16) format and back. It is possible to
convert single values to stout or values from input file to output file.

## Using as standalone app
To use the app standalone a cli control was implemented.


`java -jar guid-converter-*-jar-with-dependencies.jar`