# Quoridor TDD Checklist

---

## Domain Objects

### Direction
- [x] NORTH has row delta +1, col delta 0
- [x] SOUTH has row delta -1, col delta 0
- [x] EAST has row delta 0, col delta +1
- [x] WEST has row delta 0, col delta -1

### Position
- [x] Create position with valid values (0-8, 0-8)
- [x] Reject position with negative row
- [x] Reject position with row > 8
- [x] Reject position with negative column
- [x] Reject position with column > 8
- [x] Move north from (4,4) gives (5,4)
- [x] Move south from (4,4) gives (3,4)
- [x] Move east from (4,4) gives (4,5)
- [x] Move west from (4,4) gives (4,3)
- [x] Reject move north from top row (row 8)
- [x] Reject move south from bottom row (row 0)
- [x] Reject move west from leftmost column (col 0)
- [x] Reject move east from rightmost column (col 8)
- [ ] Two positions with same row and col are equal
- [ ] Two positions with different row are not equal
- [ ] Two positions with different col are not equal

### WallPosition
- [x] Create wall position with valid values (0-7, 0-7)
- [x] Reject wall position with negative row
- [x] Reject wall position with row > 7
- [x] Reject wall position with negative column
- [x] Reject wall position with column > 7
- [ ] Two wall positions with same coordinates are equal
- [ ] Two wall positions with different coordinates are not equal

### WallOrientation
- [ ] HORIZONTAL orientation exists
- [ ] VERTICAL orientation exists

### Wall
- [ ] Create wall with position and orientation
- [ ] Wall knows its position
- [ ] Wall knows its orientation
- [ ] Two walls with same position and orientation are equal
- [ ] Two walls with same position but different orientation are not equal
- [ ] Two walls with different position but same orientation are not equal

