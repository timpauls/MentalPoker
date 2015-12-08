package de.fhwedel.mentalpoker.de.fhwedel.coinflipping.model;

import java.util.Arrays;
import java.util.List;

/**
 * Created by tim on 08.12.2015.
 */
public class AvailableVersion {
    private List<String> versions;

    public AvailableVersion(String... versions) {
        this.versions = Arrays.asList(versions);
    }

    public List<String> getVersions() {
        return versions;
    }

    public void setVersions(List<String> versions) {
        this.versions = versions;
    }
}
