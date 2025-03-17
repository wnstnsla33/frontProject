package pProject.pPro.smtp;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisUtil {
	private final StringRedisTemplate redisTemplate;
	
	public String getData(String key) {
		ValueOperations<String,String> valueOperation = redisTemplate.opsForValue();
		return valueOperation.get(key);
	}
	public boolean existData(String key) {
		return Boolean.TRUE.equals(redisTemplate.hasKey(key));
	}
	
	public void setDataExpire(String key,String value,long duration) {
		ValueOperations<String,String> valueOperation = redisTemplate.opsForValue();
		Duration expireDuration = Duration.ofSeconds(duration);
		valueOperation.set(key, value,expireDuration);
	}
	public void deleteData(String key) {
		redisTemplate.delete(key);
	}
}
