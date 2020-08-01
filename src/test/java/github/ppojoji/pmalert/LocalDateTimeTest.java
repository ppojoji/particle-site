package github.ppojoji.pmalert;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

class LocalDateTimeTest {

	@Test
	void test() {
		LocalDateTime time = LocalDateTime.parse("2020-08-01 12:00".replace(' ', 'T') + ":00");
		System.out.println(time);
	}

}
