package edu.drexel.StatCollector;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;

import java.util.UUID;

public class Utils {
    public static final String TAG = "StatCollector.";

    public static void toggleCheck(JsonNode item) {
        ObjectNode itemObject = (ObjectNode)item;
        JsonNode checkNode = item.get("check");
        if(checkNode != null) {
            if(checkNode.getBooleanValue()) {
                itemObject.put("check", false);
            }
            else {
                itemObject.put("check", true);
            }
        }
        else {
            itemObject.put("check", true);
        }
    }

    public static JsonNode createWithText(String text) {
        UUID uuid = UUID.randomUUID();

        ObjectNode item = JsonNodeFactory.instance.objectNode();

        item.put("_id", uuid.toString());
        item.put("text", text);

        return item;
    }
}
