import mu.KotlinLogging
import mu.withLoggingContext
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors
import kotlin.system.measureTimeMillis

class Convert (input: String, private var inputFormat: String, var output: String?, threads: Int) {
    private val logger = KotlinLogging.logger {}
    private val conversionExecutors = Executors.newFixedThreadPool(threads).asCoroutineDispatcher()

    init {
        if (output == null) {
            singleConversion(input)
        } else {
            fileConversion(input, output!!)
        }
    }

    private fun calcOutputFormat(): String {
        val format = when (inputFormat) {
            "hex" -> "guid"
            "guid" -> "hex"
            else -> {
                withLoggingContext("user" to "calcOutputFormat") {
                    logger.error { "Unknown format" }
                }
                throw Exception("Unknown format")
            }
        }
        return format
    }

    internal fun validateGuid(guid: String): Boolean {
        return guid.length == 38 && guid.startsWith("{") && guid.endsWith("}")
    }

    internal fun validateHex(hex: String): Boolean {
        return hex.length == 32
    }

    internal fun guidToHex(guid: String): String {
        return (guid.substring(7, 9) + guid.substring(5, 7) + guid.substring(3, 5) + guid.substring(1, 3)
                + guid.substring(12, 14) + guid.substring(10, 12) + guid.substring(17, 19) + guid.substring(15, 17)
                + guid.substring(20, 24) + guid.substring(25, 37))
    }

    internal fun hexToGuid(hex: String): String {
        return ("{" + hex.substring(6, 8) + hex.substring(4, 6) + hex.substring(2, 4) + hex.substring(0, 2) + "-"
                + hex.substring(10, 12) + hex.substring(8, 10) + "-" + hex.substring(14, 16) + hex.substring(12, 14)
                + "-" + hex.substring(16, 20) + "-" + hex.substring(20, 32) + "}")
    }

    private fun executeHexConversion(valuesHex: List<String>) = runBlocking {
        val filesystem = Filesystem()
        val outputListGuid = mutableListOf<String>()

        for (valueHex in valuesHex) {
            launch(conversionExecutors) {
                runBlocking {
                    if (!validateHex(valueHex)) { // check format of hex value
                        withLoggingContext("user" to "executeHexConversion") {
                            logger.error { "Validation of hex value $valueHex failed" }
                        }
                        throw Exception("Validation of hex value $valueHex failed")
                    } else {
                        val valueGuid = hexToGuid(valueHex) // Convert
                        outputListGuid.add(valueGuid)
                        filesystem.writeFile(output!!, outputListGuid) // Write list of values to file
                        withLoggingContext("user" to "executeHexConversion") {
                            logger.debug { "Conversion of hex value $valueHex to guid value $valueGuid was successful" }
                        }
                    }
                }
            }
        }
    }

    private fun executeGuidConversion(valuesGuid: List<String>) = runBlocking {
        val filesystem = Filesystem()
        val outputListHex= mutableListOf<String>()

        for (valueGuid in valuesGuid) {
            launch(conversionExecutors) {
                runBlocking {
                    if (!validateGuid(valueGuid)) { // check format of guid value
                        withLoggingContext("user" to "executeGuidConversion") {
                            logger.error { "Validation of hex value $valueGuid failed" }
                        }
                        throw Exception("Validation of hex value $valueGuid failed")
                    } else {
                        val valueHex = guidToHex(valueGuid) // Convert
                        outputListHex.add(valueHex)
                        filesystem.writeFile(output!!, outputListHex) // Write list of values to file
                        withLoggingContext("user" to "executeGuidConversion") {
                            logger.debug { "Conversion of guid value $valueGuid to hex value $valueHex was successful" }
                        }
                    }
                }
            }
        }
    }

    private fun singleConversion(input: String) {
        withLoggingContext("user" to "singleConversion") {
            logger.debug { "Starting conversion process" }
        }

        if (calcOutputFormat() == "guid") { // calculate output format
            withLoggingContext("user" to "singleConversion") {
                logger.debug { "Output format is guid" }
            }

            if (!validateHex(input))  { // check format of hex value
                withLoggingContext("user" to "singleConversion") {
                    logger.error { "Validation of hex value $input failed" }
                }
                throw Exception("Validation of hex value $input failed")
            } else {
                val guidOutput = hexToGuid(input) // Convert
                println(guidOutput)
                withLoggingContext("user" to "singleConversion") {
                    logger.debug { "Conversion of hex value $input to guid value $guidOutput was successful" }
                    logger.debug { "Finished conversion process" }
                }
            }
        }
        else {
            withLoggingContext("user" to "singleConversion") {
                logger.debug { "Output format is hex" }
            }

            if (!validateGuid(input)) { // check format of guid value
                withLoggingContext("user" to "singleConversion") {
                    logger.error { "Validation of guid value $input failed" }
                }
                throw Exception("Validation of guid value $input failed")
            }
            val hexOutput = guidToHex(input) // convert
            println(hexOutput)
            withLoggingContext("user" to "singleConversion") {
                logger.debug { "Conversion of guid value $input to hex value $hexOutput was successful" }
                logger.debug { "Finished conversion process" }
            }
        }
    }

    private fun fileConversion(input: String, output: String) = runBlocking {
        val filesystem = Filesystem()

        // Check input file and output path
        filesystem.checkInputFile(input)
        filesystem.checkOutputPath(output)

        withLoggingContext("user" to "fileConversion") {
            logger.info { "Starting conversion process" }
        }

        if (calcOutputFormat() == "guid") { // calculate output format
            withLoggingContext("user" to "fileConversion") {
                logger.info { "Output format is guid" }
            }

            // Read input file
            val valuesHex = filesystem.readFile(input)

            // Execute the conversion
            val runTimeInMillis = measureTimeMillis {
                executeHexConversion(valuesHex)
            }

            withLoggingContext("user" to "fileConversion") {
                logger.info { "Finished conversion process" }
            }

            val runTime = runTimeInMillis / 1000

            withLoggingContext("user" to "fileConversion") {
                logger.info { "Runtime: $runTime seconds" }
            }

            conversionExecutors.close()
        } else {
            withLoggingContext("user" to "fileConversion") {
                logger.info { "Output format is hex" }
            }

            // Read input file
            val valuesGuid = filesystem.readFile(input)

            // Execute the conversion
            val runTimeInMillis = measureTimeMillis {
                executeGuidConversion(valuesGuid)
            }

            withLoggingContext("user" to "fileConversion") {
                logger.info { "Finished conversion process" }
            }

            val runTime = runTimeInMillis / 1000

            withLoggingContext("user" to "fileConversion") {
                logger.info { "Runtime: $runTime seconds" }
            }

            conversionExecutors.close()
        }
    }
}