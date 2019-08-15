package Test;
import junit.framework.TestCase;

public class TestSomaMaiorMenor {
	public static void main(String[] args) {
		TestLargest.assertEquals(9, SomaMaiorMenor.MaiorMenor(new int[] {3, 7, 8, 1, 9, 10}));
	}
}
