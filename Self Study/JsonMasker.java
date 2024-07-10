package com.cybersource.api.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Set;

import static com.cybersource.api.util.InstanceHelper.MASK_VALUE;

public class JsonMasker {

    public static String mask( String jsonData, Set<String> keysToMask) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonNode = mapper.readTree(jsonData);
        maskNode(jsonNode,keysToMask);
        return mapper.writeValueAsString(jsonNode);
    }

    private static void maskNode(JsonNode jsonNode, Set<String> keysToMask) {
        if(jsonNode.isObject()){
            jsonNode.fieldNames().forEachRemaining(fieldName -> mask( jsonNode,fieldName,keysToMask));
        } else if(jsonNode.isArray()){
            // recursive
            jsonNode.forEach(childNode -> maskNode(childNode,keysToMask));
        }
    }

    private static void mask(JsonNode jsonNode, String fieldName, Set<String> keysToMask) {
        ObjectNode objectNode  = (ObjectNode) jsonNode;
        JsonNode fieldValue = jsonNode.get(fieldName);
        if (fieldValue != null) {
            if(keysToMask.contains(fieldName)){
                objectNode.put(fieldName, MASK_VALUE);
            }
            else {
                //mutually recursive
                maskNode(fieldValue,keysToMask);
            }

        }
    }
}
