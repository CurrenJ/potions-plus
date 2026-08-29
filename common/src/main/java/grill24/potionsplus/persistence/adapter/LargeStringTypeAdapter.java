package grill24.potionsplus.persistence.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class LargeStringTypeAdapter extends TypeAdapter<String> {
    private static final int MAX_CHUNK_SIZE = 65535; // Define a suitable max size

    @Override
    public void write(JsonWriter out, String value) throws IOException {
        if (value.length() <= MAX_CHUNK_SIZE) {
            out.value(value);
        } else {
            out.beginArray();
            for (int i = 0; i < value.length(); i += MAX_CHUNK_SIZE) {
                int end = Math.min(value.length(), i + MAX_CHUNK_SIZE);
                out.value(value.substring(i, end));
            }
            out.endArray();
        }
    }

    @Override
    public String read(JsonReader in) throws IOException {
        if (in.peek() == com.google.gson.stream.JsonToken.STRING) {
            return in.nextString();
        } else {
            in.beginArray();
            StringBuilder sb = new StringBuilder();
            while (in.hasNext()) {
                sb.append(in.nextString());
            }
            in.endArray();
            return sb.toString();
        }
    }
}
