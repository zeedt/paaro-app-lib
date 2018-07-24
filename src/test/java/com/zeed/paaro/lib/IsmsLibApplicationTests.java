package com.zeed.paaro.lib;

import com.zeed.paaro.lib.email.sendgrid.Example;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
public class IsmsLibApplicationTests {

	@Test
	public void contextLoads() throws IOException {
		Example.baselineExample();
	}

}
