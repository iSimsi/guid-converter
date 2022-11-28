import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*

internal class ConvertTest {
    private val testConvert: Convert = Convert("{48ED4993-8F51-406E-8501-64809B4EAEC8}","guid" ,null)

    @Test
    fun testHexToGuid() {
        val expected = "{48ED4993-8F51-406E-8501-64809B4EAEC8}"
        assertEquals(expected, testConvert.hexToGuid("9349ED48518F6E40850164809B4EAEC8"))
    }

    @Test
    fun testGuidToHex() {
        val expected = "9349ED48518F6E40850164809B4EAEC8"
        assertEquals(expected, testConvert.guidToHex("{48ED4993-8F51-406E-8501-64809B4EAEC8}"))
    }

    @Test
    fun testValidateGuid1() {
        val result = testConvert.validateGuid("{48ED4993-8F51-406E-8501-64809B4EAEC8}")
        assertTrue(result)
    }

    @Test
    fun testValidateGuid2() {
        val result = testConvert.validateGuid("48ED4993-8F51-406E-8501-64809B4EAEC8}")
        assertFalse(result)
    }

    @Test
    fun testValidateGuid3() {
        val result = testConvert.validateGuid("{48ED4993-8F51-406E-8501-64809B4EAEC8l")
        assertFalse(result)
    }

    @Test
    fun testValidateGuid4() {
        val result = testConvert.validateGuid("{48ED4993-8F51-406E-85-64809B4EAEC8}")
        assertFalse(result)
    }

    @Test
    fun testValidateHex1() {
        val result = testConvert.validateHex("9349ED48518F6E40850164809B4EAEC8")
        assertTrue(result)
    }

    @Test
    fun testValidateHex2() {
        val result = testConvert.validateGuid("48518F6E40850164809B4EAEC8")
        assertFalse(result)
    }

}