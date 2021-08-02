LTO Network client for Java
===

_Signing and addresses work for the public chain only._

Requirements
---

- JDK 11 or later

Accounts
---

### Creation

#### Create an account from seed

```java
import com.ltonetwork.client.core.AccountFactory;
String seed = "my seed phrase";

AccountFactory af = new AccountFactory('T');
Account acc = af.seed(seed);
```

#### Create an account from sign public key

```java
import com.ltonetwork.client.types.Key;
import com.ltonetwork.client.core.AccountFactory;

Key signKey = new Key("wJ4WH8dD88fSkNdFQRjaAhjFUZzZhV5yiDLDwNUnp6bYwRXrvWV8MJhQ9HL9uqMDG1n7XpTGZx7PafqaayQV8Rp", Encoding.BASE58);

AccountFactory af = new AccountFactory('T');
Account acc = af.createPublic(signKey);
```

#### Create an account from full info

```java
import com.ltonetwork.client.types.Key;
import com.ltonetwork.client.types.KeyPair;
import com.ltonetwork.client.core.AccountFactory;

KeyPair signKeyPair = new KeyPair (
    new Key("wJ4WH8dD88fSkNdFQRjaAhjFUZzZhV5yiDLDwNUnp6bYwRXrvWV8MJhQ9HL9uqMDG1n7XpTGZx7PafqaayQV8Rp", Encoding.BASE58);
    new Key("FkU1XyfrCftc4pQKXCrrDyRLSnifX1SMvmx1CYiiyB3Y", Encoding.BASE58);
);
KeyPair encryptKeyPair = new KeyPair (
    Key encryptPublicKey = new Key("BVv1ZuE3gKFa6krwWJQwEmrLYUESuUabNCXgYTmCoBt6", Encoding.BASE58);
    Key encryptPrivateKey = new Key("BnjFJJarge15FiqcxrB7Mzt68nseBXXR4LQ54qFBsWJN", Encoding.BASE58);
);
Address address = new Address("3JmCa4jLVv7Yn2XkCnBUGsa7WNFVEMxAfWe", 'T');

AccountFactory af = new AccountFactory('T');
Account acc = af.create(
    signKeyPair,
    encryptKeyPair,
    address);
```

Properties that are specified will be verified. Properties that are omitted will be generated where possible.

### Signing (ED25519)

#### Sign a message

```java
Signature sig = account.sign("my message"); // Base58 encoded signature
```

#### Verify a signature

```java
String message = "my message";
Signature sig = account.sign(message);
boolean isValid = account.verify(sig, message) // True
```

### Encryption (X25519)

#### Encrypt a message for another account

```java
String message = "my message";

byte[] encrypted = senderAccount.encrypt(recipientAccount, message)
```

You can use `senderAccount.encrypt(senderAccount, message);` to encrypt a message for yourself.

#### Decrypt a message received from another account

```java
String message = "my message";

byte[] decrypted = recipientAccount.decrypt(senderAccount, message)
```

You can use `senderAccount.encrypt(senderAccount, message);` to decrypt a message from yourself.

## Public layer

```java
PublicNode publicNode = new PublicNode(new URI("https://nodes.lto.network"), "myNodeApiKey");

int amount = 1000; // Amount of LTO to transfer
Address recipient = new Address("3JmCa4jLVv7Yn2XkCnBUGsa7WNFVEMxAfWe", 'T');

Transfer tx = new Transfer($amount, $recipient);
tx.signWith(myAccount);
publicNode.broadcast(tx);
```
