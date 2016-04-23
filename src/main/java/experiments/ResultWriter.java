package experiments;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResultWriter {

	private File file;

	protected static final Logger logger = LoggerFactory.getLogger(ResultWriter.class);

	public ResultWriter(String prefix, String suffix) throws IOException {
		checkDirectory();
		file = File.createTempFile(prefix, suffix, new File("Results"));
	}

	public void writeLine(String line) {
		List<String> lines = Arrays.asList(line);

		try {
			Files.write(file.toPath(), lines, Charset.forName("UTF-8"), StandardOpenOption.APPEND);
		} catch (IOException e) {
			
			e.printStackTrace();
		}


	}

	private void checkDirectory() {
		Path path = Paths.get("Results");

		//if directory exists?
		if (!Files.exists(path)) {
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				//fail to create directory
				e.printStackTrace();
			}

		}


	}
}
