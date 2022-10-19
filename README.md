![LTO github readme](https://user-images.githubusercontent.com/100821/196711741-96cd4ba5-932a-4e95-b420-42d4d61c21fd.png)

LTO Network client for Java
===

Requirements
---
JDK 11

Accounts
---

### Creation

#### Create an account from seed

```java
    String seed = "fragile because fox snap picnic mean art observe vicious program chicken purse text hidden chest";

    AccountFactory af = new AccountFactory(AccountFactory.testnetByte());
    Account acc = af.createFromSeed(seed);
```

#### Create an account from sign public key

```java
    import com.ltonetwork.client.types.PublicKey;
    import com.ltonetwork.client.core.AccountFactory;

    PublicKey signPublicKey = new PublicKey("wJ4WH8dD88fSkNdFQRjaAhjFUZzZhV5yiDLDwNUnp6bYwRXrvWV8MJhQ9HL9uqMDG1n7XpTGZx7PafqaayQV8Rp", Encoding.BASE58);

    AccountFactory af = new AccountFactory(AccountFactory.testnetByte());
    Account acc = af.createPublic(signPublicKey);
```

#### Create an account from full info

```java
    import com.ltonetwork.client.types.Key;
    import com.ltonetwork.client.types.KeyPair;
    import com.ltonetwork.client.core.AccountFactory;

    KeyPair signKeyPair = new KeyPair (
        new PublicKey("wJ4WH8dD88fSkNdFQRjaAhjFUZzZhV5yiDLDwNUnp6bYwRXrvWV8MJhQ9HL9uqMDG1n7XpTGZx7PafqaayQV8Rp", Encoding.BASE58),
        new PrivateKey("FkU1XyfrCftc4pQKXCrrDyRLSnifX1SMvmx1CYiiyB3Y", Encoding.BASE58)
    );

    KeyPair encryptKeyPair = new KeyPair (
        new PublicKey("BVv1ZuE3gKFa6krwWJQwEmrLYUESuUabNCXgYTmCoBt6", Encoding.BASE58),
        new PrivateKey("BnjFJJarge15FiqcxrB7Mzt68nseBXXR4LQ54qFBsWJN", Encoding.BASE58)
    );

    Address address = new Address("3JmCa4jLVv7Yn2XkCnBUGsa7WNFVEMxAfWe");

    AccountFactory af = new AccountFactory(AccountFactory.testnetByte());
    Account acc = af.create(
        signKeyPair,
        encryptKeyPair,
        address);
```

Properties that are specified will be verified. Properties that are omitted will be generated where possible.

### Signing (ED25519)

#### Sign a message

```java
    import com.ltonetwork.seasalt.sign.Signature;
    
    Signature sig = account.sign("my message");
```

#### Verify a signature

```java
    import com.ltonetwork.seasalt.sign.Signature;

    String message = "my message";
    Signature sig = account.sign(message);
    boolean isValid = account.verify(sig, message); // True
```

### Encryption (X25519)

#### Encrypt a message for another account

```java
    String message = "my message";

    byte[] encrypted = senderAccount.encrypt(recipientAccount, message);
```

You can use `senderAccount.encrypt(senderAccount, message);` to encrypt a message for yourself.

#### Decrypt a message received from another account

```java
    String message = "my message";

    byte[] decrypted = recipientAccount.decrypt(senderAccount, message);
```

You can use `senderAccount.encrypt(senderAccount, message);` to decrypt a message from yourself.

Public layer
---

```java
    PublicNode publicNode = new PublicNode(new URI("https://testnet.lto.network"), "myApiKey");

    int amount = 1000; // Amount of LTO to transfer
    Address recipient = new Address("3JmCa4jLVv7Yn2XkCnBUGsa7WNFVEMxAfWe");

    Transfer tx = new Transfer(amount, recipient);
    tx.signWith(myAccount);
    publicNode.broadcast(tx);
```
