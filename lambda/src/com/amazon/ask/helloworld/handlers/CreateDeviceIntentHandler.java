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
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.helloworld.responses.Eletrodomestico;
import com.amazon.ask.helloworld.responses.Usuario;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.google.gson.Gson;

public class CreateDeviceIntentHandler implements RequestHandler {

	@Override
	public boolean canHandle(HandlerInput input) {
		return input.matches(intentName("CreateDeviceIntent"));
	}

	@Override
	public Optional<Response> handle(HandlerInput input) {
		IntentRequest request = (IntentRequest) input.getRequest();
		Slot aparelhoSlot = request.getIntent().getSlots().get("aparelho");
		Slot potenciaSlot = request.getIntent().getSlots().get("potencia");

		String aparelho = aparelhoSlot.getValue();
		Double potencia;
		try {
			potencia = Double.parseDouble(potenciaSlot.getValue());
		} catch (NumberFormatException e) {
			return input.getResponseBuilder()
					.withSpeech("Desculpe, não consegui entender a potência do eletrodoméstico.")
					.build();
		}

		String id = input.getRequestEnvelope().getSession().getUser().getUserId();
		String usuarioResponse = apiCall(id);

		Gson gson = new Gson();
		Usuario usuario = gson.fromJson(usuarioResponse, Usuario.class);

		Eletrodomestico eletrodomestico = new Eletrodomestico();
		eletrodomestico.setNome(aparelho);
		eletrodomestico.setPotencia(potencia);
		eletrodomestico.setUsuario(usuario);

		String response = apiCall(eletrodomestico);
		Eletrodomestico eletrodomesticoResponse = gson.fromJson(response, Eletrodomestico.class);
		String eletrodomesticoNome = eletrodomesticoResponse.getNome();
		Double eletrodomesticoPotencia = eletrodomesticoResponse.getPotencia();

		String repromptText = " Se quiser solicitar outros pedidos, basta pedir.";
		String speechText = "Foi criado um novo eletrodoméstico com o nome "
				+ eletrodomesticoNome + " e potência de " + eletrodomesticoPotencia + " Watts."
				+ repromptText;
		String cardText = response;

		return input.getResponseBuilder()
				.withSpeech(speechText)
				.withReprompt(repromptText)
				.withSimpleCard("Eletrodoméstico", cardText)
				.build();
	}

	private String apiCall(Eletrodomestico eletrodomestico) {
		String apiUrl = "https://jipe-fitec-production.up.railway.app/eletrodomestico";
		StringBuilder response = new StringBuilder();

		try {
			URL url = new URL(apiUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);

			Gson gson = new Gson();
			String jsonInputString = gson.toJson(eletrodomestico);

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
