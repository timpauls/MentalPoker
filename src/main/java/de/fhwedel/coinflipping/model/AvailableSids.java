package de.fhwedel.coinflipping.model;

import java.util.Arrays;
import java.util.List;

/**
 * Created by tim on 08.12.2015.
 */
public class AvailableSids {
    private List<Integer> sids;

    public AvailableSids(Integer... sids) {
        this.sids = Arrays.asList(sids);
    }

    public List<Integer> getSids() {
        return sids;
    }

    public void setSids(List<Integer> sids) {
        this.sids = sids;
    }
}
