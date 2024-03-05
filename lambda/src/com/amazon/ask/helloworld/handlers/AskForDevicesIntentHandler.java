package com.amazon.ask.helloworld.handlers;

import static com.amazon.ask.request.Predicates.intentName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.impl.IntentRequestHandler;
import com.amazon.ask.helloworld.responses.Eletrodomestico;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class AskForDevicesIntentHandler implements IntentRequestHandler {

	@Override
	public boolean canHandle(HandlerInput input, IntentRequest intentRequest) {
		return input.matches(intentName("AskForDevicesIntent"));
	}

	@Override
	public Optional<Response> handle(HandlerInput input, IntentRequest intentRequest) {
		String id = input.getRequestEnvelope().getSession().getUser().getUserId();

		String apiResponse = apiCall(id);

		Type listaDeEletrodomesticos = new TypeToken<ArrayList<Eletrodomestico>>() {
		}.getType();
		Gson gson = new Gson();
		List<Eletrodomestico> eletrodomesticos = gson.fromJson(apiResponse, listaDeEletrodomesticos);

		if (eletrodomesticos.isEmpty()) {
			String reprompt = " Se quiser cadastrar um basta pedir.";
			String resposta = "Parece que você não possui nenhum eletrodoméstico cadastrado." + reprompt;
			return input.getResponseBuilder()
					.withSpeech(resposta)
					.withReprompt(reprompt)
					.withSimpleCard("Não há eletrodomésticos", resposta)
					.build();
		}

		String nomeDoUsuario = eletrodomesticos.get(0).getUsuario().getNome();

		String apresentacao = nomeDoUsuario + " possui os seguintes eletrodomésticos: ";
		String listaDeNomes = "";
		String reprompt = "Se quiser ouvir de novo, basta pedir novamente.";

		for (Eletrodomestico eletrodomestico : eletrodomesticos) {
			listaDeNomes += eletrodomestico.getNome() + ", ";
		}

		listaDeNomes = listaDeNomes.replaceAll(", $", ". ");

		String speechText = apresentacao + listaDeNomes + reprompt;
		String cardText = speechText;

		return input.getResponseBuilder()
				.withSpeech(speechText)
				.withReprompt(reprompt)
				.withSimpleCard("Eletrodomésticos", cardText)
				.build();
	}

	private String apiCall(String id) {
		String apiUrl = "https://ecoenergy-15d81b17ef15.herokuapp.com/eletrodomestico/usuario/" + id;
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
