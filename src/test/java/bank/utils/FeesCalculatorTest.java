package bank.utils;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

// Use of Parameterized helps in this case, since multiple runs of same test are required
class FeesCalculatorTest {
	FeesCalculator calculator = new FeesCalculator();

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	// Tests all the Withdrawal Slices and the expected results are that every test passes
	@ParameterizedTest
	@CsvSource({
			"0, 100, 1000, true, 7",    // If the transaction is performed during the weekend, then there is no fee - Saturday
			"0, 100, 1000, true, 1",    // If the transaction is performed during the weekend, then there is no fee - Sunday
			"0.1, 100, 1000, true, 5",  // Otherwise, the fee is 0.1% of the amount withdrawn. - Friday
			"0.2, 100, 999, false, 5",  // If the balance of the account is less than $1,000, then the fee is 0.2% of the amount withdrawn - accountBalance = $999
			"0.1, 100, 9999, false, 5", // If, however, the balance is $1,000, or more, but less than $10,000, the fee is 0.1% of the amount withdrawn. - accountBalance = $9999
			"0, 100, 99999, false, 5"   // If the balance is more than $10,000, then there is no fee - accountBalance = $99999
	})
	public void withdrawalSliceTest(double expectedFee, double amount, double accountBalance, boolean student, int dayOfWeek) {
		assertEquals(expectedFee, calculator.calculateWithdrawalFee(amount, accountBalance, student, dayOfWeek));
	}

	// Tests the DU-Paths for interestPercentage, all test cases except test case 4 Pass
	@Test
	void DUPathsTest() {
		assertAll("All the DU Path tests",
				() -> assertEquals(0.01, calculator.calculateDepositInterest(1000, 10000, true)/1000),   // Test Case 1
				() -> assertEquals(0.005, calculator.calculateDepositInterest(1000, 100, true)/1000),    // Test Case 2
				() -> assertEquals(0.005, calculator.calculateDepositInterest(100, 10000, true)/100),    // Test Case 3
				() -> assertEquals(0, calculator.calculateDepositInterest(100, 1000, true)/100),         // Test Case 4
				() -> assertEquals(0.01, calculator.calculateDepositInterest(1000, 10000, false)/1000),  // Test Case 5
				() -> assertEquals(0.005, calculator.calculateDepositInterest(1000, 1000, false)/1000),  // Test Case 6
				() -> assertEquals(0.005, calculator.calculateDepositInterest(100, 100000, false)/100),  // Test Case 7
				() -> assertEquals(0, calculator.calculateDepositInterest(100, 1000, false)/100)         // Test Case 8
		);
	}

	// Tests all the independent paths and the expected results are that every tests are to fail
	@Test
	void basisPathTest1() {
		assertAll("All 16 Basis Path Independent Test Paths:",
				() -> assertEquals(0.01, calculator.calculateTransferFee(10, 100, 100, true)),			// Path 1
				() -> assertEquals(0.005, calculator.calculateTransferFee(10, 100, 10000, true)),		    // Path 2
				() -> assertEquals(0.05, calculator.calculateTransferFee(10, 10000, 100, true)),		    // Path 3
				() -> assertEquals(0.025, calculator.calculateTransferFee(10, 10000, 10000, true)),	    // Path 4
				() -> assertEquals(0.5, calculator.calculateTransferFee(1000, 100, 100, true)),		    // Path 5
				() -> assertEquals(0.25, calculator.calculateTransferFee(1000, 100, 10000, true)),		// Path 6
				() -> assertEquals(2.5, calculator.calculateTransferFee(1000, 10000, 100, true)),		    // Path 7
				() -> assertEquals(1.25, calculator.calculateTransferFee(1000, 10000, 10000, true)),	    // Path 8
				() -> assertEquals(0.02, calculator.calculateTransferFee(10, 100, 100, false)),		    // Path 9
				() -> assertEquals(0.01, calculator.calculateTransferFee(10, 100, 10000, false)),		    // Path 10
				() -> assertEquals(0.1, calculator.calculateTransferFee(10, 10000, 100, false)),		    // Path 11
				() -> assertEquals(0.05, calculator.calculateTransferFee(10, 10000, 10000, false)),	    // Path 12
				() -> assertEquals(1, calculator.calculateTransferFee(1000, 100, 100, false)),			// Path 13
				() -> assertEquals(0.5, calculator.calculateTransferFee(1000, 100, 10000, false)),		// Path 14
				() -> assertEquals(5, calculator.calculateTransferFee(1000, 10000, 100, false)),		    // Path 15
				() -> assertEquals(2.5, calculator.calculateTransferFee(1000, 10000, 10000, false)));	    // Path 16
	}
}
