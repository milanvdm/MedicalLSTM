package util;

import java.io.File;
import java.io.IOException;

import org.springframework.core.io.ClassPathResource;

public class GetRelativePath {

	public static void main(String[] args) throws IOException {
		
		
		ClassPathResource resource = new ClassPathResource("/home/milan/workspace/MedicalLSTM/Dataset/diabetes2.csv");
        File file = new File("/home/milan/workspace/MedicalLSTM/Dataset/diabetes2.csv");
		
		System.out.println(file.exists());

	}

}
