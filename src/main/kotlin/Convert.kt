import kotlinx.coroutines.*
import mu.KotlinLogging
import mu.withLoggingContext
import kotlinx.coroutines.channels.*
import kotlin.system.measureTimeMillis

/**
 * Represents the conversion task
 *
 * @param input the input as string or path to input file as string
 * @param inputFormat the input format as string
 * @param output the output file as String (nullable)
 * @param threads teh count of threads to use for file conversion
 *
 * @author <a href="https://github.com/iSimsi/">iSimsi</a>
 * @since 1.0.0
 */
@OptIn(ExperimentalCoroutinesApi::class)
class Convert (input: String, private var inputFormat: String, var output: String?, threads: Int) {
    private val logger = KotlinLogging.logger {}
    private val conversionExecutors = Dispatchers.IO.limitedParallelism(threads)

    /**
     * The constructor
     * @since 1.0.0
     */
    init {
        if (output == null) {
            singleConversion(input)
        } else {
            fileConversion(input, output!!)
        }
    }

    /**
     * Calculates the output format
     *
     * @return the output format
     * @since 1.0.0
     */
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

    /**
     * Validates the format of a guid
     *
     * @param guid the guid to validate as string
     * @return true if the guid is valid
     * @since 1.0.0
     */
    internal fun validateGuid(guid: String): Boolean {
        return guid.length == 38 && guid.startsWith("{") && guid.endsWith("}")
    }

    /**
     * Validates the format of a hex value
     *
     * @param hex the guid to validate as string
     * @return true if the hex value is valid
     * @since 1.0.0
     */
    internal fun validateHex(hex: String): Boolean {
        return hex.length == 32
    }

    /**
     * Converts a guid value to a hex value
     *
     * @param guid the guid to convert as string
     * @return the hex value
     * @since 1.0.0
     */
    internal fun guidToHex(guid: String): String {
        return (guid.substring(7, 9) + guid.substring(5, 7) + guid.substring(3, 5) + guid.substring(1, 3)
                + guid.substring(12, 14) + guid.substring(10, 12) + guid.substring(17, 19) + guid.substring(15, 17)
                + guid.substring(20, 24) + guid.substring(25, 37))
    }

    /**
     * Converts a hex value to a guid value
     *
     * @param hex the guid to convert as string
     * @return the guid value
     * @since 1.0.0
     */
    internal fun hexToGuid(hex: String): String {
        return ("{" + hex.substring(6, 8) + hex.substring(4, 6) + hex.substring(2, 4) + hex.substring(0, 2) + "-"
                + hex.substring(10, 12) + hex.substring(8, 10) + "-" + hex.substring(14, 16) + hex.substring(12, 14)
                + "-" + hex.substring(16, 20) + "-" + hex.substring(20, 32) + "}")
    }

    /**
     * Executes multithreaded conversions for file hex conversion
     *
     * @param valuesHex the hex values as a list of strings
     * @return the guid values as a list of strings
     * @throws Exception if input hex value validation failed
     * @throws Exception if output guid value validation failed
     * @since 1.1.0
     */
    private suspend fun executeHexConversion(valuesHex: List<String>): List<String> = withContext(conversionExecutors){
        val conversionChannel = Channel<String>()
        val outputListGuid = mutableListOf<String>()

        for (valueHex in valuesHex) {
            launch(conversionExecutors) {
                runBlocking {
                    if (!validateHex(valueHex)) { // check format of hex value
                        withLoggingContext("user" to "executeHexConversion") {
                            logger.error { "Validation of input hex value $valueHex failed" }
                        }
                        throw Exception("Validation of input hex value $valueHex failed")
                    } else {
                        val valueGuid = hexToGuid(valueHex) // Convert
                        if (!validateGuid(valueGuid)) { // check format of guid value
                            withLoggingContext("user" to "executeHexConversion") {
                                logger.error { "Validation of output guid value $valueGuid failed" }
                            }
                            throw Exception("Validation of output guid value $valueGuid failed")
                        } else {
                            conversionChannel.send(valueGuid)
                            withLoggingContext("user" to "executeHexConversion") {
                                logger.debug { "Conversion of hex value $valueHex to guid value $valueGuid was successful" }
                            }
                        }
                    }
                }
            }
            outputListGuid.add(conversionChannel.receive())
        }
        return@withContext outputListGuid
    }

    /**
     * Executes multithreaded conversions for file guid conversion
     *
     * @param valuesGuid the guid values as a list of strings
     * @return the hex values as a list of strings
     * @throws Exception if input guid value validation failed
     * @throws Exception if value output hex validation failed
     * @since 1.1.0
     */
    private suspend fun executeGuidConversion(valuesGuid: List<String>) = withContext(conversionExecutors) {
        val conversionChannel = Channel<String>()
        val outputListHex = mutableListOf<String>()

        for (valueGuid in valuesGuid) {
            launch(conversionExecutors) {
                runBlocking {
                    if (!validateGuid(valueGuid)) { // check format of guid value
                        withLoggingContext("user" to "executeGuidConversion") {
                            logger.error("Validation of input guid value $valueGuid failed")
                        }
                        throw Exception("Validation of input guid value $valueGuid failed")
                    } else {
                        val valueHex = guidToHex(valueGuid) // Convert
                        if (!validateHex(valueHex)) { // check format of hex value
                            withLoggingContext("user" to "executeGuidConversion") {
                                logger.error("Validation of output hex value $valueHex failed")
                            }
                            throw Exception("Validation of output hex value $valueHex failed")
                        } else {
                            conversionChannel.send(valueHex)
                            withLoggingContext("user" to "executeGuidConversion") {
                                logger.debug("Conversion of guid value $valueGuid to hex value $valueHex was successful")
                            }
                        }
                    }
                }
            }
            outputListHex.add(conversionChannel.receive())
        }
        return@withContext outputListHex
    }

    /**
     * The process for single conversion
     *
     * @param input the guid or hex value to convert
     * @throws Exception if value validation failed
     * @since 1.0.0
     */
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

    /**
     * The process for file conversion
     *
     * @param input the input file to process
     * @param output the output file
     * @since 1.0.0
     */
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
            val countHex = valuesHex.count()
            withLoggingContext("user" to "fileConversion") {
                logger.info { "Found $countHex guids in input file $input" }
            }

            // Execute the conversion
            val runTimeInMillis = measureTimeMillis {
                val outputListGuid = executeHexConversion(valuesHex)
                filesystem.writeFile(output, outputListGuid, countHex) // Write list of values to file
            }

            coroutineContext.cancelChildren()

            withLoggingContext("user" to "fileConversion") {
                logger.info { "Finished conversion process" }
            }

            withLoggingContext("user" to "fileConversion") {
                logger.info { "Runtime: $runTimeInMillis milliseconds" }
            }

        } else {
            withLoggingContext("user" to "fileConversion") {
                logger.info { "Output format is hex" }
            }

            // Read input file
            val valuesGuid = filesystem.readFile(input)
            val countGuids = valuesGuid.count()
            withLoggingContext("user" to "fileConversion") {
                logger.info { "Found $countGuids guids in input file $input" }
            }

            // Execute the conversion
            val runTimeInMillis = measureTimeMillis {
                val outputListHex = executeGuidConversion(valuesGuid)
                filesystem.writeFile(output, outputListHex, countGuids) // Write list of values to file
            }

            coroutineContext.cancelChildren()

            withLoggingContext("user" to "fileConversion") {
                logger.info { "Finished conversion process" }
            }

            withLoggingContext("user" to "fileConversion") {
                logger.info { "Runtime: $runTimeInMillis milliseconds" }
            }
        }
    }
}