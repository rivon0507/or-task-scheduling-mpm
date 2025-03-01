# Task Scheduling Algorithm Using the Metra Potential Method

## Overview

This repository provides an implementation of a task scheduling algorithm based on the Metra Potential Method. It
enables users to calculate the earliest start dates, latest start dates, and the critical path for a set of tasks,
considering their durations and dependencies.

## Features

- Compute the earliest start dates for tasks.
- Identify the critical path in the project schedule.
- Calculate the latest start dates for tasks.

## Getting Started

### Prerequisites

- Java Development Kit (JDK) 21 or higher
- An IDE or text editor suitable for Java development

### Installation

To include this library in your project, add the following dependency to your build file:

#### Maven

Add the JitPack repository to your `pom.xml`:

```xml

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

Then, add the dependency:

```xml

<dependency>
    <groupId>com.github.rivon0507</groupId>
    <artifactId>or-task-scheduling-mpm</artifactId>
    <version>0.5.1</version>
</dependency>
```

#### Gradle

Add the JitPack repository to your `build.gradle`:

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}
```

Then, add the dependency:

```groovy
dependencies {
    implementation 'com.github.rivon0507:or-task-scheduling-mpm:0.5.1'
}
```

## Usage Example

Below is an example demonstrating how to use the `MetraPotentialMethod` class:

```java
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        List<String> tasks = Arrays.asList("Task1", "Task2", "Task3");
        List<Integer> durations = Arrays.asList(3, 2, 1);
        List<List<String>> predecessors = Arrays.asList(
                Collections.emptyList(),
                List.of("Task1"),
                List.of("Task1", "Task2")
        );

        MetraPotentialMethod method = new MetraPotentialMethod(tasks, durations, predecessors);

        Map<String, Integer> earliestDates = method.earliestDates();
        List<String> criticalPath = method.criticalPath();
        Map<String, Integer> latestDates = method.latestDates();

        // Output results
        System.out.println("Earliest Start Dates: " + earliestDates);
        System.out.println("Critical Path: " + criticalPath);
        System.out.println("Latest Start Dates: " + latestDates);
    }
}
```

## Contributing

For details on contributing, please refer to the [CONTRIBUTING.md](CONTRIBUTING.md) file.

## License

This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Inspired by the Metra Potential Method for task scheduling.
- Thanks to all contributors and users of this project.

