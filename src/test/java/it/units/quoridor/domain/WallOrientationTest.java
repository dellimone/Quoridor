package it.units.quoridor.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WallOrientationTest {

    @Test
    void horizontalOrientationExist() {
        assertNotNull(WallOrientation.HORIZONTAL);
    }

    @Test
    void verticalOrientationExist() {
        assertNotNull(WallOrientation.VERTICAL);
    }

}