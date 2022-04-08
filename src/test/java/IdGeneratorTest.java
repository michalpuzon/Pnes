import org.ekipa.pnes.models.elements.Place;
import org.ekipa.pnes.models.elements.NetObject;
import org.ekipa.pnes.models.elements.Transition;
import org.ekipa.pnes.utils.IdGenerator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IdGeneratorTest {

    private NetObject rectangle1;
    private NetObject rectangle2;
    private NetObject rectangle3;
    private NetObject circle1;
    private NetObject circle2;
    private NetObject circle3;


    @BeforeEach
    public void initialize() {

        IdGenerator.resetElements();
        rectangle1 = new Transition("", "P", 2, 3);
        rectangle2 = new Transition("", "P", 4, 5);
        rectangle3 = new Transition("", "P", 4, 5);

        circle1 = new Place<Integer>("", "K", 2, 3, 5);
        circle2 = new Place<Integer>("", "K", 2, 3, 5);
        circle3 = new Place<Integer>("", "K", 2, 3, 5);


    }

    @Test
    public void doesIdWillBeChangedToCorrect() {
        rectangle1.setId("P5");

        String expected = "T1";
        String actual = IdGenerator.setElementId(rectangle1).getId();

        assertEquals(expected, actual);
    }

    @Test
    public void doesNetElementsHaveTheSameIdBeforeUsingSetIdAndAfter() {

        rectangle1.setId("T1");
        String expected = rectangle1.getId();
        String actual = IdGenerator.setElementId(rectangle1).getId();

        assertEquals(expected, actual);
    }

    @Test
    public void doesIdWillBeChangedForTheObjectWithTheSameFields() {
        rectangle1 = new Transition("P0", "P", 2, 3);
        rectangle2 = new Transition("P0", "P", 2, 3);
        rectangle3 = new Transition("P0", "P", 2, 3);


        IdGenerator.setElementId(rectangle1);
        IdGenerator.setElementId(rectangle2);
        IdGenerator.setElementId(rectangle3);

        String expected = "T3";
        String actual = rectangle3.getId();

        assertEquals(expected, actual);
    }

    @Test
    public void doesIdGenerateWellForPlace() {

        String expected;
        String actual;

        IdGenerator.setElementId(circle1);
        expected = "P1";
        actual = circle1.getId();

        assertEquals(expected, actual);

        IdGenerator.setElementId(circle2);
        expected = "P2";
        actual = circle2.getId();

        assertEquals(expected, actual);

        IdGenerator.setElementId(circle3);
        expected = "P3";
        actual = circle3.getId();

        assertEquals(expected, actual);

    }

    @Test
    public void doesIdGenerateWellForTranistion() {

        String expected;
        String actual;

        IdGenerator.setElementId(rectangle1);
        expected = "T1";
        actual = rectangle1.getId();

        assertEquals(expected, actual);

        IdGenerator.setElementId(rectangle2);
        expected = "T2";
        actual = rectangle2.getId();

        assertEquals(expected, actual);

        IdGenerator.setElementId(rectangle3);
        expected = "T3";
        actual = rectangle3.getId();

        assertEquals(expected, actual);

    }

    @Test
    public void doesIdGenerateWellForPlaceAndTransitionTogether() {

        String expected;
        String actual;

        IdGenerator.setElementId(rectangle1);
        expected = "T1";
        actual = rectangle1.getId();

        assertEquals(expected, actual);

        IdGenerator.setElementId(circle1);
        expected = "P1";
        actual = circle1.getId();

        assertEquals(expected, actual);

        IdGenerator.setElementId(rectangle2);
        expected = "T2";
        actual = rectangle2.getId();

        assertEquals(expected, actual);

        IdGenerator.setElementId(circle2);
        expected = "P2";
        actual = circle2.getId();

        assertEquals(expected, actual);

        IdGenerator.setElementId(rectangle3);
        expected = "T3";
        actual = rectangle3.getId();

        assertEquals(expected, actual);


        IdGenerator.setElementId(circle3);
        expected = "P3";
        actual = circle3.getId();

        assertEquals(expected, actual);


    }

}

