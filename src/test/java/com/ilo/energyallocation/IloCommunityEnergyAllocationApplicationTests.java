package com.ilo.energyallocation;

import com.ilo.energyallocation.config.TestConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestConfig.class)
class IloCommunityEnergyAllocationApplicationTests {

    @Test
    void contextLoads() {
    }

}
