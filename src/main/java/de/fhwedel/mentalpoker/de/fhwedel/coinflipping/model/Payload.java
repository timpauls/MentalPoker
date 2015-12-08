package de.fhwedel.mentalpoker.de.fhwedel.coinflipping.model;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

/**
 * Created by tim on 08.12.2015.
 */
public class Payload {
    private List<String> initialCoin;
    private String desiredCoin;
    private List<String> encryptedCoin;
    private String enChosenCoin;
    private String deChosenCoin;
    private List<BigInteger> keyA;
    private List<BigInteger> keyB;
    private String signatureA;

    public Payload(List<String> initialCoin, String desiredCoin, List<String> encryptedCoin, String enChosenCoin, String deChosenCoin, List<BigInteger> keyA, List<BigInteger> keyB, String signatureA) {
        this.initialCoin = initialCoin;
        this.desiredCoin = desiredCoin;
        this.encryptedCoin = encryptedCoin;
        this.enChosenCoin = enChosenCoin;
        this.deChosenCoin = deChosenCoin;
        this.keyA = keyA;
        this.keyB = keyB;
        this.signatureA = signatureA;
    }

    public List<String> getInitialCoin() {
        return initialCoin;
    }

    public void setInitialCoin(List<String> initialCoin) {
        this.initialCoin = initialCoin;
    }

    public String getDesiredCoin() {
        return desiredCoin;
    }

    public void setDesiredCoin(String desiredCoin) {
        this.desiredCoin = desiredCoin;
    }

    public List<String> getEncryptedCoin() {
        return encryptedCoin;
    }

    public void setEncryptedCoin(List<String> encryptedCoin) {
        this.encryptedCoin = encryptedCoin;
    }

    public String getEnChosenCoin() {
        return enChosenCoin;
    }

    public void setEnChosenCoin(String enChosenCoin) {
        this.enChosenCoin = enChosenCoin;
    }

    public String getDeChosenCoin() {
        return deChosenCoin;
    }

    public void setDeChosenCoin(String deChosenCoin) {
        this.deChosenCoin = deChosenCoin;
    }

    public List<BigInteger> getKeyA() {
        return keyA;
    }

    public void setKeyA(List<BigInteger> keyA) {
        this.keyA = keyA;
    }

    public List<BigInteger> getKeyB() {
        return keyB;
    }

    public void setKeyB(List<BigInteger> keyB) {
        this.keyB = keyB;
    }

    public String getSignatureA() {
        return signatureA;
    }

    public void setSignatureA(String signatureA) {
        this.signatureA = signatureA;
    }

    public static class Builder {
        private List<String> initialCoin;
        private String desiredCoin;
        private List<String> encryptedCoin;
        private String enChosenCoin;
        private String deChosenCoin;
        private List<BigInteger> keyA;
        private List<BigInteger> keyB;
        private String signatureA;

        public Builder setInitialCoin(String... initialCoin) {
            this.initialCoin = Arrays.asList(initialCoin);
            return this;
        }

        public Builder setDesiredCoin(String desiredCoin) {
            this.desiredCoin = desiredCoin;
            return this;
        }

        public Builder setEncryptedCoin(String... encryptedCoin) {
            this.encryptedCoin = Arrays.asList(encryptedCoin);
            return this;
        }

        public Builder setEnChosenCoin(String enChosenCoin) {
            this.enChosenCoin = enChosenCoin;
            return this;
        }

        public Builder setDeChosenCoin(String deChosenCoin) {
            this.deChosenCoin = deChosenCoin;
            return this;
        }

        public Builder setKeyA(BigInteger... keyA) {
            this.keyA = Arrays.asList(keyA);
            return this;
        }

        public Builder setKeyB(BigInteger... keyB) {
            this.keyB = Arrays.asList(keyB);
            return this;
        }

        public Builder setSignatureA(String signatureA) {
            this.signatureA = signatureA;
            return this;
        }

        public Payload build() {
            return new Payload(initialCoin, desiredCoin, encryptedCoin, enChosenCoin, deChosenCoin, keyA, keyB, signatureA);
        }
    }
}
