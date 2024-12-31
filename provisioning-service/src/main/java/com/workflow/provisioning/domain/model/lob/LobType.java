package com.workflow.provisioning.domain.model.lob;

import lombok.Getter;

@Getter
public enum LobType {
    INTERNET("Internet")
    , IPTV("IPTV");

    private final String lob;

    LobType(String lob) {
        this.lob = lob;
    }
}
