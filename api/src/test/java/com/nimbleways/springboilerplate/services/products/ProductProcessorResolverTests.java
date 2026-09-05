package com.nimbleways.springboilerplate.services.products;

import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.entities.ProductType;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;

import java.util.List;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@UnitTest
class ProductProcessorResolverTests {

    @Test
    void returnsTheProcessorDeclaringTheType() {
        ProductProcessor normal = processorFor(ProductType.NORMAL);
        ProductProcessor seasonal = processorFor(ProductType.SEASONAL);
        ProductProcessorResolver resolver = new ProductProcessorResolver(List.of(normal, seasonal));

        assertThat(resolver.resolve(ProductType.NORMAL)).isSameAs(normal);
        assertThat(resolver.resolve(ProductType.SEASONAL)).isSameAs(seasonal);
    }

    @Test
    void failsLoudlyWhenATypeHasNoProcessor() {
        ProductProcessorResolver resolver = new ProductProcessorResolver(List.of(processorFor(ProductType.NORMAL)));

        assertThatThrownBy(() -> resolver.resolve(ProductType.EXPIRABLE))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("EXPIRABLE");
    }

    private static ProductProcessor processorFor(ProductType type) {
        return new ProductProcessor() {
            @Override
            public ProductType supportedType() {
                return type;
            }

            @Override
            public void process(Product product) {
                // nothing to do, this test only cares about the lookup
            }
        };
    }
}
