package com.techlekh;

public class FPLLeagueWinners {

    private static final String BASE_URL = "https://fantasy.premierleague.com/api/leagues-classic/";

    public static void main(String[] args) {
        try {
            // League ID to be used, replace with the league ID you want to fetch data for
            int leagueId = 123456; // Example league ID
            int gameweek = 9;      // Replace with the gameweek you want to fetch

            // Fetch and display the top 20 players for the given gameweek
            fetchTop20Winners(leagueId, gameweek);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Function to fetch the top 20 players for a specific league and gameweek
    public static void fetchTop20Winners(int leagueId, int gameweek) throws Exception {
        // Construct the URL
        String url = BASE_URL + leagueId + "/standings/";

        // Make the API request
        String jsonResponse = makeHttpRequest(url);

        // Parse the JSON response
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(jsonResponse);
        JsonNode standingsNode = rootNode.path("standings").path("results");

        // Extract the top 20 winners for the specified gameweek
        System.out.println("Top 20 Winners for Gameweek " + gameweek + ":");
        int count = 0;
        for (JsonNode manager : standingsNode) {
            if (count == 20) break; // Limit to top 20
            String managerName = manager.path("player_name").asText();
            int totalPoints = manager.path("total").asInt();
            System.out.println((count + 1) + ". " + managerName + " - Points: " + totalPoints);
            count++;
        }
    }

    // Helper function to make HTTP GET request and return the response as a String
    private static String makeHttpRequest(String urlString) throws Exception {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        // Read the response
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }

        // Close connections
        in.close();
        conn.disconnect();

        return content.toString();
    }
}
