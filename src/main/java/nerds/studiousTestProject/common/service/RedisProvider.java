package nerds.studiousTestProject.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 에서 여러 가지 자료구조의 데이터를 읽기/쓰기 하는 클래스
 */
@RequiredArgsConstructor
@Service
public class RedisProvider {
    private final RedisTemplate<String, Object> redisTemplate;

    public <T> void addValueData(String key, T value, Long expiredTime) {
        redisTemplate.opsForValue().set(key, value, expiredTime, TimeUnit.MILLISECONDS);
    }

    public <T> void addSetData(String key, T value) {
        redisTemplate.opsForSet().add(key, value);
    }

    public Object getValueData(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public Set<Object> getSetData(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    public void deleteValueData(String key) {
        redisTemplate.opsForValue().getAndDelete(key);
    }

    public void deleteSetData(String key) {
        redisTemplate.opsForSet().remove(key);
    }
}
