package de.fhwedel.mentalpoker.de.fhwedel.coinflipping.model;

/**
 * Sid according to http://redmine.fh-wedel.de/attachments/125/protocol.txt
 */
public enum Sid {
    SRA1024SHA1(0, "1024", "SHA1"),
    SRA2048SHA1(1, "2048", "SHA1"),
    SRA3072SHA1(2, "3072", "SHA1"),
    SRA4096SHA1(3, "4096", "SHA1"),

    SRA1024SHA256(10, "1024", "SHA256"),
    SRA2048SHA256(11, "2048", "SHA256"),
    SRA3072SHA256(12, "3072", "SHA256"),
    SRA4096SHA256(13, "4096", "SHA256"),

    SRA2048SHA512(20, "2048", "SHA512"),
    SRA3072SHA512(21, "3072", "SHA512"),
    SRA4096SHA512(22, "4096", "SHA512"),
    SRA8192SHA512(23, "8192", "SHA512"),
    SRA16384SHA512(24, "16384", "SHA512");

    private final int id;
    private final String modulus;
    private final String algorithm;

    Sid (int id, String modulus, String algorithm) {
        this.id = id;
        this.modulus = modulus;
        this.algorithm = algorithm;
    }

    public int getId() {
        return id;
    }

    public String getModulus() {
        return modulus;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public Sid findById(int id) {
        // TODO: hacks!
        if (id < 4) {
            return Sid.values()[id];
        } else if (id >= 10 && id < 13) {
            return Sid.values()[id-6];
        } else if (id >= 20 && id < 25) {
            return Sid.values()[id-12];
        }

        return null;
    }
}
