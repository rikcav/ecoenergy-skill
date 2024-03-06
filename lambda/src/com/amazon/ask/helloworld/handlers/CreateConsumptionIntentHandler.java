package com.amazon.ask.helloworld.handlers;

import static com.amazon.ask.request.Predicates.intentName;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.omg.CORBA.portable.OutputStream;
import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.impl.IntentRequestHandler;
import com.amazon.ask.helloworld.responses.ConsumoUnico;
import com.amazon.ask.helloworld.responses.Eletrodomestico;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.google.gson.Gson;

public class CreateConsumptionIntentHandler implements IntentRequestHandler {

    @Override
    public boolean canHandle(HandlerInput input, IntentRequest intentRequest) {
        return input.matches(intentName("CreateConsumptionIntent"));
    }

    @Override
    public Optional<Response> handle(HandlerInput input, IntentRequest intentRequest) {
        IntentRequest request = (IntentRequest) input.getRequest();

        Slot eletrodomesticoSlot = request.getIntent().getSlots().get("eletrodomestico");
        Slot horasUsoSlot = request.getIntent().getSlots().get("horasUso");
        Slot potenciaSlot = request.getIntent().getSlots().get("potencia");

        String eletrodomesticoNome = eletrodomesticoSlot.getValue();
        Double horasUso = Double.valueOf(horasUsoSlot.getValue());
        Double potencia = Double.valueOf(potenciaSlot.getValue());

        Eletrodomestico eletrodomestico = new Eletrodomestico();
        eletrodomestico.setNome(eletrodomesticoNome); 
        eletrodomestico.setPotencia(potencia);
        

        ConsumoUnico consumoUnico = new ConsumoUnico();
        consumoUnico.setHorasUso(horasUso);
        consumoUnico.setEletrodomestico(eletrodomestico); 

        

        String repromptText = "Se quiser solicitar outros pedidos, basta pedir.";
        String speechText = "Foi atribuido um novo consumo único para o eletrodoméstico "
                + eletrodomesticoNome + " com " + potencia + " potência." 
                + repromptText;

        return input.getResponseBuilder()
                .withSpeech(speechText)
                .withReprompt(repromptText)
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
}
