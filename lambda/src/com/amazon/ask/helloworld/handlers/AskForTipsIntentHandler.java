package com.amazon.ask.helloworld.handlers;

import static com.amazon.ask.request.Predicates.intentName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.helloworld.responses.Dica;
import com.amazon.ask.model.Response;
import com.google.gson.Gson;

public class AskForTipsIntentHandler implements RequestHandler {

	@Override
	public boolean canHandle(HandlerInput input) {
		return input.matches(intentName("AskForTipsIntent"));
	}

	@Override
	public Optional<Response> handle(HandlerInput input) {
		String apiResponse = apiCall();

		Gson gson = new Gson();
		Dica dica = gson.fromJson(apiResponse, Dica.class);

		String titulo = dica.getTitulo();
		String descricao = dica.getDescricao();

		String repromptText = "Se quiser outra dica, basta pedir novamente.";
		String speechText = titulo + ": " + descricao + "\n" + repromptText;
		return input.getResponseBuilder()
				.withSpeech(speechText)
				.withReprompt(repromptText)
				.withSimpleCard("Dica", speechText)
				.build();
	}

	private String apiCall() {
		String apiUrl = "https://ecoenergy-15d81b17ef15.herokuapp.com/dicas/aleatoria";
		StringBuilder response = new StringBuilder();

		try {
			URL url = new URL(apiUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("GET");
			connection.setConnectTimeout(5000);
			connection.setReadTimeout(5000);

			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			return "Desculpe, ocorreu um erro ao chamar a API.";
		}
		return response.toString();
	}
}
