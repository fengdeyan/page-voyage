package me.yan;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;

public class MyTest2 {

        @Test
        public void a() {
            HashMap<String, String> stringStringHashMap = new HashMap<>();
            stringStringHashMap.put("1","fuzhou");
            stringStringHashMap.put("2","xiamen");
            String compact = Jwts.builder().signWith(SignatureAlgorithm.HS256, "ZnV6aG91ZGF4dWU=")
                    .addClaims(stringStringHashMap)
                    .setExpiration(new Date(System.currentTimeMillis() + 3600 * 10000))
                    .compact();
            System.out.println(compact);
        }
}
