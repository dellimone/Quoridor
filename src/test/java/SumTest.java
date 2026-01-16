import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SumTest {
    @Test
    public void testAdd() {
        // 1. Setup (Arrange)
        Sum calculator = new Sum();

        // 2. Execute (Act)
        int result = calculator.add(5, 10);

        // 3. Verify (Assert)
        // Check if the result is 15. If not, the test fails.
        assertEquals(15, result, "5 + 10 should equal 15");
    }

}