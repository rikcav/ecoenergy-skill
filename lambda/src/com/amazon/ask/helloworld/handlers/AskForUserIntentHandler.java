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
import com.amazon.ask.helloworld.responses.Usuario;
import com.amazon.ask.model.Response;
import com.google.gson.Gson;

public class AskForUserIntentHandler implements RequestHandler {

	@Override
	public boolean canHandle(HandlerInput input) {
		return input.matches(intentName("AskForUserIntent"));
	}

	@Override
	public Optional<Response> handle(HandlerInput input) {
		String id = input.getRequestEnvelope().getSession().getUser().getUserId();
		String response = apiCall(id);

		Gson gson = new Gson();
		Usuario usuario = gson.fromJson(response, Usuario.class);

		String nome = usuario.getNome();
		Double tarifa = usuario.getTarifa();

		String repromptText = "Se quiser ouvir novamente, basta pedir de novo.";
		String speechText = "O nome do usuário é " + nome + " e sua tarifa é " + tarifa + " reais por kilowatts hora. "
				+ repromptText;
		String cardText = speechText;

		return input.getResponseBuilder()
				.withSpeech(speechText)
				.withReprompt(repromptText)
				.withSimpleCard("Usuário", cardText)
				.build();
	}

	private String apiCall(String id) {
		String apiUrl = "https://jipe-fitec-production.up.railway.app/usuarios/" + id;
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
