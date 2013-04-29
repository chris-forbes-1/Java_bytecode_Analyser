package example.code;

import java.io.File;

import analysis.bytecode.ByteCodeAnalyzer_Code;

public class TestMe {
	public static void main(String[] args){
		TestMe();
		//ByteCodeAnalyzer_Code bc = new ByteCodeAnalyzer_Code(new File("bin/example/code/TestMe.class"));
	}
	public static void TestMe() {
		
		
		
		int x = 1; 
		int y = 2;
		int z = 3;
		
		
		int a = x + y;
		int b = x + z;
		int c = y + z;

		for (int i = 0; i < 10; i++) {
			//System.out.println(a);
		}
		for (int index = 0; index < 10; index++) {
			//System.out.println(b);
		}
		for (int xc = 0; xc < 10; xc++) {
			//System.out.println(c);
		}
		do{
		a++;
		b++;
		c++;
		x++;
		y++;
		z++;
		}while(a != 10);
		
		if (x > a || y > b || z > c){
			System.out.println("Test Complete");
		}else{
			System.out.println("The other test complete");
		}
	}
	
	
}
