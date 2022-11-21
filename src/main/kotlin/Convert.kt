import mu.KotlinLogging
import mu.withLoggingContext
import kotlin.system.exitProcess

class Convert (input: String, private var inputFormat: String, var output: String?) {
    private val logger = KotlinLogging.logger {}

    init {
        if (output == null) {
            singleConversion(input)
        }
        else {
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
                exitProcess(1)
            }
        }
        return format
    }

    private fun validateGuid(guid: String): Boolean {
        return guid.length == 38 && guid.startsWith("{") && guid.endsWith("}")
    }

    private fun validateHex(hex: String): Boolean {
        return hex.length == 32
    }

    private fun guidToHex(guid: String): String {
        return (guid.substring(7, 9) + guid.substring(5, 7) + guid.substring(3, 5) + guid.substring(1, 3)
                + guid.substring(12, 14) + guid.substring(10, 12) + guid.substring(17, 19) + guid.substring(15, 17)
                + guid.substring(20, 24) + guid.substring(25, 37))
    }

    private fun hexToGuid(hex: String): String {
        return ("{" + hex.substring(6, 8) + hex.substring(4, 6) + hex.substring(2, 4) + hex.substring(0, 2) + "-"
                + hex.substring(10, 12) + hex.substring(8, 10) + "-" + hex.substring(14, 16) + hex.substring(12, 14)
                + "-" + hex.substring(16, 20) + "-" + hex.substring(20, 32) + "}")
    }

    private fun singleConversion(input: String) {
        if (calcOutputFormat() == "guid") {
            if (!validateHex(input))  {
                withLoggingContext("user" to "singleConversion") {
                    logger.error { "Validation of hex value $input failed" }
                }
                exitProcess(1)
            } else {
                val guidOutput = hexToGuid(input)
                println(guidOutput)
                withLoggingContext("user" to "singleConversion") {
                    logger.info { "Conversion of hex value $input to guid value $guidOutput was successful" }
                }
                exitProcess(0)
            }
        }
        else {
            if (!validateGuid(input)) {
                withLoggingContext("user" to "singleConversion") {
                    logger.error { "Validation of guid value $input failed" }
                }
                exitProcess(1)
            }
            val hexOutput = guidToHex(input)
            println(hexOutput)
            withLoggingContext("user" to "singleConversion") {
                logger.info { "Conversion of guid value $input to hex value $hexOutput was successful" }
            }
            exitProcess(0)
        }
    }

    private fun fileConversion(input: String, output: String) {
        val filesystem = Filesystem()

        filesystem.checkInputFile(input)
        filesystem.checkOutputPath(output)

        if (calcOutputFormat() == "guid") {
            val valuesHex = filesystem.readFile(input)
            for (valueHex in valuesHex) {
                if (!validateHex(valueHex)) {
                    withLoggingContext("user" to "fileConversion") {
                        logger.error { "Validation of hex value $valueHex failed" }
                    }
                    exitProcess(1)
                } else {
                    val valueGuid = hexToGuid(valueHex)
                    filesystem.writeFile(output, valueGuid)
                    withLoggingContext("user" to "fileConversion") {
                        logger.info { "Conversion of hex value $input to guid value $valueGuid was successful" }
                    }
                }
            }
            exitProcess(0)
        } else {
            val valuesGuid = filesystem.readFile(input)
            for (valueGuid in valuesGuid) {
                if (!validateHex(valueGuid)) {
                    withLoggingContext("user" to "fileConversion") {
                        logger.error { "Validation of hex value $valueGuid failed" }
                    }
                    exitProcess(1)
                } else {
                    val valueHex = guidToHex(valueGuid)
                    filesystem.writeFile(output, valueHex)
                    withLoggingContext("user" to "fileConversion") {
                        logger.info { "Conversion of hex value $input to guid value $valueHex was successful" }
                    }
                }
            }
            exitProcess(0)
        }
    }
}