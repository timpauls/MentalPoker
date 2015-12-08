package de.fhwedel.mentalpoker.de.fhwedel.coinflipping.model;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tim on 08.12.2015.
 */
public class KeyNegotiation {
    private BigInteger p;
    private BigInteger q;
    private Integer sid;
    private List<AvailableSids> availableSids;

    public KeyNegotiation(BigInteger p, BigInteger q, Integer sid, AvailableSids... availableSids) {
        this.p = p;
        this.q = q;
        this.sid = sid;
        this.availableSids = Arrays.asList(availableSids);
    }

    public BigInteger getP() {
        return p;
    }

    public void setP(BigInteger p) {
        this.p = p;
    }

    public BigInteger getQ() {
        return q;
    }

    public void setQ(BigInteger q) {
        this.q = q;
    }

    public Integer getSid() {
        return sid;
    }

    public void setSid(Integer sid) {
        this.sid = sid;
    }

    public List<AvailableSids> getAvailableSids() {
        return availableSids;
    }

    public void setAvailableSids(List<AvailableSids> availableSids) {
        this.availableSids = availableSids;
    }

}
