package org.ekipa.pnes.api.configs.security;

import java.util.ArrayList;
import java.util.List;

public abstract class Controller {

    private static final List<Controller> accessibleControllers;
    static {
        accessibleControllers = new ArrayList<>();
    }

    public Controller() {
        accessibleControllers.add(this);
    }

    public static List<Controller> getAccessibleControllers() {
        return accessibleControllers;
    }
}
