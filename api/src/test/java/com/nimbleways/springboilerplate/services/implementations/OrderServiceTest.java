package com.nimbleways.springboilerplate.services.implementations;

import com.nimbleways.springboilerplate.dto.product.ProcessOrderResponse;
import com.nimbleways.springboilerplate.entities.Order;
import com.nimbleways.springboilerplate.entities.Product;
import com.nimbleways.springboilerplate.enumerations.ProductTypeEnum;
import com.nimbleways.springboilerplate.exceptions.NotFoundException;
import com.nimbleways.springboilerplate.repositories.OrderRepository;
import com.nimbleways.springboilerplate.utils.Annotations.UnitTest;

import java.util.Optional;
import java.util.Set;

@ExtendWith(SpringExtension.class)
@UnitTest
class OrderServiceTest {
    Order order;
    Product product;
    @Mock
    private ProductService productService;
    @Mock
    private OrderRepository orderRepository;
    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        Product product = new Product(1, 10, 10, ProductTypeEnum.NORMAL, "RJ45 Cable", null, null, null);
        Order order = new Order();
        order.setId(1L);
        order.setItems(Set.of(product));
    }

    @Test
    void testProcessOrder() {
        when(orderRepository.findById(1L)).thenReturn(order);
        doNothing().when(productService).processNormalProduct(any());
        doNothing().when(productService).massiveSave(anyList());

        ProcessOrderResponse result = orderService.processOrder(order.getId());

        assertEquals(1L, result.getId());
    }

    @Test
    void testProcessOrderOrderNotFound() {
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        NotFoundException exception = assertThrows(NotFoundException.class, () -> orderService.processOrder(1L));

        assertEquals("Order not found", exception.getMessage());
    }
}
