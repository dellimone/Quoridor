# Quoridor TDD Checklist

---

## Domain Objects

### Position
- [ ] Create position with valid values (0-8, 0-8)
- [ ] Reject position with negative row
- [ ] Reject position with row > 8
- [ ] Reject position with negative column
- [ ] Reject position with column > 8
- [ ] Move north from (4,4) gives (3,4)
- [ ] Move south from (4,4) gives (5,4)
- [ ] Move east from (4,4) gives (4,5)
- [ ] Move west from (4,4) gives (4,3)
