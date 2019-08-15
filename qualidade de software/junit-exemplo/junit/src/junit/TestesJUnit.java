package junit;

public class TestesJUnit {
	  public int evaluate(String expression) {
	    int sum = 0;
	    for (String summand: expression.split("\\+"))
	      sum += Integer.valueOf(summand);
	    return sum;
	  }
	  
	  public int MaiorMenor(int[] list) {
			int index, min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
			for(index = 0; index < list.length-1; index++) {
				if(list[index] < min) {
					min = list[index];
				}if(list[index] > max) {
					max = list[index];
				}
			}
			return (max + min);
	 }
}