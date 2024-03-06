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
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.helloworld.responses.ConsumoMensal;
import com.amazon.ask.model.Response;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class AskForAllMonthlyConsumptionsIntentHandler implements RequestHandler {

	@Override
	public boolean canHandle(HandlerInput input) {
		return input.matches(intentName("AskForAllMonthlyConsumptionsIntent"));
	}

	@Override
	public Optional<Response> handle(HandlerInput input) {
		String id = input.getRequestEnvelope().getSession().getUser().getUserId();

		String apiResponse = apiCall(id);

		Type listaDeConsumosMensais = new TypeToken<ArrayList<ConsumoMensal>>() {
		}.getType();
		Gson gson = new Gson();
		List<ConsumoMensal> consumoMensais = gson.fromJson(apiResponse, listaDeConsumosMensais);

		if (consumoMensais.isEmpty()) {
			String reprompt = " Se quiser cadastrar um basta pedir.";
			String resposta = "Parece que você não possui nenhum consumo mensal cadastrado." + reprompt;
			return input.getResponseBuilder()
					.withSpeech(resposta)
					.withReprompt(reprompt)
					.withSimpleCard("Não há consumos mensais", resposta)
					.build();
		}

		String nomeDoUsuario = consumoMensais.get(0).getUsuario().getNome();

		String apresentacao = nomeDoUsuario + " possui os seguintes consumos mensais: ";
		String listaDeNomesEKWHT = "";
		String reprompt = "Se quiser ouvir de novo, basta pedir novamente.";

		for (ConsumoMensal consumoMensal : consumoMensais) {
			listaDeNomesEKWHT += consumoMensal.getNome() + " com "
					+ consumoMensal.getKillowattsHoraTotal() + " killowatts hora totais" + ", ";
		}

		listaDeNomesEKWHT = listaDeNomesEKWHT.replaceAll(", $", ". ");

		String speechText = apresentacao + listaDeNomesEKWHT + reprompt;
		String cardText = speechText;

		return input.getResponseBuilder()
				.withSpeech(speechText)
				.withReprompt(reprompt)
				.withSimpleCard("Consumos mensais", cardText)
				.build();
	}

	private String apiCall(String id) {
		String apiUrl = "https://jipe-fitec-production.up.railway.app/consumos-mensais/usuario/" + id;
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
