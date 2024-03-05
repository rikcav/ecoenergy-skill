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
import com.amazon.ask.helloworld.responses.Usuario;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.google.gson.Gson;

public class CreateUserIntentHandler implements IntentRequestHandler {

	@Override
	public boolean canHandle(HandlerInput input, IntentRequest intentRequest) {
		return input.matches(intentName("CreateUserIntent"));
	}

	@Override
	public Optional<Response> handle(HandlerInput input, IntentRequest intentRequest) {
		IntentRequest request = (IntentRequest) input.getRequest();

		Slot nomeSlot = request.getIntent().getSlots().get("firstname");
		Slot tarifaSlot = request.getIntent().getSlots().get("tarifa");

		String id = input.getRequestEnvelope().getSession().getUser().getUserId();
		String nome = nomeSlot.getValue();
		Double tarifa = Double.valueOf(tarifaSlot.getValue());

		Usuario usuario = new Usuario();
		usuario.setId(id);
		usuario.setNome(nome);
		usuario.setTarifa(tarifa);
		String response = apiCall(usuario);

		Gson gson = new Gson();
		Usuario usuarioResponse = gson.fromJson(response, Usuario.class);

		String usuarioResponseNome = usuarioResponse.getNome();
		Double usuarioResponseTarifa = usuarioResponse.getTarifa();

		String repromptText = " Se quiser solicitar outros pedidos, basta pedir.";
		String speechText = "Foi criado um novo usuário com nome "
				+ usuarioResponseNome + " e com tarifa " + usuarioResponseTarifa + "."
				+ repromptText;
		String cardText = response;

		return input.getResponseBuilder()
				.withSpeech(speechText)
				.withReprompt(repromptText)
				.withSimpleCard("Resposta", cardText)
				.build();
	}

	String apiCall(Usuario usuario) {
		String apiUrl = "https://ecoenergy-15d81b17ef15.herokuapp.com/usuarios";
		StringBuilder response = new StringBuilder();

		try {
			URL url = new URL(apiUrl);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setDoOutput(true);

			Gson gson = new Gson();
			String jsonInputString = gson.toJson(usuario);

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

}
