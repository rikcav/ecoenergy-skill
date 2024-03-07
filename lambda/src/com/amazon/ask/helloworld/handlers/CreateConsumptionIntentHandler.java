package com.amazon.ask.helloworld.handlers;

import static com.amazon.ask.request.Predicates.intentName;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.impl.IntentRequestHandler;
import com.amazon.ask.helloworld.responses.ConsumoMensal;
import com.amazon.ask.helloworld.responses.ConsumoUnico;
import com.amazon.ask.helloworld.responses.Eletrodomestico;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class CreateConsumptionIntentHandler implements IntentRequestHandler {

	@Override
	public boolean canHandle(HandlerInput input, IntentRequest intentRequest) {
		return input.matches(intentName("CreateConsumptionIntent"));
	}

	@Override
	public Optional<Response> handle(HandlerInput input, IntentRequest intentRequest) {
		Gson gson = new Gson();

		IntentRequest request = (IntentRequest) input.getRequest();

		Slot horasUsoSlot = request.getIntent().getSlots().get("horasUso");
		Slot eletrodomesticoSlot = request.getIntent().getSlots().get("aparelho");
		Slot mesSlot = request.getIntent().getSlots().get("mes");

		// Transformando slots em valores
		Double horasUso = Double.valueOf(horasUsoSlot.getValue());
		String eletrodomesticoNome = eletrodomesticoSlot.getValue();
		String mes = mesSlot.getValue();

		// Pegar lista de consumos mensais através do id do usuário e mês
		String usuarioId = input.getRequestEnvelope().getSession().getUser().getUserId();
		String consumosMensaisResponse = consumosMensaisApiCall(usuarioId, mes);
		Type listaDeConsumosMensais = new TypeToken<ArrayList<ConsumoMensal>>() {
		}.getType();
		List<ConsumoMensal> consumosMensais = gson.fromJson(consumosMensaisResponse, listaDeConsumosMensais);

		if (consumosMensais.isEmpty()) {
			String reprompt = " Se quiser cadastrar um consumo mensal basta pedir.";
			String resposta = "Parece que você não possui nenhum consumo mensal cadastrado para o mês de " + mes + "."
					+ reprompt;
			return input.getResponseBuilder()
					.withSpeech(resposta)
					.withReprompt(reprompt)
					.withSimpleCard("Não há consumos mensais", resposta)
					.build();
		}

		// Pega primeiro consumo mensal da lista
		ConsumoMensal consumoMensal = consumosMensais.get(0);

		// Pegar lista de eletrodomésticos através do id do usuário e nome do aparelho
		String eletrodomesticosResponse = eletrodomesticosApiCall(usuarioId, eletrodomesticoNome);
		Type listaDeEletrodomesticos = new TypeToken<ArrayList<Eletrodomestico>>() {
		}.getType();
		List<Eletrodomestico> eletrodomesticos = gson.fromJson(eletrodomesticosResponse, listaDeEletrodomesticos);

		if (eletrodomesticos.isEmpty()) {
			String reprompt = " Se quiser cadastrar um aparelho basta pedir.";
			String resposta = "Parece que você não possui nenhum eletrodoméstico com o nome "
					+ eletrodomesticoNome + "." + reprompt;
			return input.getResponseBuilder()
					.withSpeech(resposta)
					.withReprompt(reprompt)
					.withSimpleCard("Não há eletrodomésticos", resposta)
					.build();
		}

		// Pega primeiro eletrodoméstico da lista
		Eletrodomestico eletrodomestico = eletrodomesticos.get(0);

		ConsumoUnico consumoUnico = new ConsumoUnico();
		consumoUnico.setHorasUso(horasUso);
		consumoUnico.setConsumoMensal(consumoMensal);
		consumoUnico.setEletrodomestico(eletrodomestico);

		String response = apiCall(consumoUnico);

		ConsumoUnico consumoUnicoResponse = gson.fromJson(response, ConsumoUnico.class);

		Double consumoUnicoResponseHorasUso = consumoUnicoResponse.getHorasUso();
		String consumoUnicoResponseEletrodomesticoNome = consumoUnicoResponse.getEletrodomestico().getNome();
		Double consumoUnicoResponseEletrodomesticoPotencia = consumoUnicoResponse.getEletrodomestico().getPotencia();
		String consumoUnicoResponseMes = consumoUnicoResponse.getConsumoMensal().getNome();

		String repromptText = "Se quiser solicitar outros pedidos, basta pedir.";
		String speechText = "Foram atribuídas" + consumoUnicoResponseHorasUso + " horas de uso" +
				" para o eletrodoméstico " + consumoUnicoResponseEletrodomesticoNome
				+ " com " + consumoUnicoResponseEletrodomesticoPotencia + " watts de potência."
				+ " Este consumo foi atribuído ao mês de " + consumoUnicoResponseMes
				+ repromptText;
		String cardText = speechText + response;

		return input.getResponseBuilder()
				.withSpeech(speechText)
				.withReprompt(repromptText)
				.withSimpleCard("Resposta", cardText)
				.build();
	}

	String apiCall(ConsumoUnico consumoUnico) {
		String apiUrl = "https://jipe-fitec-production.up.railway.app/consumos-unicos";
		StringBuilder response = new StringBuilder();

		try {
			URL url = new URL(apiUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);

			Gson gson = new Gson();
			String jsonInputString = gson.toJson(consumoUnico);

			OutputStream os = (OutputStream) connection.getOutputStream();
			byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
			os.write(input, 0, input.length);

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

	String consumosMensaisApiCall(String id, String mes) {
		String apiUrl = "https://jipe-fitec-production.up.railway.app/consumos-mensais/usuario/" + id + "/" + mes;
		StringBuilder response = new StringBuilder();

		try {
			URL url = new URL(apiUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("GET");
			connection.setReadTimeout(5000);
			connection.setConnectTimeout(5000);

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

	String eletrodomesticosApiCall(String id, String nome) {
		String apiUrl = "https://jipe-fitec-production.up.railway.app/eletrodomestico/usuario/" + id + "/" + nome;
		StringBuilder response = new StringBuilder();

		try {
			URL url = new URL(apiUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("GET");
			connection.setReadTimeout(5000);
			connection.setConnectTimeout(5000);

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
