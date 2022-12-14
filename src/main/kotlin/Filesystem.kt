import mu.KotlinLogging
import mu.withLoggingContext
import java.io.*
import org.apache.commons.io.FilenameUtils

class Filesystem {
    private val logger = KotlinLogging.logger {}

    fun checkInputFile(inputFile: String) {
        val file = File(inputFile).exists()
        if (!file) {
            withLoggingContext("user" to "checkInputFile") {
                logger.error { "File $inputFile not found" }
            }
            throw Exception("File $inputFile not found")
        } else {
            withLoggingContext("user" to "checkInputFile") {
                logger.info { "File $inputFile found" }
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
            throw Exception("Path $outputFile not found")
        } else {
            withLoggingContext("user" to "checkOutputPath") {
                logger.info { "Path $outputPath found" }
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
            throw exception
        }
    }

    fun writeFile(outputFile: String, outputList: List<String>) {
        try {
            val fileWriter = FileWriter(outputFile)
            val bufferedWriter = BufferedWriter(fileWriter)
            for (value in outputList) {
                bufferedWriter.write(value)
                bufferedWriter.newLine()
            }
            bufferedWriter.close()
        } catch (exception: IOException) {
            withLoggingContext("user" to "writeFile") {
                logger.error { "$exception" }
            }
            throw exception
        }
    }

}