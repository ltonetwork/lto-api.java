package legalthings.lto_api.lto.core;

public class KeyPair {
    private byte[] secretkey;
    private byte[] publickey;

    public KeyPair(byte[] publickey, byte[] secretkey) {
        if (publickey != null) {
            this.publickey = new byte[publickey.length];
            System.arraycopy(publickey, 0, this.publickey, 0, publickey.length);
        }
        if (secretkey != null) {
            this.secretkey = new byte[secretkey.length];
            System.arraycopy(secretkey, 0, this.secretkey, 0, secretkey.length);
        }
    }

    public KeyPair() {
        this.publickey = null;
        this.secretkey = null;
    }

    public void setSecretkey(byte[] secretkey) {
        this.secretkey = new byte[secretkey.length];

        System.arraycopy(secretkey, 0, this.secretkey, 0, secretkey.length);
    }

    public byte[] getSecretkey() {
        return this.secretkey;
    }

    public void setPublickey(byte[] publickey) {
        this.publickey = new byte[publickey.length];

        System.arraycopy(publickey, 0, this.publickey, 0, publickey.length);
    }

    public byte[] getPublickey() {
        return this.publickey;
    }
}
