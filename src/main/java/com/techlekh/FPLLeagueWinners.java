package com.techlekh;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class FPLLeagueWinners {

    private static final String BASE_LEAGUE_URL = "https://fantasy.premierleague.com/api/leagues-classic/";
    private static final String PLAYER_HISTORY_URL = "https://fantasy.premierleague.com/api/entry/";

    public static void main(String[] args) throws Exception {
        System.out.println("Program to get the list of top winners");
        System.out.println("--------------------------------------");

        Scanner scanner = new Scanner(System.in);
        System.out.print("\nEnter league ID: ");
        int leagueId = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Enter gameweek no.: ");
        int gameweek = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Top ??: ");
        int topCount = scanner.nextInt();
        scanner.nextLine();

        List<Manager> managers = fetchAllManagersWithGameweekPoints(leagueId);

        while (true) {
            displayTopWinners(managers, topCount, gameweek);
            System.out.print("\nDisplay top winner for another gameweek (Y/N): ");
            String s = scanner.nextLine();
            if (s.equalsIgnoreCase("Y")) {
                System.out.print("Enter gameweek no.: ");
                gameweek = Integer.parseInt(scanner.nextLine());
            } else {
                break;
            }
        }
    }

    private static void displayTopWinners(List<Manager> managers, int topCount, int gameweek) {
        List<Manager> filteredManagers = managers.stream()
                .filter(manager -> manager.getGwPointsByGwNo().containsKey(gameweek))
                .sorted((o1, o2) -> Integer.compare(
                        o2.getGwPointsByGwNo().get(gameweek),
                        o1.getGwPointsByGwNo().get(gameweek)
                )).toList();
        System.out.println();
        System.out.printf(
                "%10s %50s %50s %20s %60s",
                "Id",
                "Name",
                "User name",
                "Gameweek Points",
                "Link"
        );
        System.out.println();
        filteredManagers.subList(0, Math.min(topCount, filteredManagers.size())).forEach(manager -> {
            System.out.printf(
                    "%10d %50s %50s %20d %60s",
                    manager.getId(),
                    manager.getPlayerName(),
                    manager.getEntryName(),
                    manager.getGwPointsByGwNo().get(gameweek),
                    String.format(
                            "https://fantasy.premierleague.com/entry/%d/event/%d",
                            manager.getEntry(),
                            gameweek
                    )
            );
            System.out.println();
        });
    }

    public static List<Manager> fetchAllManagersWithGameweekPoints(int leagueId) throws Exception {
        List<Manager> managers = new ArrayList<>();

        int pageNo = 1;

        System.out.println("Fetching list of managers in the league. Please wait...");
        while (true) {
            String url = BASE_LEAGUE_URL + leagueId + "/standings?page_standings=" + pageNo;

            String jsonResponse = makeHttpRequest(url);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            JsonNode standingsNode = rootNode.path("standings");
            JsonNode resultsNode = standingsNode.path("results");

            for (JsonNode managerJsonNode : resultsNode) {
                int entryId = managerJsonNode.path("entry").asInt();

                var manager = new Manager(
                        managerJsonNode.path("id").asInt(),
                        managerJsonNode.path("player_name").asText(),
                        entryId,
                        managerJsonNode.path("entry_name").asText(),
                        Collections.emptyMap(),
                        0
                );
                managers.add(manager);
            }
            if (!standingsNode.path("has_next").asBoolean()) {
                break;
            }
            pageNo++;
            System.out.printf("Fetched %d managers.\n", managers.size());
        }
        System.out.println("Fetch complete!");

        System.out.printf("Fetching history of %d managers in the league. Please wait...\n", managers.size());
        managers.parallelStream().forEach(manager -> {
            try {
                Map<Integer, Integer> gwPointsByGwNo = fetchPlayerGameweekPoints(manager.getEntry());
                manager.setGwPointsByGwNo(gwPointsByGwNo);
                manager.setTotalPoints(gwPointsByGwNo.values().stream().reduce(0, Integer::sum));
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println("Fetch complete!");

        return managers;
    }

    public static Map<Integer, Integer> fetchPlayerGameweekPoints(int entryId) throws Exception {
        String url = PLAYER_HISTORY_URL + entryId + "/history/";

        String jsonResponse = makeHttpRequest(url);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode currentNode = rootNode.path("current");

        Map<Integer, Integer> gwPointsByGwNo = new HashMap<>();
        for (JsonNode gameweekNode : currentNode) {
            gwPointsByGwNo.put(gameweekNode.path("event").asInt(), gameweekNode.path("points").asInt());
        }

        return gwPointsByGwNo;
    }

    private static String makeHttpRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        in.close();
        conn.disconnect();

        return content.toString();
    }
}
