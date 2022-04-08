package org.ekipa.pnes.models.elements;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Place<V> extends NetObject {
    private int tokenCapacity;
    private V tokens;


    public Place(String id, String name, double x, double y, int tokenCapacity) {
        this(id, name, x, y, tokenCapacity, null);
    }

    public Place(String id, String name, double x, double y, int tokenCapacity, V tokens) {
        super(id, name, x, y);
        this.tokenCapacity = tokenCapacity;
        this.tokens = tokens;
    }
}
