import mu.KotlinLogging
import mu.withLoggingContext
import java.io.*
import org.apache.commons.io.FilenameUtils

/**
 * Provides Methods to interact with the Filesystem
 *
 * @author <a href="https://github.com/iSimsi/">iSimsi</a>
 * @since 1.0.0
 */
class Filesystem {
    private val logger = KotlinLogging.logger {}

    /**
     * Checks if the input file exists
     *
     * @param inputFile the path to the input file as String
     * @throws IOException if the input file is not found
     * @since 1.0.0
     */
    fun checkInputFile(inputFile: String) {
        val file = File(inputFile).exists()
        if (!file) {
            withLoggingContext("user" to "checkInputFile") {
                logger.error { "File $inputFile not found" }
            }
            throw IOException("File $inputFile not found")
        } else {
            withLoggingContext("user" to "checkInputFile") {
                logger.info { "File $inputFile found" }
            }
        }
    }

    /**
     * Checks if the output folder exists
     *
     * @param outputFile the path to the output file as String
     * @throws IOException if the folder is not found
     * @since 1.0.0
     */
    fun checkOutputPath(outputFile: String) {
        val outputPath = FilenameUtils.getPath(outputFile)
        val path = File(outputPath).isDirectory

        if (!path) {
            withLoggingContext("user" to "checkOutputPath") {
                logger.error { "Path $outputPath not found" }
            }
            throw IOException("Path $outputFile not found")
        } else {
            withLoggingContext("user" to "checkOutputPath") {
                logger.info { "Path $outputPath found" }
            }
        }
    }

    /**
     * Reads the content of the input file
     *
     * @param inputFile the path to the input file as string
     * @throws IOException if the input file is not found
     * @since 1.0.0
     */
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

    /**
     * Writes the content to the output file
     *
     * @param outputFile the path to the output file as string
     * @param outputList the values to write as a list of strings
     * @param count the expected number of values to write as integer
     * @throws IOException if an I/O error occurs
     * @since 1.0.0
     */
    fun writeFile(outputFile: String, outputList: List<String>, count: Int) {
        try {
            val fileWriter = FileWriter(outputFile)
            val bufferedWriter = BufferedWriter(fileWriter)

            var writeCounter = 0

            val countOutputList = outputList.count()
            withLoggingContext("user" to "writeFile") {
                logger.info("Received $countOutputList values to write to the file $outputFile")
            }

            for (value in outputList) {
                bufferedWriter.write(value)
                bufferedWriter.newLine()
                writeCounter += 1
            }
            bufferedWriter.close()

            if (writeCounter == count) {
                withLoggingContext("user" to "writeFile") {
                    logger.info("$writeCounter values were successfully written to the file $outputFile")
                }
            } else {
                withLoggingContext("user" to "writeFile") {
                    logger.warn("Number of values written to file $writeCounter are not equal to the expected number of $count")
                }
            }
        } catch (exception: IOException) {
            withLoggingContext("user" to "writeFile") {
                logger.error("$exception")
            }
            throw exception
        }
    }
}