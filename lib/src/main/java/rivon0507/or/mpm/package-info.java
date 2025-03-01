/// # Package `rivon0507.or.mpm`
///
/// Provides a classes the implementation of a task scheduling algorithm using the Metra Potential Method.
///
/// ## Overview
///
/// The primary class in this package is {@link rivon0507.or.mpm.MetraPotentialMethod}, which allows
/// users to calculate earliest start dates, latest start dates, and the critical
/// path of a set of tasks based on their durations and predecessors.
/// ## Basic Usage
/// ```java
/// List<String> TASKS = Arrays.asList("Task1", "Task2", "Task3");
/// List<Integer> DURATIONS = Arrays.asList(3, 2, 1);
/// List<List<String>> PREDECESSORS = Arrays.asList(
///     Collections.emptyList(),
///     Arrays.asList("Task1"),
///     Arrays.asList("Task1", "Task2")
///);
/// MetraPotentialMethod method = new MetraPotentialMethod(TASKS, DURATIONS, PREDECESSORS);
/// Map<String, Integer> earliestDates = method.earliestDates();
/// List<String> criticalPath = method.criticalPath();
/// List<String> latestDates = method.latestDates();
///```
/// In this example:
/// - `TASKS` is the list of tasks (strings).
/// - `DURATIONS` is the list of durations (integers) where `DURATIONS[i]` is the duration of `TASK[i]`.
/// - `PREDECESSORS` is the list of lists of strings where `PREDECESSORS[i]` are the predecessors of `TASKS[i]`.
/// The `MetraPotentialMethod` class provides methods to compute:
/// - `earliestDates()` - Returns a map of task names to their earliest start dates.
/// - `criticalPath()` - Returns a list of task names that form the critical path.
/// - `latestDates()` - Returns a list of task names to their latest start dates.
///
/// @see rivon0507.or.mpm.MetraPotentialMethod
package rivon0507.or.mpm;