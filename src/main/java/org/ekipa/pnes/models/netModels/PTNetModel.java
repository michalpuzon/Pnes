package org.ekipa.pnes.models.netModels;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.ekipa.pnes.models.elements.*;
import org.ekipa.pnes.models.exceptions.ImpossibleTransformationException;
import org.ekipa.pnes.models.exceptions.ProhibitedConnectionException;
import org.ekipa.pnes.utils.MyRandom;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@JsonDeserialize(using = PTNetModelDeserializer.class)
public class PTNetModel extends NetModel {

    public PTNetModel() {
        super();
    }

    public PTNetModel(List<NetElement> netElements) {
        super(netElements);
    }

    /**
     * Tworzy łuk {@link org.ekipa.pnes.models.elements.Arc}.
     *
     * @param start  Początek łuku.
     * @param end    Koniec łuku.
     * @param weight Waga łuku.
     * @return Stworzony łuk.
     * @throws {@link org.ekipa.pnes.models.exceptions.ProhibitedConnectionException} W momencie próby stworzenia łuku,
     * o początku i końcu tej samej klasy.
     */
    public Arc createArc(NetObject start, NetObject end, int weight) throws ProhibitedConnectionException {
        return (Arc) addElement(new Arc(start, end, weight));
    }

    /**
     * Tworzy tranzycje {@link org.ekipa.pnes.models.elements.Transition}.
     *
     * @param name Nazwa tranzycji.
     * @param x    Współrzędne tranzycji x.
     * @param y    Współrzędne tranzycji y.
     * @return Stworzona tranzycja.
     */

    public Transition createTransition(String name, double x, double y) {
        return (Transition) addElement(new Transition("", name, x, y));
    }

    /**
     * Tworzy miejsce {@link org.ekipa.pnes.models.elements.Place}.
     *
     * @param name          Nazwa miejsca.
     * @param x             Współrzędne tranzycji x.
     * @param y             Współrzędne tranzycji y.
     * @param tokenCapacity Limit tokenów dla danego miejsca.
     * @param token         Ilość tokenów.
     * @return Stworzone miejsce.
     */

    public Place<Integer> createPlace(String name, double x, double y, int tokenCapacity, int token) {
        return (Place<Integer>) addElement(new Place<>("", name, x, y, tokenCapacity, token));
    }

    /**
     * Edytuje element {@link org.ekipa.pnes.models.netModels.NetModel}.
     *
     * @param actualId Id zmienionego obiektu.
     * @param newId    Id nowego obiektu.
     * @return Zmieniony element.
     */

    public NetElement edit(String actualId, String newId) {
        return editElement(actualId, newId);
    }

    @Override
    public void translate(NetModel model) {

    }

    @Override
    public void transform(NetModel model) throws ImpossibleTransformationException {

    }

    @Override
    protected boolean validateElement(NetElement o) {
        boolean wasValidated = false;
        if (o instanceof Arc) {
            wasValidated = true;
            Arc arc = (Arc) o;
            if (arc.getWeight() <= 0) return false;
            try {
                if (arc.getEnd().getClass().equals(arc.getStart().getClass())) return false;
            } catch (Exception e) {
                return false;
            }
        }
        if (o instanceof Place) {
            wasValidated = true;
            try {
                Place<Integer> place = (Place<Integer>) o;
                if (place.getTokenCapacity() < 0) return false;
                if (place.getTokens() > place.getTokenCapacity()) return false;
            } catch (Exception e) {
                return false;
            }
        }
        if (o instanceof Transition) {
            wasValidated = true;
            Transition transition = (Transition) o;
            if (!transition.getState().equals(Transition.TransitionState.Unready)) return false;
        }
        if (o instanceof NetObject) {
            NetObject netObject = (NetObject) o;
            if (netObject.getX() < 0 || netObject.getY() < 0) return false;
        }
        return wasValidated;
    }

    @Override
    protected void addTokens(String placeId, Object tokens) {
        if (tokens instanceof Integer && (Integer) tokens > 0 && ((Place) getElement(placeId)).getTokens() instanceof Integer) {
            int placeTokens = (Integer) ((Place) getElement(placeId)).getTokens();
            int newTokens = (Integer) tokens;
            int tokenSet = placeTokens + newTokens;
            ((Place) getElement(placeId)).setTokens(tokenSet);
            if (tokenSet > ((Place) getElement(placeId)).getTokenCapacity())
                ((Place) getElement(placeId)).setTokens(((Place) getElement(placeId)).getTokenCapacity());
        }

    }

    @Override
    public String serialize(NetModel netModel) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(netModel);
    }

    @Override
    public NetModel deserialize(String json) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(json, this.getClass());
    }

    @Override
    protected boolean runTransition(Transition transition) {
        if (!transition.getState().equals(Transition.TransitionState.Running)) {
            return false;
        }
        if (transition.getArcs().stream().noneMatch(arc -> arc.getStart().getId().equals(transition.getId()))) {
            return false;
        }
        List<Arc> consumeTokenArcs = transition.getArcs().stream().filter(arc -> arc.getEnd().getId().equals(transition.getId())).collect(Collectors.toList());
        consumeTokenArcs.forEach(arc -> {
            Place<Integer> place = (Place<Integer>) getNetElements().stream().filter(i -> i.getId().equals(arc.getStart().getId())).findFirst().orElse(null);
            setNetElements(getNetElements().stream().peek(i -> {
                if (i.getId().equals(place.getId())) {
                    if (place.getTokens() > 0) {
                        ((Place) i).setTokens((place.getTokens() - (int) arc.getWeight()));
                    }
                }
            }).collect(Collectors.toList()));
        });

        List<Arc> forwardTokenArcs = transition.getArcs().stream().filter(arc -> arc.getStart().getId().equals(transition.getId())).collect(Collectors.toList());
        forwardTokenArcs.forEach(arc -> {

            Place<Integer> place = (Place<Integer>) getNetElements().stream().filter(i -> i.getId().equals(arc.getEnd().getId())).findFirst().orElse(null);

            setNetElements(getNetElements().stream().peek(i -> {
                if (i.getId().equals(place.getId())) {

                    if (place.getTokenCapacity() >= (place.getTokens() + (int) arc.getWeight())) {
                        ((Place) i).setTokens(place.getTokens() + (int) arc.getWeight());
                    }
                }
            }).collect(Collectors.toList()));
        });

        return transition.setUnready();
    }

    @Override
    protected List<Transition> prepareTransitions() {

        List<NetElement> newNetElements = this.getNetElements()
                .stream()
                .peek(i -> {
                    if (i instanceof Transition) {
                        if (canTransitionBeReady(i.getId())) {
                            ((Transition) i).setReady();
                        }
                    }
                }).collect(Collectors.toList());
        this.setNetElements(newNetElements);

        return newNetElements.stream().filter(i -> i instanceof Transition).map(i -> ((Transition) i)).collect(Collectors.toList());
    }

    @Override
    protected List<Transition> selectTransitionsToRun(List<Transition> transitions) {
        return Collections.singletonList(MyRandom.getRandom(transitions.stream()
                .filter(i -> i.getState().equals(Transition.TransitionState.Ready))
                .collect(Collectors.toList())));
    }

    /**
     * Sprawdza czy podana tranzycja może być gotowa.
     *
     * @param transitionId Id tranzycji która ma być sprawdzona.
     * @return true jeśli tranzycja może być gotowa, w przeciwnym przypadku false.
     */

    private boolean canTransitionBeReady(String transitionId) {
        if (((Transition) getElement(transitionId)).getArcs().isEmpty()) return false;
        Set<Arc> transitionArcs = ((Transition) getElement(transitionId)).getArcs()
                .stream()
                .filter(arc -> arc.getEnd().getId().equals(transitionId))
                .collect(Collectors.toSet());
        return transitionArcs
                .stream()
                .noneMatch(arc -> getCurrentTokens(arc.getId()) < (int) arc.getWeight());
    }

    /**
     * Zwracanie dokładnej ilości tokenów.
     *
     * @param arcId Podawany jest id łuku.
     * @return Ilość tokenów z początku łuku.
     */

    private int getCurrentTokens(String arcId) {
        Optional<Place<Integer>> first = getNetElements().stream()
                .filter(i -> i.getId().equals(((Arc) getElement(arcId)).getStart().getId())).map(i -> (Place<Integer>) i).findFirst();
        if (first.isPresent()) {
            return first.get().getTokens();
        } else {
            return Integer.MAX_VALUE;
        }
    }
}