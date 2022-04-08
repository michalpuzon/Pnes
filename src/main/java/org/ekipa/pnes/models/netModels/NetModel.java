package org.ekipa.pnes.models.netModels;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Data;
import org.ekipa.pnes.models.elements.*;
import org.ekipa.pnes.models.exceptions.ImpossibleTransformationException;
import org.ekipa.pnes.utils.IdGenerator;
import org.ekipa.pnes.utils.Pair;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public abstract class NetModel {
    private List<NetElement> netElements;

    public NetModel(List<NetElement> netElements) {
        this.netElements = netElements;
    }

    public NetModel() {
        this.netElements = new ArrayList<>();
    }

    protected NetElement getElement(String id) {
        return netElements.stream().filter(element -> element.getId().equals(id)).findFirst().orElse(null);
    }

    protected NetObject getObject(String id) {
        return netElements.stream()
                .filter(element -> element instanceof NetObject)
                .filter(netObject -> netObject.getId().equals(id))
                .map(netElement -> (NetObject) netElement).findFirst().orElse(null);
    }

    /**
     * `
     * Tworzy model obecnego typu na podstawie porównywania parametrów sieci z innym modelem i zamienia te parametry,
     * które są możliwe do zmiany, w przeciwnym razie dla danych których nie da się przetłumaczyć,
     * zostaną stworzone odpowiedniki i zostaną ustawione im wartości.
     *
     * @param model Model {@link org.ekipa.pnes.models.netModels.NetModel}.
     */
    public abstract void translate(NetModel model);

    /**
     * Tworzy model obecnego typu na podstawie przekazanego modelu jeżeli jest to możliwe,
     * w przeciwnym razie wyrzuca wyjątek o niemożliwej transformacji.
     *
     * @param model {@link org.ekipa.pnes.models.netModels.NetModel}.
     * @throws ImpossibleTransformationException Wyjątek informujący o niemożliwej transformacji.
     */
    public abstract void transform(NetModel model) throws ImpossibleTransformationException;

    /**
     * Wykonuje podaną ilość kroków symulacji dla podanej sieci.
     *
     * @param netModel Sieć na której ma zostać przeprowadzona symulacja.
     * @param cycles   Ilość kroków, które mają się wykonać.
     * @return {@link java.util.List}<{@link org.ekipa.pnes.models.netModels.NetModel}> Lista modeli jako kroki symulacji.
     */
    public static List<List<NetModel>> simulate(NetModel netModel, int cycles) throws Exception {
        if (netModel.getNetElements() == null || netModel.getNetElements().isEmpty())
            return Collections.singletonList(Collections.singletonList(netModel));
        List<List<NetModel>> result = new ArrayList<>();
        result.add(Collections.singletonList(netModel));
        for (int i = 0; i < cycles; i++) {
            List<NetModel> previousCycle = result.get(i);
            NetModel lastInCycle = previousCycle.get(previousCycle.size() - 1);
            result.add(lastInCycle.wholeStep());
        }
        return result;
    }

    /**
     * Usuwa podany element z całego modelu sieci.
     *
     * @param element Element który ma zostać usunięty.
     */
    public void deleteElement(NetElement element) {
        netElements = netElements.stream().filter(net -> !net.equals(element)).collect(Collectors.toList());
    }

    /**
     * Dodaje podany element do modelu sieci jeśli przejdzie walidację.
     *
     * @param element Element który ma zostać dodany.
     * @return Dodany element.
     */
    public NetElement addElement(NetElement element) {
        if (!validateElement(element)) return null;
        netElements.add(IdGenerator.setElementId(element));
        return element;
    }

    /**
     * Usuwanie elementu sieci za pomocą jego id {@link org.ekipa.pnes.models.elements.NetElement}.
     *
     * @param id Id elementu który ma zostać usunięty.
     */
    public void deleteById(String id) {
        netElements.stream().filter(net -> net.getId().equals(id)).forEach(this::deleteElement);
    }

    /**
     * Usuwanie elemtu sieci po nazwie {@link org.ekipa.pnes.models.elements.NetElement}.
     *
     * @param name Nazwa elementu do usunięcia.
     */
    public void deleteByName(String name) {
        netElements.stream().filter(net -> net.getName().equals(name)).forEach(this::deleteElement);
    }

    /**
     * Odnajduje wszystkie łuki, które są połączone z obiektem sieci.
     *
     * @param netObject Obiekt sieci.
     * @return Łuki podanego obiektu.
     */
    public Set<Arc> getArcsByNetObject(NetObject netObject) {
        return netObject.getArcs();
    }

    /**
     * Odnajduje wszystkie łuki po id, które są połączone z obiektem sieci.
     *
     * @param id Obiektu sieci.
     * @return Łuki podanego obiektu.
     */

    public Set<Arc> getArcsByNetObjectId(String id) {
        return getObject(id).getArcs();
    }

    /**
     * Odnajduje początek i koniec podanego łuku.
     *
     * @param arcId Id łuku.
     * @return Para obiektów połączonych łukiem (początek, koniec).
     */
    public Pair<NetObject, NetObject> getNetObjectsByArc(String arcId) {
        Stream<NetElement> netElementStream = netElements.stream()
                .filter(netElement -> !(netElement.getId().equals(arcId)));

        NetObject start = (NetObject) netElementStream
                .filter(netElement -> ((Arc) getElement(arcId)).getStart().getId().equals(netElement.getId()))
                .findFirst()
                .orElse(null);

        NetObject end = (NetObject) netElementStream
                .filter(netElement -> ((Arc) getElement(arcId)).getEnd().getId().equals(netElement.getId()))
                .findFirst()
                .orElse(null);

        return new Pair<>(start, end);
    }

    /**
     * Edytuje obiekt modelu jeśli przejdzie walidację.
     *
     * @param actualId Id dokładnego obiektu, który ma zostać zaktualizowany.
     * @param newId    Id obiektu, z którego ma zamienić wartości.
     * @return Zaktualizowany obiekt.
     */
    public NetElement editElement(String actualId, String newId) {
        if (!(actualId.charAt(0) == newId.charAt(0))) return getElement(actualId);
        if (!validateElement(getElement(newId))) return getElement(actualId);
        List<Field> fieldsBefore = getAllFields(getElement(actualId));
        List<String> ignoredFields = Arrays.asList("arcs", "id", "start", "end");
        for (Field f : fieldsBefore.stream().filter(f -> !ignoredFields.contains(f.getName())).collect(Collectors.toList())) {
            f.setAccessible(true);
            try {
                f.set(getElement(actualId), f.get(getElement(newId)));
            } catch (IllegalAccessException ignored) {

            }
            f.setAccessible(false);
        }
        return getElement(actualId);
    }

    /**
     * Przeprowadza walidację dowolnych elementów w modelu sieci.
     *
     * @param o Id obiektu do walidacji.
     * @return Wynik walidacji.
     */
    protected abstract boolean validateElement(NetElement o);

    /**
     * Dodaje tokeny do podanego miejsca.
     *
     * @param placeId Id miejsca do którego mają zostać dodane tokeny.
     * @param tokens  Tokeny.
     */
    protected abstract void addTokens(String placeId, Object tokens);

    /**
     * Wykonuje pojedynczy krok symulacji, zwraca kopie modelu po wykonaniu kroku.
     *
     * @return {@link java.util.List}<{@link org.ekipa.pnes.models.netModels.NetModel}> Model po wykonaniu kroku.
     */
    protected List<NetModel> wholeStep() throws JsonProcessingException {
        List<NetModel> currentSimulationSteps = new ArrayList<>();
        List<Transition> readyTransitions = prepareTransitions();
        currentSimulationSteps.add(this.copy());
        List<Transition> transitionsToRun = selectTransitionsToRun(readyTransitions);
        transitionsToRun = transitionsToRun.stream().peek(Transition::setRunning).collect(Collectors.toList());
        currentSimulationSteps.add(this.copy());
        transitionsToRun.forEach(this::runTransition);
        currentSimulationSteps.add(this.copy());
        getTransitionsWithState(Transition.TransitionState.Ready).forEach(Transition::setUnready);
        currentSimulationSteps.add(this.copy());
        return currentSimulationSteps;
    }

    /**
     * Tworzy kopie obecnego stanu tej klasy {@link org.ekipa.pnes.models.netModels.NetModel} bez referencji.
     *
     * @return Skopiowany element.
     */

    private NetModel copy() throws JsonProcessingException {
        String json = serialize(this);
        return deserialize(json);
    }

    /**
     * Serializuje dany obiekt do JSON'a w postaci stringa.
     *
     * @param netModel Model sieci do serializowania.
     * @return Serializowany obiekt.
     */

    public abstract String serialize(NetModel netModel) throws JsonProcessingException;

    /**
     * Deserializuje dany obiekt w postaci JSON'a.
     *
     * @param json Obiekt {@link org.ekipa.pnes.models.netModels.NetModel} w postaci JSON'a.
     * @return Deserializowany obiekt.
     */

    public abstract NetModel deserialize(String json) throws JsonProcessingException;


    /**
     * Uruchamia podaną tranzycję.
     *
     * @param transition Tranzycja do uruchomienia.
     * @return true jeśli uruchomiono, w przeciwnym przypadku zwraca false.
     */
    protected abstract boolean runTransition(Transition transition);

    /**
     * Odnajduje te tranzycje, które mogą zostać przygotowane, następnie ustawia je jako gotowe.
     *
     * @return {@link java.util.List}<{@link org.ekipa.pnes.models.elements.Transition}> Lista gotowych tranzycji.
     */
    protected abstract List<Transition> prepareTransitions();

    /**
     * Wybiera te tranzycje spośród gotowych, które mają zostać uruchomione.
     *
     * @param transitions {@link java.util.List}<{@link org.ekipa.pnes.models.elements.Transition}> Lista tranzycji do wybrania.
     * @return {@link java.util.List}<{@link org.ekipa.pnes.models.elements.Transition}> Lista tranzycji do uruchomienia.
     */
    protected abstract List<Transition> selectTransitionsToRun(List<Transition> transitions);

    /**
     * Zwraca te tranzycje sieci, które znajdują się w podanym stanie.
     *
     * @param state {@link org.ekipa.pnes.models.elements.Transition.TransitionState}
     * @return {@link java.util.List}<{@link org.ekipa.pnes.models.elements.Transition}> Lista tranzycji.
     */
    protected List<Transition> getTransitionsWithState(Transition.TransitionState state) {
        return netElements.stream()
                .filter(element -> element instanceof Transition)
                .filter(transition -> ((Transition) transition).getState().equals(state))
                .map(netElement -> (Transition) netElement)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Zwraca wszystkie pola podanego obiektu.
     *
     * @param o Obiekt z którego bedą wyciągane pola.
     * @return {@link java.util.List}<{@link java.lang.reflect.Field}> Lista pól podanego obiektu.
     */

    private List<Field> getAllFields(Object o) {
        List<Field> fields = new ArrayList<>();
        Class clazz = o.getClass();
        while (!clazz.equals(Object.class)) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    /**
     * Zwraca wszystkie tranzycje.
     *
     * @return {@link java.util.List}<{@link org.ekipa.pnes.models.elements.Transition}> Lista wszystkich tranzycji.
     */

    @JsonIgnore
    private List<Transition> getAllTransitions() {
        return netElements.stream()
                .filter(element -> element instanceof Transition)
                .map(netElement -> (Transition) netElement)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}