package com.assistant;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.SystemMessage;

public class PersonalAssistant {
    private final AssistantService assistantService;

    public PersonalAssistant() {
        ChatModel model = OllamaChatModel.builder()
                .baseUrl("http://localhost:11434")
                .modelName("qwen3:8b")
                .build();

        assistantService = AiServices.builder(AssistantService.class)
                .chatModel(model)
                .tools(new WeatherTool(ConfigManager.getOpenWeatherApiKey()))
                .tools(new DirectionsTool(ConfigManager.getGoogleMapsApiKey()))
                .build();
    }

    public String chat(String userQuestion) {
        return assistantService.chat(userQuestion);
    }

    @SystemMessage("""
        You are a travel assistant. For any travel-related question (such as 'I'm going from X to Y, what is the weather and drive time?'), you MUST:
        1. Use the currentWeather tool for BOTH the origin and destination.
        2. Use the getDirections tool for the route.
        3. Wait until you have called BOTH tools and received their results before answering.
        4. Combine the tool results into a single, clear, and complete response.
        5. NEVER provide general or estimated information—ALWAYS use the tools for real-time data.
        
        Example:
        User: I'm going from Danville, CA to San Diego, CA tomorrow, what is the estimated drive time as well as weather?
        Step 1: Call currentWeather("Danville, CA")
        Step 2: Call currentWeather("San Diego, CA")
        Step 3: Call getDirections("Danville, CA", "San Diego, CA")
        Step 4: Combine the results and present them in this format:
        ---
        WEATHER:
        Danville, CA: [weather result]
        San Diego, CA: [weather result]
        
        DRIVING DIRECTIONS:
        [directions result]
        ---
        Do not answer until you have all tool results. If you cannot get a result, state which tool failed and why.
        """)
    interface AssistantService {
        String chat(String userQuestion);
    }
} 