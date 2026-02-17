# Quoridor

[![CI](https://github.com/dellimone/Quoridor/actions/workflows/build.yml/badge.svg)](https://github.com/dellimone/Quoridor/actions/workflows/build.yml)
[![Gradle](https://img.shields.io/badge/gradle-8.11-blue?logo=gradle)](https://gradle.org/)
[![Java](https://img.shields.io/badge/java-24-red?logo=openjdk)](https://openjdk.java.net/)

A Java implementation of Quoridor board game for the Sofware Development Methods Exam project.

## Architecture

This project follows a **layered architecture** with clear separation of concerns:

- **Domain**: Data structures (player, wall, board, ...)
- **Logic**: Rule validators (pawn move, wall placement)
- **Engine**: Game orchestration (move execution, undo)
- **Controller**: UI-to-game translation
- **View**: Humble dialog pattern (rendering only)

For a high-level overview of the system see [docs/architecture.md](docs/architecture.md). Implementation detail can be found in the [Project Wiki](https://github.com/dellimone/Quoridor/wiki)

## Project Structure

The project is organized into several packages, each with a distinct responsibility:

```bash
src
├── main/java/it/units/quoridor
│   ├── controller/      # Glue between View and Engine
│   ├── domain/          # Core entities (Board, Player, Wall, etc.)
│   ├── engine/          # Game state management and move execution
│   ├── logic/           # Complex behavioral logic
│   │   ├── pathFinder/  # BFS algorithms for goal reachability
│   │   ├── rules/       # Game-specific win conditions and setup
│   │   └── validation/  # Move and wall placement legality checks
│   └── view/            # Swing-based GUI components and ViewModels
└── test/java/it/units/quoridor
    └── ...              # Comprehensive unit and integration tests
```
## Building and Running

### Prerequisites

- Java
- Gradle

### Build Commands

To build the project, execute the following command:
```bash
./gradlew build
```
This will compile the Java source code.

### Running the game

To run the Quoridor game, execute the following command:
```bash
./gradlew run
```
This will launch the Swing-based graphical user interface.

## Documentation

- [Architecture Details](docs/architecture.md) - High level description of architecture
- [Game Rules](docs/rules.md) - Complete Quoridor rules reference
- [Behaviors](docs/behaviors.md) - Technical functional requirements
- [Test Scenarios (Plays)](docs/plays.md) - Integration test cases and expected outcomes.
- [Development Checklist](docs/todo.md) – Task tracking
- [Contributing Guide](docs/CONTRIBUTING.md) - Commit message reference

## Technologies

- **Language**: Java 24
- **Build Tool**: Gradle 8.11
- **Testing**: JUnit 5, Mockito
- **GUI**: Swing