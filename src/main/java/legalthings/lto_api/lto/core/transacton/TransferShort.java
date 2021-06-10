package legalthings.lto_api.lto.core.transacton;

public class TransferShort {
    private final String recipient;
    private final int amount;

    public TransferShort(String recipient, int amount) {
        this.recipient = recipient;
        this.amount = amount;
    }

    public String getRecipient() {
        return recipient;
    }

    public int getAmount() {
        return amount;
    }
}
