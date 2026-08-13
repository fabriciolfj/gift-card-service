package com.github.fabriciolfj.giftcard;

import com.github.fabriciolfj.giftcard.configurations.ExpiryProperties;
import com.github.fabriciolfj.giftcard.configurations.GiftCardOrderProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({ExpiryProperties.class, GiftCardOrderProperties.class})
public class GiftcardApplication {

	public static void main(String[] args) {
		SpringApplication.run(GiftcardApplication.class, args);
	}

}
