package org.ekipa.pnes.models.netModels;

public enum NetModelType {
    PT_NET(PTNetModel.class);

    Class<NetModel> type;

    NetModelType(Class<? extends NetModel> type) {
        this.type = (Class<NetModel>) type;
    }

    public Class<NetModel> getType() {
        return type;
    }
}
