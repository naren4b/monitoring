package com.nokia.ndac;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.nokia.ndac.bean.SystemParameter;
import com.nokia.ndac.repository.SystemParmeterRepository;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Bean
	CommandLineRunner init(SystemParmeterRepository systemParmeterRepository) {

		return (evt) -> {
			systemParmeterRepository.deleteAll();
			systemParmeterRepository.save(new SystemParameter("name", "Name of the System", ""));
			systemParmeterRepository.save(new SystemParameter("description", "Description of the System", ""));
			systemParmeterRepository.save(new SystemParameter("systemTime", "Local time at the System", ""));
			systemParmeterRepository.save(new SystemParameter("location", "Location of the System", ""));
			systemParmeterRepository.save(new SystemParameter("hostIp", "System IP address", ""));
			systemParmeterRepository.save(new SystemParameter("availableProcessors",
					"Gives the number of processors available to the Java virtual machine. ", ""));
			systemParmeterRepository.save(new SystemParameter("freeMemory",
					"Gives the amount of free memory in the Java Virtual Machine", "bytes"));
			systemParmeterRepository.save(new SystemParameter("maxMemory",
					"Gives the maximum amount of memory that the Java virtual machine will attempt to use.", "bytes"));
			systemParmeterRepository.save(new SystemParameter("totalMemory",
					"Gives the total amount of memory in the Java virtual machine.", "bytes"));

		};
	}
}