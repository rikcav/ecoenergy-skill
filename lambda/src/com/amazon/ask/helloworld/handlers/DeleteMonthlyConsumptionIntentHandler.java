package com.amazon.ask.helloworld.handlers;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;

import static com.amazon.ask.request.Predicates.intentName;

public class DeleteMonthlyConsumptionIntentHandler implements RequestHandler  {
    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("DeleteMonthlyConsumptionIntent"));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {
        String id = input.getRequestEnvelope().getSession().getUser().getUserId();
        String response = apiCall(id);

        String repromptText = "Se quiser solicitar outro serviço, basta pedir.";
        String speechText = "O seu consumo mensal foi deletado com sucesso. " + repromptText;
        String cardText = speechText + response;

        return input.getResponseBuilder()
                .withSpeech(speechText)
                .withReprompt(repromptText)
                .withSimpleCard("Resposta", cardText)
                .build();
    }

    private String apiCall(String id) {
        String apiUrl = "https://jipe-fitec-production.up.railway.app/consumos-mensais/" + id;
        StringBuilder response = new StringBuilder();

        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("DELETE");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
            return "Desculpe, ocorreu um erro ao chamar a API.";
        }

        return response.toString();
    }

}

