package org.ekipa.pnes.models.elements;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.ekipa.pnes.models.exceptions.ProhibitedConnectionException;

@Data
@NoArgsConstructor
public class Arc extends NetElement {
    private NetObject start;
    private NetObject end;
    private double weight;


    public Arc(String id, NetObject start, NetObject end, double weight) throws ProhibitedConnectionException {
        validateElements(start, end);
        this.setId(id);
        this.start = start;
        this.end = end;
        this.weight = weight;
        this.start.addArc(this);
        this.end.addArc(this);
    }

    public Arc(NetObject start, NetObject end, double weight) throws ProhibitedConnectionException {
        this("", start, end, weight);
    }

    public void setStart(NetObject start) throws ProhibitedConnectionException {
        validateElements(start, this.end);
        this.start = start;
    }

    public void setEnd(NetObject end) throws ProhibitedConnectionException {
        validateElements(this.start, end);
        this.end = end;
    }

    /**
     * Sprawdza czy elementy są tego samego typu, jeżeli tak to
     * zostanie wyrzucony wyjątek.
     *
     * @param start Element początkowy łuku.
     * @param end   Element końcowy łuku.
     * @throws ProhibitedConnectionException W momencie połączenia błędnych obiektów za pomocą łuku.
     */
    private void validateElements(NetObject start, NetObject end) throws ProhibitedConnectionException {
        if (start.getClass().equals(end.getClass()))
            throw new ProhibitedConnectionException(String.format("Start and end of an arc cannot both be %s", start.getClass().getSimpleName()));

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Arc arc = (Arc) o;
        if (Double.compare(arc.getWeight(), getWeight()) != 0) return false;
        if (getId() != null ? !getId().equals(arc.getId()) : arc.getId() != null) return false;
        if (getStart() != null ? !getStart().equals(arc.getStart()) : arc.getStart() != null) return false;
        return getEnd() != null ? getEnd().equals(arc.getEnd()) : arc.getEnd() == null;
    }

    @Override
    public String toString() {
        return "Arc{" +
                "id='" + this.getId() + '\'' +
                ", start=" + start +
                ", end=" + end +
                ", weight=" + weight +
                '}';
    }
}
