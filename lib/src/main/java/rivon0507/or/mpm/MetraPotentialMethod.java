package rivon0507.or.mpm;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnmodifiableView;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/// A class that computes critical path, latest and earliest start dates for task scheduling, using the MPM or Metra
/// Potential Method
/// To use it, simply initialise it with the task names, task durations and tasks predecessors lists, then call for
/// the available methods. The class caches the results of computations, so if you want it to recompute, call the methods
/// starting with "compute"
public class MetraPotentialMethod {
    private final Map<String, Integer> durations = new HashMap<>();
    private final Map<String, Integer> earliest = new HashMap<>();
    private final Map<String, Integer> latest = new HashMap<>();
    private final Map<String, List<String>> predecessors = new HashMap<>();
    private final Map<String, List<String>> successors = new HashMap<>();
    private final LinkedList<String> criticalPath = new LinkedList<>();

    public static final String START_TASK = "start";
    public static final String END_TASK = "end";

    /// Constructs the graph of the tasks using the provided arguments. The order of the tasks' data in the three arguments
    /// should be the same, i.e. `predecessors[i]` and `taskDurations[i]` are respectively the list of predecessors
    /// and the duration of `taskNames[i]`
    ///
    /// @param taskNames     the iterable containing the name of each task
    /// @param taskDurations the iterable containing the duration of each task
    /// @param predecessors  the iterable containing the tasks preceding each task
    public MetraPotentialMethod(
            @NotNull Iterable<String> taskNames,
            @NotNull Iterable<Integer> taskDurations,
            @NotNull Iterable<? extends Iterable<String>> predecessors) {

        initMaps(taskNames);

        Iterator<String> nameIterator = taskNames.iterator();
        Iterator<Integer> durationIterator = taskDurations.iterator();
        Iterator<? extends Iterable<String>> predecessorIterator = predecessors.iterator();

        while (nameIterator.hasNext() || durationIterator.hasNext() || predecessorIterator.hasNext()) {
            if (!(nameIterator.hasNext() == durationIterator.hasNext() == predecessorIterator.hasNext())) {
                throw new IllegalArgumentException("The number of task names, task durations and tasks predecessors do not match");
            }

            String name = nameIterator.next();
            addDuration(name, durationIterator.next());
            addPredecessors(name, predecessorIterator.next());
        }

        for (String taskName : taskNames) {
            if (!successors.containsKey(taskName) || successors.get(taskName).isEmpty()) {
                successors.put(taskName, new LinkedList<>(List.of(END_TASK)));
                this.predecessors.get(END_TASK).add(taskName);
            }
        }
    }

    /// Initializes the duration map with the start and end task, and the two dependency maps with all the task names with
    /// empty lists
    private void initMaps(@NotNull Iterable<String> taskNames) {
        durations.putAll(Map.of(
                START_TASK, 0,
                END_TASK, -1
        ));
        this.predecessors.put(START_TASK, new LinkedList<>());
        this.predecessors.put(END_TASK, new LinkedList<>());
        this.predecessors.putAll(StreamSupport.stream(taskNames.spliterator(), false).distinct()
                .collect(Collectors.toMap(
                        k -> k,
                        k -> new LinkedList<>()
                ))
        );
        this.successors.put(START_TASK, new LinkedList<>());
        this.successors.put(END_TASK, new LinkedList<>());
        this.successors.putAll(StreamSupport.stream(taskNames.spliterator(), false).distinct()
                .collect(Collectors.toMap(
                        k -> k,
                        k -> new LinkedList<>()
                ))
        );
    }

    /// Associate the provided list of predecessors to the task with the provided name
    ///
    /// @throws IllegalArgumentException if a task with the provided name is already present with predecessors
    private void addPredecessors(String name, @NotNull Iterable<String> predecessorList) {
        List<String> pre = predecessors.get(name);
        if (!pre.isEmpty()) {
            throw new IllegalArgumentException("Task " + name + " has two sets of predecessors declared");
        }
        StreamSupport.stream(predecessorList.spliterator(), false).distinct().forEach(
                taskName -> {
                    if (!successors.containsKey(taskName)) {
                        throw new IllegalArgumentException("Task " + taskName + ", declared predecessor of " + name + ", do not exist");
                    }
                    pre.add(taskName);
                    successors.get(taskName).add(name);
                }
        );
        if (pre.isEmpty()) {
            pre.add(START_TASK);
            successors.get(START_TASK).add(name);
        }
    }

    /// Adds a new task with duration
    ///
    /// @param name     the name of the task
    /// @param duration the duration of the task
    /// @throws IllegalArgumentException if a task with the provided name is already present with a different duration
    private void addDuration(String name, int duration) {
        Integer oldDuration = durations.put(name, duration);
        if (oldDuration != null && oldDuration != -1) {
            throw new IllegalArgumentException("Task " + name + " has two different durations");
        }
    }

    /// Computes the earliest dates for each task
    ///
    /// @return a map associating a task name to its corresponding earliest start date
    @UnmodifiableView
    public Map<String, Integer> computeEarliestDates() {
        earliest.clear();
        earliest.put(START_TASK, 0);
        Set<String> seen = new HashSet<>();
        Queue<String> queue = new LinkedList<>(successors.get(START_TASK));

        while (!queue.isEmpty()) {
            String taskName = queue.poll();
            if (!seen.contains(taskName)) {
                try {
                    computeEarliest(taskName);

                    seen.add(taskName);
                    queue.addAll(successors.get(taskName));
                } catch (NoSuchElementException e) {
                    queue.add(taskName);
                }
            }
        }
        return Collections.unmodifiableMap(earliest);
    }

    /// Computes the critical path alongside the latest dates. It also computes the earliest dates if it was not done
    /// beforehand.
    ///
    /// @return a list containing the critical path (sorted). You can call to {@code latestDates()} afterward to get the
    /// latest start dates
    @UnmodifiableView
    public List<String> computeCriticalPath() {
        criticalPath.clear();
        latest.clear();
        if (earliest.isEmpty()) {
            computeEarliestDates();
        }
        latest.put(END_TASK, earliest.get(END_TASK));
        criticalPath.push(END_TASK);
        Set<String> seen = new HashSet<>();
        Queue<String> queue = new LinkedList<>(predecessors.get(END_TASK));

        while (!queue.isEmpty()) {
            String taskName = queue.poll();
            if (!seen.contains(taskName)) {
                try {
                    computeLatest(taskName);
                    if (Objects.equals(earliest.get(taskName), latest.get(taskName))) {
                        criticalPath.addFirst(taskName);
                    }

                    seen.add(taskName);
                    queue.addAll(predecessors.get(taskName));
                } catch (NoSuchElementException e) {
                    queue.add(taskName);
                }
            }
        }

        return Collections.unmodifiableList(criticalPath);
    }

    /// Returns the cached critical path and compute it if it is not yet cached
    @UnmodifiableView
    public List<String> criticalPath() {
        if (criticalPath.isEmpty()) {
            computeCriticalPath();
        }
        return Collections.unmodifiableList(criticalPath);
    }

    /// Returns the cached latest start dates and compute it if it is not yet cached
    @UnmodifiableView
    public Map<String, Integer> latestDates() {
        if (latest.isEmpty()) {
            computeCriticalPath();
        }
        return Collections.unmodifiableMap(latest);
    }

    /// Returns the cached earliest start dates and compute it if it is not yet cached
    @UnmodifiableView
    public Map<String, Integer> earliestDates() {
        if (earliest.isEmpty()) {
            computeEarliestDates();
        }
        return Collections.unmodifiableMap(earliest);
    }

    @UnmodifiableView
    public Map<String, List<String>> getPredecessors() {
        return Collections.unmodifiableMap(predecessors);
    }

    @UnmodifiableView
    public Map<String, List<String>> getSuccessors() {
        return Collections.unmodifiableMap(successors);
    }

    @UnmodifiableView
    public Map<String, Integer> getDurations() {
        return Collections.unmodifiableMap(durations);
    }

    /// Computes the minimum of the latest start time of the successors of the task.
    ///
    /// @throws IllegalStateException if the task has no successor
    /// @throws NoSuchElementException if one or more of this task's successor doesn't have a latest start date yet
    private void computeLatest(@NotNull String task) throws NoSuchElementException {
        int res = successors.get(task)
                          .stream()
                          .map(key -> Optional.ofNullable(latest.get(key)).orElseThrow())
                          .min(Integer::compareTo)
                          .orElseThrow(() -> new IllegalStateException("Task " + task + " has no successor"))
                  - durations.get(task);

        latest.put(task, res);
    }

    /// Computes the maximum of the earliest start time of the predecessors of the task.
    ///
    /// @throws IllegalStateException  if the task has no predecessor
    /// @throws NoSuchElementException if one or more of this task's predecessor doesn't have an earliest start date yet
    private void computeEarliest(@NotNull String task) throws NoSuchElementException {
        int res = predecessors.get(task)
                .stream()
                .map(t -> Optional.ofNullable(earliest.get(t)).orElseThrow() + durations.get(t))
                .max(Integer::compareTo)
                .orElseThrow(() -> new IllegalStateException("Task " + task + " has no predecessor"));

        earliest.put(task, res);
    }
}
