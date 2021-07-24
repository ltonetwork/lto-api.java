LTO Network client for PHP
===

_Signing and addresses work for the public chain only._

Requirements
---

- JDK 11 or later

Accounts
---

### Creation

#### Create an account from seed

```java com.ltonetwork.client.core
String seed = "my seed phrase";

AccountFactory af = new AccountFactory("Testnet");
Account acc = af.seed(seed);
```

#### Create an account from sign and encrypt public keys

```java com.ltonetwork.client.core
Key signKey = new Key("mySignKeyValue", Encoding.BASE58);
Key encryptKey = new Key("myEncryptKeyValue", Encoding.BASE58);

AccountFactory af = new AccountFactory("Testnet");
Account acc = af.createPublic(signKey, (byte) 84, encryptKey);
```

#### Create an account from full info

```java com.ltonetwork.client.core
KeyPair signKeyPair = new KeyPair (
    new Key("mySignPublicKey", Encoding.BASE58);
    new Key("mySignPrivateKey", Encoding.BASE58);
);
KeyPair encryptKeyPair = new KeyPair (
    Key encryptPublicKey = new Key("myEncryptPublicKey", Encoding.BASE58);
    Key encryptPrivateKey = new Key("myEncryptPrivateKey", Encoding.BASE58);
);

AccountFactory af = new AccountFactory("Testnet");
Account acc = af.create(
    signKeyPair,
    (byte) 84,
    encryptKeyPair,
    "myAddress".getBytes());
```

Properties that are specified will be verified. Properties that are omitted will be generated where possible.

### Signing (ED25519)

#### Sign a message

```java com.ltonetwork.client.core
Signature sig = account.sign("my message"); // Base58 encoded signature
```

#### Verify a signature

```java com.ltonetwork.client.core
String message = "my message";
Signature sig = account.sign(message);
boolean isValid = account.verify(sig, message) // True
```

### Encryption (X25519)

#### Encrypt a message for another account

```java com.ltonetwork.client.core
String message = "my message";

byte[] encrypted = senderAccount.encrypt(recipientAccount, message)
```

You can use `senderAccount.encrypt(senderAccount, message);` to encrypt a message for yourself.

#### Decrypt a message received from another account

```java com.ltonetwork.client.core
String message = "my message";

byte[] decrypted = recipientAccount.decrypt(senderAccount, message)
```

You can use `senderAccount.encrypt(senderAccount, message);` to decrypt a message from yourself.

## Public layer

```java com.ltonetwork.client.core
use LTO\Transaction\Transfer;
use LTO\PublicNode;

PublicNode publicNode = new PublicNode(new URI("https://nodes.lto.network"), "myNodeApiKey");

int amount = 1000; // Amount of LTO to transfer
Address recipient = new Address("recipientAddress", (byte) 84);

Transfer tx = new Transfer($amount, $recipient);
tx.signWith(myAccount);
publicNode.broadcast(tx);
```
