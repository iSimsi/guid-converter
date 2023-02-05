# Guid converter
GUID (globally unique identifier) is a synonym for UUID (universally 
unique identifier). GUIDs are used by many applications as unique identifier for objects. 
In Oracle GUIDs ar represented using a RAW(16) datatype. Unfortunately, 
the byte order is different from the standard hyphen-separated GUIDs 
used in applications.

This small guid converter app, written in kotlin converts the standard 
hyphen-separated format into RAW(16) format and back. It is possible to
convert single values to stout or values from input file to output file.

## Using as standalone app
To use the guid converter as standalone app a cli control is implemented. 
If you call the app without subcommand/option, nothing will happen.

### Display the CLI help
```
$ java -jar guid-converter-[version]-jar-with-dependencies.jar --help
Usage: guid-converter options_list
Subcommands:
file - Convert the content of input file to output file
single - Convert a single String

Options:
--help, -h -> Usage info

$ java -jar guid-converter-[version]-jar-with-dependencies.jar single --help
Usage: guid-converter single options_list
Options:
    --inputString, -s -> String to convert (always required) { String }
    --inputStringFormat, -i -> Format of the values in the input file (always required) { Value should be one of [guid, hex] }
    --help, -h -> Usage info


$ java -jar guid-converter-[version]-jar-with-dependencies.jar file --help
Usage: guid-converter file options_list
Arguments:
    inputFile -> Full path to input file { String }
    outputFile -> Full path to output file { String }
Options:
    --inputFileFormat, -i -> Format of the values in the input file (always required) { Value should be one of [guid, hex] }
    --threadCount, -t [1] -> Count of Threads for file conversion { Int }
    --help, -h -> Usage info

```

## Import the package in your own project
### Maven
```
<dependency>
    <groupId>com.isimsi</groupId>
    <artifactId>guid-converter</artifactId>
    <version>1.2.1</version>
</dependency> 
```
