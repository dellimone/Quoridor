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
- [ ] Move north from (4,4) gives (3,4)
- [ ] Move south from (4,4) gives (5,4)
- [ ] Move east from (4,4) gives (4,5)
- [ ] Move west from (4,4) gives (4,3)

