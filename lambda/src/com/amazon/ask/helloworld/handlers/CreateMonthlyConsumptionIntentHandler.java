package com.amazon.ask.helloworld.handlers;

import static com.amazon.ask.request.Predicates.intentName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.impl.IntentRequestHandler;
import com.amazon.ask.helloworld.responses.ConsumoMensal;
import com.amazon.ask.helloworld.responses.Usuario;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.google.gson.Gson;

public class CreateMonthlyConsumptionIntentHandler implements IntentRequestHandler {

	@Override
	public boolean canHandle(HandlerInput input, IntentRequest intentRequest) {
		return input.matches(intentName("CreateMonthlyConsumptionIntent"));
	}

	@Override
	public Optional<Response> handle(HandlerInput input, IntentRequest intentRequest) {
		IntentRequest request = (IntentRequest) input.getRequest();

		Slot mesSlot = request.getIntent().getSlots().get("mes");

		String id = input.getRequestEnvelope().getSession().getUser().getUserId();

		String usuarioResponse = apiCall(id);
		Gson gson = new Gson();
		Usuario usuario = gson.fromJson(usuarioResponse, Usuario.class);

		String mes = mesSlot.getValue();

		ConsumoMensal consumoMensal = new ConsumoMensal();
		consumoMensal.setNome(mes);
		consumoMensal.setKillowattsHoraTotal(0D);
		consumoMensal.setUsuario(usuario);

		String response = apiCall(consumoMensal);

		ConsumoMensal consumoMensalResponse = gson.fromJson(response, ConsumoMensal.class);

		String consumoMensalResponseNome = consumoMensalResponse.getNome();

		String repromptText = " Se quiser solicitar outros pedidos, basta pedir.";
		String speechText = "Foi criado um novo consumo mensal para o mes de "
				+ consumoMensalResponseNome + "."
				+ repromptText;
		String cardText = response;

		return input.getResponseBuilder()
				.withSpeech(speechText)
				.withReprompt(repromptText)
				.withSimpleCard("Resposta", cardText)
				.build();
	}

	String apiCall(ConsumoMensal consumoMensal) {
		String apiUrl = "https://jipe-fitec-production.up.railway.app/consumos-mensais";
		StringBuilder response = new StringBuilder();

		try {
			URL url = new URL(apiUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);

			Gson gson = new Gson();
			String jsonInputString = gson.toJson(consumoMensal);

			OutputStream os = connection.getOutputStream();
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
