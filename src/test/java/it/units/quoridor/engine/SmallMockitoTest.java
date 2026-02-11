package it.units.quoridor.engine;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SmallMockitoTest {

    // fake interface for the test
    interface DummyService {
        int value();
    }

    // mockito proxy
    @Mock
    DummyService dummy;

    @Test
    void mockitoWorks() {

        // when we call dummy.value() return 42
        when(dummy.value()).thenReturn(42);
        int result = dummy.value(); // save the result

        // assert whether it is correct
        assertEquals(42, result);
        verify(dummy).value();
    }
}
