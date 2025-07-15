package com.storypass.storypass;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StorypassApplication {

	public static void main(String[] args) {

		//import db parameters from db.env
		Dotenv dotenv = Dotenv.configure().filename("storypass/db.env").load();
		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

		SpringApplication.run(StorypassApplication.class, args);
	}

}
