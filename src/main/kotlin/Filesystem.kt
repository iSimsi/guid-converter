import mu.KotlinLogging
import mu.withLoggingContext
import java.io.File
import kotlin.system.exitProcess
import org.apache.commons.io.FilenameUtils
import java.io.IOException

class Filesystem {
    private val logger = KotlinLogging.logger {}

    fun checkInputFile(inputFile: String) {
        val file = File(inputFile).exists()

        if (!file) {
            withLoggingContext("user" to "checkInputFile") {
                logger.error { "File $inputFile not found" }
            }
            exitProcess(1)
        } else {
            withLoggingContext("user" to "checkInputFile") {
                logger.error { "File $inputFile found" }
            }
        }
    }

    fun checkOutputPath(outputFile: String) {
        val outputPath = FilenameUtils.getPath(outputFile)
        val path = File(outputPath).isDirectory

        if (!path) {
            withLoggingContext("user" to "checkOutputPath") {
                logger.error { "Path $outputPath not found" }
            }
            exitProcess(1)
        } else {
            withLoggingContext("user" to "checkOutputPath") {
                logger.error { "Path $outputPath found" }
            }
        }
    }

    fun readFile(inputFile: String): List<String> {
        try {
            return File(inputFile).bufferedReader().readLines()
        } catch(exception: IOException) {
            withLoggingContext("user" to "readFile") {
                logger.error { "$exception" }
            }
            exitProcess(1)
        }
    }


    fun writeFile(outputFile: String, value: String) {
        try {
            File(outputFile).bufferedWriter().use { out -> out.write(value) }
        } catch (exception: IOException) {
            withLoggingContext("user" to "writeFile") {
                logger.error { "$exception" }
            }
            exitProcess(1)

        }
    }

}