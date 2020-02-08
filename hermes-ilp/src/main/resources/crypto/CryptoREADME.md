# Connector Crypto
This Connector implementation supports various mechanism to support Encryption and Signing operations.

## Keystores
To use a Java Keystore as your location of any key material, first create a Keystore using the following commands:

```bash
> keytool -keystore ./crypto.p12 -storetype PKCS12 -genseckey -alias secret0 -keyalg aes -keysize 256
> keytool -keystore ./crypto.p12 -storetype PKCS12 -list
``` 

Note the JKS and secret0 password used.

Next, update the following properties in your `application.yml` file:

```yaml
jks:
  jks_filename: crypto.p12
  jks_password: password
  secret0_alias: secret0
  secret0_password: password
```

Finally, make sure the JKS file `crypto.pkcs12` is added to the runtime classpath of the Connector.

### JKS
### Google GCP
### Vault