package com.example.afferentcoupling.model;

import java.util.List;

public class ResponseObject {
    private List<AfferentCouplingData> afferent_history;

    public void setAfferent_history(List<AfferentCouplingData> afferent_history) {
        this.afferent_history = afferent_history;
    }

    public List<AfferentCouplingData> getAfferent_history() {
        return afferent_history;
    }

    private AfferentCouplingData current_afferent;

    public ResponseObject(List<AfferentCouplingData> afferent_history, AfferentCouplingData current_afferent) {
        this.afferent_history = afferent_history;
        this.current_afferent = current_afferent;
    }

    public void setCurrent_afferent(AfferentCouplingData current_afferent) {
        this.current_afferent = current_afferent;
    }

    public AfferentCouplingData getCurrent_afferent() {
        return current_afferent;
    }
}
