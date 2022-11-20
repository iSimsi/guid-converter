import kotlinx.cli.ArgParser
import kotlinx.cli.ArgType
import kotlinx.cli.optional
import kotlinx.cli.required

fun sayHello(
    inputFile: String?,
    outputFile: String?,
    inputFormat: String,
    outputFormat: String,
    inputString: String?
) {
    println("Given inputFile: $inputFile")
    println("Given outputFile: $outputFile")
    println("Given inputFormat: $inputFormat")
    println("Given outputFormat: $outputFormat")
    println("Given inputString: $inputString")
}


fun main(args: Array<String>) {
    val parser = ArgParser("guid.converter")

    // Arguments
    val inputFile: String? by parser.argument(ArgType.String, description = "Input file").optional()
    val outputFile: String? by parser.argument(ArgType.String, description = "Output file").optional()

    // Options
    val inputFormat: String by parser.option(
        ArgType.Choice(listOf("guid", "hex"), { it }),
        shortName = "i", description = "Input Format"
    ).required()
    val outputFormat: String by parser.option(
        ArgType.Choice(listOf("guid", "hex"), { it }),
        shortName = "o", description = "Output Format"
    ).required()
    val inputString: String? by parser.option(
        ArgType.String,
        shortName = "s", description = "Convert a single String"
    )

    parser.parse(args)

    sayHello(inputFile, outputFile, inputFormat, outputFormat, inputString)
}
