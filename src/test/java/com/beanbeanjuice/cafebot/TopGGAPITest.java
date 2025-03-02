package com.beanbeanjuice.cafebot;

import com.beanbeanjuice.CafeBot;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.security.auth.login.LoginException;
import java.util.concurrent.ExecutionException;

public class TopGGAPITest {

    @Test
    @DisplayName("Top.GG API Test")
    public void topAPITest() throws LoginException, InterruptedException, ExecutionException {
        new CafeBot();
        Assertions.assertEquals(CafeBot.getPrefix(), CafeBot.getTopGGAPI().getBot(System.getenv("CAFEBOT_TOPGG_ID")).toCompletableFuture().get().getPrefix());
    }

}
