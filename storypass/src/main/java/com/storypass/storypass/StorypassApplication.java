package com.storypass.storypass;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StorypassApplication {

	public static void main(String[] args) {

		// inside try catch for hosting purposes,
		// so that it is able to create the docker file without the db.env file present
		try {
			//import db and other parameters from db.env
			Dotenv dotenv = Dotenv.configure().filename("storypass/db.env").load();
			dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
		} catch (Exception e) {
			e.printStackTrace();
		}


		SpringApplication.run(StorypassApplication.class, args);
	}

}
