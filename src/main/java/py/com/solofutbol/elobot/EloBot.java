package py.com.solofutbol.elobot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import py.com.solofutbol.elo.EloHelper;

import java.util.*;

@Component
public class EloBot extends TelegramLongPollingBot {
    String REGEX = "/mili[^a-zA-Z]*\\s*.*";


    public static final String HTTP_TOKEN = "-- TODO --";

    Long chatId = null;

    Map<String, Player> players = new HashMap<>();

    EloHelper eloHelper = new EloHelper();

    @Override
    public void onUpdateReceived(Update update) {
        chatId = update.getMessage().getChatId();
        System.out.println("Message Received");
        //sendMessage();
        System.out.println(update.getMessage().getText());
        String command = update.getMessage().getText();
        if (command.matches(REGEX)) {
            String[] strings = command.split("\\s");
            try {
                if (strings.length >= 2 && "-help".equalsIgnoreCase(strings[1])) {
                    sendMessage("-l lista la tabla de posiciones");
                    sendMessage("-r <jugadorGanador> <jugadorPerdedor> agrega un resultado y devuelve resultados");
                    sendMessage("-a <jugador> Agrega un jugador a la tabla de posiciones, empieza con 400 puntos");
                }
                if (strings.length >= 2 && "-l".equalsIgnoreCase(strings[1])) {
                    Comparator comparator = new Comparator<Player>() {

                        @Override
                        public int compare(Player o1, Player o2) {
                            return (int) o2.getPuntos() - (int) o1.getPuntos();
                        }
                    };
                    List<Player> list = new ArrayList<>(players.values());
                    Collections.sort(list, comparator);

                    for (Player player : list) {
                        sendMessage("" + player.getNombre() + " -> " + player.getPuntos() + " puntos");
                    }
                }
                if (strings.length >= 3 && "-a".equalsIgnoreCase(strings[1])) {
                    players.put(strings[2].toUpperCase(), new Player(strings[2]));
                    sendMessage("Jugador " + strings[2] + " agregado con éxito");
                }
                if (strings.length >= 4 && "-r".equalsIgnoreCase(strings[1])) {
                    Player player1 = players.get(strings[2].toUpperCase());
                    Player player2 = players.get(strings[3].toUpperCase());
                    double[] resultado = eloHelper.calculateRating(20d, new double[]{player1.getPuntos(), player2.getPuntos()}, 0);
                    player1.setPuntos(resultado[0]);
                    player2.setPuntos(resultado[1]);
                    sendMessage("Jugador " + player1.getNombre() + " ahora tiene " + player1.getPuntos() + " puntos");
                    sendMessage("Jugador " + player2.getNombre() + " ahora tiene " + player2.getPuntos() + " puntos");
                }
            } catch (Exception e) {

            }

        }

    }

    @Override
    public String getBotUsername() {
        return "EloBot";
    }

    public void sendMessage(String message) {
        if (chatId == null)
            return;

        SendMessage response = new SendMessage();
        response.setChatId(chatId);
        response.setText(message);
        try {
            execute(response);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getBotToken() {
        return HTTP_TOKEN;
    }

    class Player {
        private double puntos = 400;
        private String nombre;

        public Player(String nombre) {
            this.nombre = nombre;
        }

        public double getPuntos() {
            return puntos;
        }

        public void setPuntos(double puntos) {
            this.puntos = puntos;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }
    }
}