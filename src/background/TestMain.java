package background;

import java.io.File;
import java.io.IOException;

public class TestMain {
	
	public static void main(String args[]) throws IOException {
		System.out.println(System.getProperty("user.dir"));
		String path = "TestData/";
		String fileName = "test9.txt";
		File file = new File(path + fileName);
		
		if (!file.exists()) {
			System.out.println("the file doesn't exit!");
			return;
		}
		System.out.println(file.getPath());
		MainCompiler.mainCompiler(path + fileName);
	}
}
