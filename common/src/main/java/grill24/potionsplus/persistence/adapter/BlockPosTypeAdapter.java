package grill24.potionsplus.persistence.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.core.BlockPos;

import java.io.IOException;

public class BlockPosTypeAdapter extends TypeAdapter<BlockPos> {
    @Override
    public void write(JsonWriter out, BlockPos value) throws IOException {
        out.beginObject();
        if (value != null) {
            out.name("x").value(value.getX());
            out.name("y").value(value.getY());
            out.name("z").value(value.getZ());
        }
        out.endObject();
    }

    @Override
    public BlockPos read(JsonReader in) throws IOException {
        int x = 0, y = 0, z = 0;

        in.beginObject();
        while (in.hasNext()) {
            switch (in.nextName()) {
                case "x":
                    x = in.nextInt();
                    break;
                case "y":
                    y = in.nextInt();
                    break;
                case "z":
                    z = in.nextInt();
                    break;
            }
        }
        in.endObject();

        return new BlockPos(x, y, z);
    }
}
