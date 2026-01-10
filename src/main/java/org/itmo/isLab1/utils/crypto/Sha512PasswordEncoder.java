package org.itmo.isLab1.utils.crypto;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

@Component
public class Sha512PasswordEncoder implements PasswordEncoder {
  @Override
  public String encode(CharSequence rawPassword) {
    byte[] bytes = rawPassword.toString().getBytes(StandardCharsets.UTF_8);

    try {
      var md = MessageDigest.getInstance("SHA-512");
      md.update(bytes);
      byte[] passwordDigest = md.digest();
      return new String(Base64.getEncoder().encode(passwordDigest));
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("No SHA-512 algorithm in MessageDigest", e);
    }
  }

  @Override
  public boolean matches(CharSequence rawPassword, String encodedPassword) {
    var rawEncoded = encode(rawPassword);
    return Objects.equals(rawEncoded, encodedPassword);
  }
}
