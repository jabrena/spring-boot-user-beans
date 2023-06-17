package info.jab.ms;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.beans.BeansEndpoint;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class MainApplicationTests {

	@Autowired
	private BeansEndpoint beansEndpoint;

	@Test
	void contextLoads() {
		assertThat(beansEndpoint).isNotNull();
	}
}
