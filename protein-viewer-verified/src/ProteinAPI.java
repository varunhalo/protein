import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONObject;

public class ProteinAPI {

    public static ProteinModel fetchProtein(String query) {
        try {
            query = query.trim();

            String jsonInput = "{\n" +
                    "  \"query\": {\n" +
                    "    \"type\": \"terminal\",\n" +
                    "    \"service\": \"full_text\",\n" +
                    "    \"parameters\": {\n" +
                    "      \"value\": \"" + query + "\"\n" +
                    "    }\n" +
                    "  },\n" +
                    "  \"return_type\": \"entry\",\n" +
                    "  \"request_options\": {\n" +
                    "    \"paginate\": {\n" +
                    "      \"start\": 0,\n" +
                    "      \"rows\": 1\n" +
                    "    }\n" +
                    "  }\n" +
                    "}";

            URL url = new URL("https://search.rcsb.org/rcsbsearch/v2/query");

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                os.write(jsonInput.getBytes("utf-8"));
            }

            InputStream is = (conn.getResponseCode() >= 400)
                    ? conn.getErrorStream()
                    : conn.getInputStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            String json = response.toString();

            // 🔍 DEBUG (optional)
            System.out.println("API RESPONSE: " + json);

            JSONObject obj = new JSONObject(json);

            // ❗ HANDLE ERROR RESPONSE SAFELY
            if (!obj.has("result_set")) {
                System.out.println("No result_set found!");
                return null;
            }

            JSONArray results = obj.getJSONArray("result_set");

            if (results.length() == 0) return null;

            String pdbId = results.getJSONObject(0).getString("identifier");

            return new ProteinModel(
        capitalize(query),   // protein name
        pdbId,               // PDB ID
                    "Top match found in Protein Data Bank (RCSB)",
                    "Sequence data can be extended in future"
            );

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static String capitalize(String text) {
        if (text == null || text.isEmpty()) return text;
        return text.substring(0,1).toUpperCase() + text.substring(1);
    }
}