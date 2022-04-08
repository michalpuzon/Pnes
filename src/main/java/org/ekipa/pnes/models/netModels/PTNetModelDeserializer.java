package org.ekipa.pnes.models.netModels;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.SneakyThrows;
import org.ekipa.pnes.models.elements.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class PTNetModelDeserializer extends JsonDeserializer<PTNetModel> {

    @SneakyThrows
    @Override
    public PTNetModel deserialize(JsonParser p, DeserializationContext ctxt) {

        JsonNode jsonNode = p.getCodec().readTree(p);
        ObjectMapper objectMapper = new ObjectMapper();
        ArrayNode netElementsJson = (ArrayNode) jsonNode.get("netElements");
        List<NetElement> netElements = new ArrayList<>();

        List<Arc> arcs = new ArrayList<>();


        if (netElementsJson.isArray()) {
            Iterator<JsonNode> elements = netElementsJson.elements();
            while (elements.hasNext()) {
                JsonNode next = elements.next();

                if (next.has("start")) {
                    JsonNode startNode = next.get("start");
                    JsonNode endNode = next.get("end");
                    double weight = next.get("weight").asDouble();
                    String id = next.get("id").asText();
                    NetObject start;
                    NetObject end;

                    try {
                        start = objectMapper.readValue(startNode.toString(), Transition.class);
                        end = objectMapper.readValue(endNode.toString(), Place.class);
                    } catch (JsonProcessingException e) {
                        start = objectMapper.readValue(startNode.toString(), Place.class);
                        end = objectMapper.readValue(endNode.toString(), Transition.class);
                    }

                    Arc arc = new Arc(id, start, end, weight);
                    arcs.add(arc);

                } else {
                    if (next.has("state")) {
                        Transition transition = objectMapper.readValue(next.toString(), Transition.class);
                        transition.setArcs(new HashSet<>());
                        netElements.add(transition);

                    } else {
                        Place<Integer> place = objectMapper.readValue(next.toString(), Place.class);
                        netElements.add(place);
                    }
                }

            }
        }
        for (Arc arc : arcs) {
            NetObject start = arc.getStart();
            NetObject end = arc.getEnd();

            if (isTransition(start)) {
                netElements = netElements
                        .stream()
                        .peek(i -> {
                            if (i.getId().equals(start.getId())) {
                                ((NetObject) i).addArc(arc);
                            }
                        })
                        .collect(Collectors.toList());
            } else {
                netElements = netElements
                        .stream()
                        .peek(i -> {
                            if (i.getId().equals(end.getId())) {
                                ((NetObject) i).addArc(arc);
                            }
                        })
                        .collect(Collectors.toList());
            }
        }

        netElements.addAll(arcs);
        return new PTNetModel(netElements);
    }

    /**
     * Zwracanie elementu za pomocą id z podanej listy elemntów.
     *
     * @param id Szukanego elementu.
     * @param netElements Lista elementów.
     * @return Element sieci.
     */

    private NetElement getElementWithId(String id, List<NetElement> netElements) {
        return netElements.stream().filter(i -> i.getId().equals(id)).findFirst().orElse(null);
    }

    /**
     * Sprawdza czy obiekt jest tranzycją.
     *
     * @param object Obiekt sieci.
     * @return false jeśli jest tranzycją, w przeciwnym przypadku zwraca true.
     */

    private boolean isTransition(NetObject object) {
        return object.getId().startsWith("T");
    }
}
