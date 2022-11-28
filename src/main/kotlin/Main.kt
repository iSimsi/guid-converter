@file:OptIn(ExperimentalCli::class)

import kotlinx.cli.*

fun main(args: Array<String>) {
    val parser = ArgParser("guid-converter")

    // File conversion (Arguments and options for cli parsing)
    class File: Subcommand("file", "Convert the content of input file to output file") {
        val inputFile: String by argument(ArgType.String, description = "Full path to the input file")
        val outputFile: String by argument(ArgType.String, description = "Full path with name of the output file")
        val inputFileFormat: String by option(ArgType.Choice(listOf("guid", "hex"), { it }),
            shortName = "i", description = "Format of the values in the input file"
        ).required()

        override fun execute() {
            Convert(inputFile, inputFileFormat, outputFile)
        }
    }

    // Single conversion (Options for cli parsing)
    class Single: Subcommand("single", "Convert a single String") {
        val inputString: String by option(ArgType.String,
            shortName = "s", description = "String to convert"
        ).required()
        val inputStringFormat: String by option(ArgType.Choice(listOf("guid", "hex"), { it }),
            shortName = "i", description = "Format of the values in the input file"
        ).required()

        override fun execute() {
            Convert(inputString, inputStringFormat, output = null)
        }
    }

    // Parse arguments and options
    val file = File()
    val single = Single()
    parser.subcommands(file, single)
    parser.parse(args)
}
