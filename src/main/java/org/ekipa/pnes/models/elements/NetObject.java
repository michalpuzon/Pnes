package org.ekipa.pnes.models.elements;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
public abstract class NetObject extends NetElement {

    private double x, y;

    @JsonIgnore
    private Set<Arc> arcs;

    public NetObject() {
        this.arcs = new HashSet<>();
    }

    public NetObject(String id, String name, double x, double y) {
        this.setId(id);
        this.setName(name);
        this.x = x;
        this.y = y;
        this.arcs = new HashSet<>();
    }

    public void addArc(Arc arc) {
        this.arcs.add(arc);
    }


    @Override
    public String toString() {
        return "NetElement{" +
                "id='" + this.getId() + '\'' +
                ", name='" + this.getName() + '\'' +
                ", x=" + x +
                ", y=" + y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NetObject that = (NetObject) o;
        if (Double.compare(that.getX(), getX()) != 0) return false;
        if (Double.compare(that.getY(), getY()) != 0) return false;
        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        return !(getName() != null ? !getName().equals(that.getName()) : that.getName() != null);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getName() != null ? getName().hashCode() : 0);
        temp = Double.doubleToLongBits(getX());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(getY());
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + getArcs().hashCode();
        return result;
    }
}
