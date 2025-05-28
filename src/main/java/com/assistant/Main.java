package com.assistant;

public class Main {
    public static void main(String[] args) {


        PersonalAssistant assistant = new PersonalAssistant();

        // Test weather functionality
        System.out.println("\nGetting weather for Danville, CA:");
        System.out.println(assistant.chat("Get current weather for Danville, CA"));

        System.out.println("\nGetting weather for San Francisco, US:");
        System.out.println(assistant.chat("Get current weather for San Francisco, US"));

        // Test directions functionality
        System.out.println("\nGetting directions:");
        System.out.println(assistant.chat("Get directions from 219 Akabane Lane, Danville, CA to Salesforce Tower, San Francisco, CA"));
    
        // Test directions functionality
        System.out.println("\nI'm going from Danville, CA to Carlsbad, CA tomorrow, what is the estimated drive time as well as weather?");
        System.out.println(assistant.chat("I'm going from Danville, CA to Carlsbad, CA tomorrow, what is the estimated drive time as well as weather?"));
   
    }
} 