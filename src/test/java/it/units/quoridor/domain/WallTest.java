package it.units.quoridor.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WallTest {

    @Test
    void createWallWithPositionAndOrientation() {
        WallPosition wallPosition = new WallPosition(1,2);
        Wall wall = new Wall(wallPosition, WallOrientation.HORIZONTAL);

        assertEquals(WallOrientation.HORIZONTAL, wall.orientation());
        assertEquals(wallPosition, wall.position());
    }

    @Test
    void wallsWithSamePositionAndOrientationAreEqual() {
        WallPosition wallPosition = new WallPosition(1,2);
        assertEquals(new Wall(wallPosition, WallOrientation.HORIZONTAL),
                new Wall(wallPosition, WallOrientation.HORIZONTAL));
    }

    @Test
    void wallsWithDifferentPositionOrOrientationAreNotEqual() {
        WallPosition wallPositionA = new WallPosition(1,2);
        WallPosition wallPositionB = new WallPosition(2,1);

        assertNotEquals(
                new Wall(wallPositionA, WallOrientation.HORIZONTAL),
                new Wall(wallPositionB, WallOrientation.HORIZONTAL));

        assertNotEquals(
                new Wall(wallPositionA, WallOrientation.HORIZONTAL),
                new Wall(wallPositionA, WallOrientation.VERTICAL));

    }


}