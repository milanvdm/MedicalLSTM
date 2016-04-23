package testing;

import java.io.IOException;

import experiments.ResultWriter;

public class WritingTest {

	public static void main(String[] args) throws IOException {
		ResultWriter writer = new ResultWriter("test", "params: ");
		
		writer.writeLine("test");
		writer.writeLine("Miln");

	}

}
