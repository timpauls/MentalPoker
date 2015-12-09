package de.fhwedel.coinflipping.model;

import java.util.Arrays;
import java.util.List;

/**
 * Created by tim on 08.12.2015.
 */
public class ProtocolNegotiation {
    private String version;
    private List<AvailableVersion> availableVersions;

    public ProtocolNegotiation(AvailableVersion... availableVersions) {
        this.availableVersions = Arrays.asList(availableVersions);
    }

    public ProtocolNegotiation(String version, AvailableVersion... availableVersions) {
        this(availableVersions);
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<AvailableVersion> getAvailableVersions() {
        return availableVersions;
    }

    public void setAvailableVersions(List<AvailableVersion> availableVersions) {
        this.availableVersions = availableVersions;
    }

}
