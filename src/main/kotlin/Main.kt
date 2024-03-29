import kotlinx.cli.*

/**
 * Main class of the app.
 *
 * Parses the arguments passed from CLI and executes a file conversion or single conversion
 * from hex to guid, or from guid to hex.
 *
 * @param args the arguments passed from cli
 * @author <a href="https://github.com/iSimsi/">iSimsi</a>
 * @since 1.0.0
 * @see <a href="https://github.com/Kotlin/kotlinx-cli">https://github.com/Kotlin/kotlinx-cli</a>
 */

@OptIn(ExperimentalCli::class)
fun main(args: Array<String>) {
    val parser = ArgParser("guid-converter")

    // File conversion (Arguments and options for cli parsing)
    class File: Subcommand("file", "Convert the content of input file to output file") {
        val inputFile: String by argument(ArgType.String, description = "Full path to the input file")
        val outputFile: String by argument(ArgType.String, description = "Full path with name of the output file")
        val inputFileFormat: String by option(ArgType.Choice(listOf("guid", "hex"), { it }),
            shortName = "i", description = "Format of the values in the input file"
        ).required()
        val threadCount: Int by option(ArgType.Int, shortName = "t",
            description = "Count of Threads for file conversion"
        ).default(1)

        override fun execute() {
            Convert(inputFile, inputFileFormat, outputFile, threadCount)
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
            Convert(inputString, inputStringFormat, output = null, threads = 1)
        }
    }

    // Parse arguments and options
    val file = File()
    val single = Single()
    parser.subcommands(file, single)
    parser.parse(args)
}
