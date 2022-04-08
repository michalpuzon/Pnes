package org.ekipa.pnes.models.elements;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class NetElement {

    private String id;
    private String name;
}
