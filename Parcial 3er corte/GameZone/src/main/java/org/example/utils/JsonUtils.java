package org.example.utils;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.example.entities.*;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class JsonUtils {


    private static final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class,
                (JsonSerializer<LocalDateTime>) (src, t, ctx) ->
                    new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
            .registerTypeAdapter(LocalDateTime.class,
                (JsonDeserializer<LocalDateTime>) (json, t, ctx) ->
                    LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .registerTypeHierarchyAdapter(VideoGame.class, new VideoGameAdapter())
            .create();

    private static final String GAMES_FILE = "data/videojuegos.json";
    private static final String SALES_FILE = "data/ventas.json";


    public static void saveGames(List<VideoGame> list) {
        writeFile(GAMES_FILE, list);
    }

    public static List<VideoGame> readGames() {
        try (Reader reader = new FileReader(GAMES_FILE)) {
            Type type = new TypeToken<List<JsonObject>>(){}.getType();
            List<JsonObject> raw = gson.fromJson(reader, type);
            if (raw == null) return new ArrayList<>();
            List<VideoGame> result = new ArrayList<>();
            for (JsonObject obj : raw) {
                result.add(VideoGameAdapter.fromJson(obj));
            }
            return result;
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException("Error al leer videojuegos.json", e);
        }
    }


    public static void saveSales(List<Sale> list) {
        writeFile(SALES_FILE, list);
    }

    public static List<Sale> readSales() {

        try (Reader reader = new FileReader(SALES_FILE)) {
            Type type = new TypeToken<List<JsonObject>>(){}.getType();
            List<JsonObject> raw = gson.fromJson(reader, type);
            if (raw == null) return new ArrayList<>();
            List<Sale> result = new ArrayList<>();
            for (JsonObject obj : raw) {

                String id        = obj.get("id").getAsString();
                String gameTitle = obj.get("gameTitle").getAsString();
                int    qty       = obj.get("quantity").getAsInt();
                double unit      = obj.get("unitPrice").getAsDouble();
                double total     = obj.get("total").getAsDouble();
                String dateStr   = obj.get("saleDate").getAsString();

                // VideoGame stub solo con título para mostrar en tabla
                VideoGame stub = new DigitalVideoGame(gameTitle, unit, "", 0, "", 0, "");
                Sale s = new Sale(id, stub, qty, unit);
                result.add(s);
            }
            return result;
        } catch (FileNotFoundException e) {
            return new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException("Error al leer ventas.json", e);
        }
    }

    public static void appendSale(Sale sale) {
        List<Sale> list = readSales();
        list.add(sale);
        // Serializar de forma plana
        List<JsonObject> flat = new ArrayList<>();
        for (Sale s : list) {
            JsonObject o = new JsonObject();
            o.addProperty("id",        s.getId());
            o.addProperty("gameTitle", s.getVideoGame().getTitle());
            o.addProperty("quantity",  s.getQuantity());
            o.addProperty("unitPrice", s.getUnitPrice());
            o.addProperty("total",     s.getTotal());
            o.addProperty("saleDate",  s.getSaleDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            flat.add(o);
        }
        writeFile(SALES_FILE, flat);
    }

    // ── helpers ──────────────────────────────────────────────────────────────
    private static void writeFile(String path, Object obj) {
        try {
            new File("data").mkdirs();
            try (Writer w = new FileWriter(path)) {
                gson.toJson(obj, w);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error al escribir " + path, e);
        }
    }

    // ── Polimorfismo adapter ─────────────────────────────────────────────────
    public static class VideoGameAdapter implements JsonSerializer<VideoGame>,
                                                     JsonDeserializer<VideoGame> {
        private static final String TYPE_FIELD = "type";

        @Override
        public JsonElement serialize(VideoGame src, Type t, JsonSerializationContext ctx) {
            JsonObject obj = new Gson().toJsonTree(src).getAsJsonObject();
            obj.addProperty(TYPE_FIELD, src.getClass().getSimpleName());
            return obj;
        }

        @Override
        public VideoGame deserialize(JsonElement json, Type t, JsonDeserializationContext ctx)
                throws JsonParseException {
            return fromJson(json.getAsJsonObject());
        }

        public static VideoGame fromJson(JsonObject obj) {
            String type = obj.has("type") ? obj.get("type").getAsString() : "DigitalVideoGame";
            Gson plain = new Gson();
            if ("PhysicalVideoGame".equals(type)) {
                return plain.fromJson(obj, PhysicalVideoGame.class);
            }
            return plain.fromJson(obj, DigitalVideoGame.class);
        }
    }
}
