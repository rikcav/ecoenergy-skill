package com.amazon.ask.helloworld.handlers;

import static com.amazon.ask.request.Predicates.intentName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Optional;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.helloworld.responses.ConsumoMensal;
import com.amazon.ask.helloworld.responses.ConsumoUnico;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class AskForConsumptionsOfTheMonthIntentHandler implements RequestHandler {

	@Override
	public boolean canHandle(HandlerInput input) {
		return input.matches(intentName("AskForConsumptionsOfTheMonthIntent"));
	}

	@Override
	public Optional<Response> handle(HandlerInput input) {
		IntentRequest request = (IntentRequest) input.getRequest();

		Slot mesSlot = request.getIntent().getSlots().get("mes");
		String mes = mesSlot.getValue();

		String userId = input.getRequestEnvelope().getSession().getUser().getUserId();

		String response = apiCall(userId, mes);
		Type listaDeConsumosMensais = new TypeToken<List<ConsumoMensal>>() {
		}.getType();

		Gson gson = new Gson();
		List<ConsumoMensal> consumosMensais = gson.fromJson(response, listaDeConsumosMensais);

		if (consumosMensais.isEmpty()) {
			String repromptText = " Se quiser cadastrar um, bastar pedir.";
			String speechText = "Aparentemente não há um consumo mensal cadastrado no mês de " + mes + repromptText;
			String cardText = speechText;
			return input.getResponseBuilder()
					.withSpeech(speechText)
					.withReprompt(repromptText)
					.withSimpleCard("Não há consumo mensal em " + mes, cardText)
					.build();
		}

		Long consumoMensalId = consumosMensais.get(0).getId();
		String consumosUnicosResponse = apiCall(consumoMensalId);

		Type listaDeConsumosUnicos = new TypeToken<List<ConsumoUnico>>() {
		}.getType();
		List<ConsumoUnico> consumosUnicos = gson.fromJson(consumosUnicosResponse, listaDeConsumosUnicos);

		String nomeDoUsuario = consumosUnicos.get(0).getConsumoMensal().getUsuario().getNome();

		String apresentacao = nomeDoUsuario + " possui os utilizou os seguintes eletrodomésticos durante " + mes + ": ";
		String listaDeConsumos = "";
		String repromptText = " Se quiser ouvir de novo, basta pedir novamente.";

		for (ConsumoUnico consumoUnico : consumosUnicos) {
			listaDeConsumos += consumoUnico.getEletrodomestico().getNome() + consumoUnico.getHorasUso()
					+ " horas de uso, ";
		}

		listaDeConsumos = listaDeConsumos.replaceAll(", $", ". ");

		String speechText = apresentacao + listaDeConsumos + repromptText;
		String cardText = speechText;

		return input.getResponseBuilder()
				.withSpeech(speechText)
				.withReprompt(repromptText)
				.withSimpleCard("Seus consumos", cardText)
				.build();
	}

	private String apiCall(String id, String mes) {
		String apiUrl = "https://jipe-fitec-production.up.railway.app/consumos-mensais/usuario/" + id + "/" + mes;
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

	private String apiCall(Long id) {
		String apiUrl = "https://jipe-fitec-production.up.railway.app/consumos-unicos/consumo-mensal/" + id;
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
