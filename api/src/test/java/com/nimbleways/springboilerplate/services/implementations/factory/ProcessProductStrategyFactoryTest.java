package com.nimbleways.springboilerplate.services.implementations.factory;

import com.nimbleways.springboilerplate.enums.ProductType;
import com.nimbleways.springboilerplate.services.product.ProcessProductStrategy;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.mock;

@ExtendWith(SpringExtension.class)
@UnitTest
class ProcessProductStrategyFactoryTest {
    private Map<String, ProcessProductStrategy> strategyMap;
    private ProcessProductStrategyFactory factory;

    @BeforeEach
    void setup() {
        strategyMap = new HashMap<>();
        factory = new ProcessProductStrategyFactory(strategyMap);
    }

    @Test
    void getProductStrategy_shouldReturnCorrectStrategy_whenTypeIsValid() {
        ProductType type = ProductType.SEASONAL;
        ProcessProductStrategy strategy = mock(ProcessProductStrategy.class);

        strategyMap.put(type.name(), strategy);

        ProcessProductStrategy result = factory.getProductStrategy(type);

        assertThat(result).isSameAs(strategy);
    }

    @Test
    void getProductStrategy_shouldThrow_whenTypeNotInMapOrNull() {
        ProductType type = null;

        assertThatThrownBy(() -> factory.getProductStrategy(type))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unexpected product type");
    }

}
