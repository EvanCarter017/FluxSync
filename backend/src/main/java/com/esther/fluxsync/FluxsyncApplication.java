package com.esther.fluxsync;

import com.esther.fluxsync.service.RedisService;
import com.esther.fluxsync.utils.LogConverter;
import com.esther.fluxsync.websocket.handler.MyWebSocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@EnableAspectJAutoProxy
@SpringBootApplication
public class FluxsyncApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(FluxsyncApplication.class);
	private final RedisService redisService;

	public FluxsyncApplication(RedisService redisService) {
		this.redisService = redisService;
	}

	public static void main(String[] args) {
		SpringApplication.run(FluxsyncApplication.class, args);
	}

	@Override
	public void run(String... args) {
		redisService.clear();
		log.info(LogConverter.info("Redis 已清空!"));
	}

}
