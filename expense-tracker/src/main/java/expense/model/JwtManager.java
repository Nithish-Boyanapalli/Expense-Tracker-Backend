package expense.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtManager {
  
  public final String SEC_KEY = "hsdbhbvjjerigjjkebn5rg5341e51g1tg135rt1g3510rt2g153drigergbg545g1656544546564684";
  public final SecretKey key = Keys.hmacShaKeyFor(SEC_KEY.getBytes());
  
  public String generateToken(String email)
  {
    Map<String, String> data = new HashMap<String, String>();
    data.put("email", email);
    
    return Jwts.builder()
        .setClaims(data)
        .setIssuedAt(new Date())
        .setExpiration(new Date(new Date().getTime() + 864000000))
        .signWith(key)
        .compact();
  }
}